package io.github.jcohy.gradle.antora;

import static org.gradle.internal.impldep.com.amazonaws.util.EC2MetadataUtils.getData;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Task to generate a local Antora playbook.
 */
public abstract class GenerateAntoraPlaybook extends DefaultTask {
    private static final String ANTORA_SOURCE_DIR = "src/docs/antora";

    private static final String GENERATED_DOCS = "build/generated/docs/";

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @Input
    public abstract Property<String> getContentSourceConfiguration();

    @Input
    @Optional
    public abstract ListProperty<String> getXrefStubs();

    @Input
    @Optional
    public abstract MapProperty<String, String> getAlwaysInclude();

    public GenerateAntoraPlaybook() {
        setGroup("Documentation");
        setDescription("Generates an Antora playbook.yml file for local use");
        getOutputFile().convention(getProject().getLayout().getBuildDirectory().file("generated/docs/antora-playbook/antora-playbook.yml"));
        getContentSourceConfiguration().convention("antoraContent");
    }

    @TaskAction
    public void writePlaybookYml() throws IOException {
        File file = getOutputFile().get().getAsFile();
        file.getParentFile().mkdirs();
        try (FileWriter out = new FileWriter(file)) {
            createYaml().dump(getData(), out);
        }
    }

    @Input
    final Map<String, Object> getData() throws IOException {
        Map<String, Object> data = loadPlaybookTemplate();
        // add extensions
        addExtensions(data);
        // add document source
        addSources(data);
        //
        addDir(data);
        return data;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadPlaybookTemplate() throws IOException {
        try (InputStream resource = getClass().getResourceAsStream("antora-playbook-template.yml")) {
            return createYaml().loadAs(resource, LinkedHashMap.class);
        }
    }

    /**
     * configuration antora-playbook.yml extensions. this contains two configuration.
     * antora:
     *   extensions:
     * asciidoc:
     *   extensions:
     * @param data data
     */
    @SuppressWarnings("unchecked")
    private void addExtensions(Map<String, Object> data) {
        Map<String, Object> antora = (Map<String, Object>) data.get("antora");
        antora.put("extensions", Extensions.antora((extensions) -> {
            extensions.xref((xref) -> xref.stub(getXrefStubs().getOrElse(Collections.emptyList())));
            extensions.zipContentsCollector((zipContentsCollector) -> {
                zipContentsCollector.versionFile("gradle.properties");
                String locationName = getProject().getName() + "-${version}-${name}-${classifier}.zip";
                Path antoraContent = getRelativeProjectPath()
                    .resolve(GENERATED_DOCS + "antora-content/" + locationName);
                Path antoraDependencies = getRelativeProjectPath()
                    .resolve(GENERATED_DOCS + "antora-dependencies-content/" + locationName);
                zipContentsCollector.locations(antoraContent, antoraDependencies);
                zipContentsCollector.alwaysInclude(getAlwaysInclude().getOrNull());
            });
            extensions.rootComponent((rootComponent) -> rootComponent.name("boot"));
        }));
        Map<String, Object> asciidoc = (Map<String, Object>) data.get("asciidoc");
        asciidoc.put("extensions", Extensions.asciidoc());
    }

    /**
     * configuration content.sources.
     * content:
     *   sources:
     *   - url: .\..\..\..\..\..
     *     branches: HEAD
     *     version: unspecified
     *     start_paths:
     *     - project\src\docs\antora
     *  start_path load form antoraContent gradle configuration. from src/docs/antora in the specified project
     * @param data data
     */
    private void addSources(Map<String, Object> data) {
        List<Map<String, Object>> contentSources = getList(data, "content.sources");
        contentSources.add(createContentSource());
    }

    private Map<String, Object> createContentSource() {
        Map<String, Object> source = new LinkedHashMap<>();
        Path playbookPath = getOutputFile().get().getAsFile().toPath().getParent();
        Path antoraSrc = getProjectPath(getProject()).resolve(ANTORA_SOURCE_DIR);
        StringBuilder url = new StringBuilder(".");
        relativizeFromRootProject(playbookPath).normalize().forEach((path) -> url.append(File.separator).append(".."));
        source.put("url", url.toString());
        source.put("branches", "HEAD");
        source.put("version", getProject().getVersion().toString());
        Set<String> startPaths = new LinkedHashSet<>();
        addAntoraContentStartPaths(startPaths);
        startPaths.add(relativizeFromRootProject(antoraSrc).toString());
        source.put("start_paths", startPaths.stream().toList());
        return source;
    }

    private void addAntoraContentStartPaths(Set<String> startPaths) {
        Configuration configuration = getProject().getConfigurations().findByName("antoraContent");
        if (configuration != null) {
            for (ProjectDependency dependency : configuration.getAllDependencies().withType(ProjectDependency.class)) {
                Path path = dependency.getDependencyProject().getProjectDir().toPath();
                startPaths.add(relativizeFromRootProject(path).resolve(ANTORA_SOURCE_DIR).toString());
            }
        }
    }

    /**
     * configuration output.
     * output:
     *   dir: .\..\..\..\site
     * @param data data
     */
    private void addDir(Map<String, Object> data) {
        Path playbookDir = toRealPath(getOutputFile().get().getAsFile().toPath()).getParent();
        Path outputDir = toRealPath(getProject().getBuildDir().toPath().resolve("site"));
        data.put("output", Map.of("dir", "." + File.separator + playbookDir.relativize(outputDir).toString()));
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getList(Map<String, Object> data, String location) {
        return (List<T>) get(data, location);
    }

    @SuppressWarnings("unchecked")
    private Object get(Map<String, Object> data, String location) {
        Object result = data;
        String[] keys = location.split("\\.");
        for (String key : keys) {
            result = ((Map<String, Object>) result).get(key);
        }
        return result;
    }

    /**
     * create yaml file options
     * @return /
     */
    private Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        return new Yaml(options);
    }

    private Path getRelativeProjectPath() {
        return relativizeFromRootProject(getProjectPath(getProject()));
    }

    private Path relativizeFromRootProject(Path subPath) {
        Path rootProjectPath = getProjectPath(getProject().getRootProject());
        return rootProjectPath.relativize(subPath).normalize();
    }

    private Path getProjectPath(Project project) {
        return toRealPath(project.getProjectDir().toPath());
    }

    private Path toRealPath(Path path) {
        try {
            return Files.exists(path) ? path.toRealPath() : path;
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
