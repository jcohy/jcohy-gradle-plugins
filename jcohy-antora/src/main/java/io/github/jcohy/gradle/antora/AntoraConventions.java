package io.github.jcohy.gradle.antora;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gradle.node.NodeExtension;
import com.github.gradle.node.npm.task.NpmInstallTask;

import org.antora.gradle.AntoraPlugin;
import org.antora.gradle.AntoraTask;
import org.gradle.StartParameter;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AntoraConventions {
    private static final String DEPENDENCIES_PATH = ":bom";


    private static final String ANTORA_SOURCE_DIR = "src/docs/antora";

    private static final List<String> NAV_FILES = List.of("nav.adoc", "local-nav.adoc");

    void apply(Project project) {
        project.getPlugins().withType(AntoraPlugin.class, (antoraPlugin) -> apply(project, antoraPlugin));
    }

    private void apply(Project project, AntoraPlugin antoraPlugin) {

        project.getPlugins().apply(GenerateAntoraYmlPlugin.class);
        TaskContainer tasks = project.getTasks();

        GenerateAntoraPlaybook generateAntoraPlaybookTask = tasks.create("generateAntoraPlaybook",
            GenerateAntoraPlaybook.class);
        configureGenerateAntoraPlaybookTask(project, generateAntoraPlaybookTask);
        Copy copyAntoraPackageJsonTask = tasks.create("copyAntoraPackageJson", Copy.class);
        configureCopyAntoraPackageJsonTask(project, copyAntoraPackageJsonTask);
        NpmInstallTask npmInstallTask = tasks.create("antoraNpmInstall", NpmInstallTask.class);
        configureNpmInstallTask(project, npmInstallTask, copyAntoraPackageJsonTask);
        tasks.withType(GenerateAntoraYmlTask.class, (generateAntoraYmlTask) -> configureGenerateAntoraYmlTask(project,
            generateAntoraYmlTask));
        tasks.withType(AntoraTask.class,
            (antoraTask) -> configureAntoraTask(project, antoraTask, npmInstallTask, generateAntoraPlaybookTask));
        project.getExtensions()
            .configure(NodeExtension.class, (nodeExtension) -> configureNodeExtension(project, nodeExtension));
    }

    /**
     * configuration GenerateAntoraPlaybookTask
     * @param project project
     * @param generateAntoraPlaybookTask generateAntoraPlaybookTask
     */
    private void configureGenerateAntoraPlaybookTask(Project project,
        GenerateAntoraPlaybook generateAntoraPlaybookTask) {
        File nodeProjectDir = getNodeProjectDir(project.getBuildDir());
        generateAntoraPlaybookTask.getOutputFile().set(new File(nodeProjectDir, "antora-playbook.yml"));
    }

    private void configureCopyAntoraPackageJsonTask(Project project, Copy copyAntoraPackageJsonTask) {
        copyAntoraPackageJsonTask
            .from(project.getRootProject().file("antora"), (spec) -> spec.include("package.json", "package-lock.json"))
            .into(getNodeProjectDir(project.getBuildDir()));
    }

    private void configureNpmInstallTask(Project project, NpmInstallTask npmInstallTask, Copy copyAntoraPackageJson) {
        npmInstallTask.dependsOn(copyAntoraPackageJson);
        Map<String, String> environment = new HashMap<>();
        environment.put("npm_config_omit", "optional");
        environment.put("npm_config_update_notifier", "false");
        npmInstallTask.getEnvironment().set(environment);
        npmInstallTask.getNpmCommand().set(List.of("ci", "--silent", "--no-progress"));
    }

    private void configureGenerateAntoraYmlTask(Project project, GenerateAntoraYmlTask generateAntoraYmlTask) {
        generateAntoraYmlTask.getOutputs().doNotCacheIf("getAsciidocAttributes() changes output", (task) -> true);
        generateAntoraYmlTask.setProperty("componentName", "boot");
        generateAntoraYmlTask.setProperty("outputFile",
            new File(project.getBuildDir(), "generated/docs/antora-yml/antora.yml"));
        generateAntoraYmlTask.setProperty("yml", getDefaultYml(project));
        generateAntoraYmlTask.doFirst((task) -> generateAntoraYmlTask.getAsciidocAttributes()
            .putAll(project.provider(() -> getAsciidocAttributes(project))));
    }

    private Map<String, ?> getDefaultYml(Project project) {
        String navFile = null;
        for (String candidate : NAV_FILES) {
            if (project.file(ANTORA_SOURCE_DIR + "/" + candidate).exists()) {
                Assert.state(navFile == null, "Multiple nav files found");
                navFile = candidate;
            }
        }
        Map<String, Object> defaultYml = new LinkedHashMap<>();
        defaultYml.put("title", "Spring Boot");
        if (navFile != null) {
            defaultYml.put("nav", List.of(navFile));
        }
        return defaultYml;
    }

    private Map<String, String> getAsciidocAttributes(Project project) {
        AntoraAsciidocAttributes attributes = new AntoraAsciidocAttributes(project);
        return attributes.get();
    }

    private void configureAntoraTask(Project project, AntoraTask antoraTask, NpmInstallTask npmInstallTask,
        GenerateAntoraPlaybook generateAntoraPlaybookTask) {
        antoraTask.setGroup("Documentation");
        antoraTask.dependsOn(npmInstallTask, generateAntoraPlaybookTask);
        antoraTask.setPlaybook("antora-playbook.yml");
        antoraTask.setUiBundleUrl(getUiBundleUrl(project));
        antoraTask.getArgs().set(project.provider(() -> getAntoraNpxArs(project, antoraTask)));
        project.getPlugins()
            .withType(JavaBasePlugin.class,
                (javaBasePlugin) -> project.getTasks()
                    .getByName(JavaBasePlugin.CHECK_TASK_NAME)
                    .dependsOn(antoraTask));
    }

    private List<String> getAntoraNpxArs(Project project, AntoraTask antoraTask) {
        logWarningIfNodeModulesInUserHome(project);
        StartParameter startParameter = project.getGradle().getStartParameter();
        boolean showStacktrace = startParameter.getShowStacktrace().name().startsWith("ALWAYS");
        boolean debugLogging = project.getGradle().getStartParameter().getLogLevel() == LogLevel.DEBUG;
        String playbookPath = antoraTask.getPlaybook();
        List<String> arguments = new ArrayList<>();
        arguments.addAll(List.of("--package", "@antora/cli"));
        arguments.add("antora");
        arguments.addAll((!showStacktrace) ? Collections.emptyList() : List.of("--stacktrace"));
        arguments.addAll((!debugLogging) ? List.of("--quiet") : List.of("--log-level", "all"));
        arguments.addAll(List.of("--ui-bundle-url", antoraTask.getUiBundleUrl()));
        arguments.add(playbookPath);
        return arguments;
    }

    private void logWarningIfNodeModulesInUserHome(Project project) {
        if (new File(System.getProperty("user.home"), "node_modules").exists()) {
            project.getLogger()
                .warn("Detected the existence of $HOME/node_modules. This directory is "
                    + "not compatible with this plugin. Please remove it.");
        }
    }

    private String getUiBundleUrl(Project project) {
        try {
            File packageJson = project.getRootProject().file("antora/package.json");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<?, ?> json = objectMapper.readerFor(Map.class).readValue(packageJson);
            Map<?, ?> config = (json != null) ? (Map<?, ?>) json.get("config") : null;
            String url = (config != null) ? (String) config.get("ui-bundle-url") : null;
            Assert.state(StringUtils.hasText(url.toString()), "package.json has not ui-bundle-url config");
            return url;
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void configureNodeExtension(Project project, NodeExtension nodeExtension) {
        File buildDir = project.getBuildDir();
        nodeExtension.getWorkDir().set(buildDir.toPath().resolve(".gradle/nodejs").toFile());
        nodeExtension.getNpmWorkDir().set(buildDir.toPath().resolve(".gradle/npm").toFile());
        nodeExtension.getNodeProjectDir().set(getNodeProjectDir(buildDir));
    }

    private File getNodeProjectDir(File buildDir) {
        return buildDir.toPath().resolve(".gradle/nodeproject").toFile();
    }
}
