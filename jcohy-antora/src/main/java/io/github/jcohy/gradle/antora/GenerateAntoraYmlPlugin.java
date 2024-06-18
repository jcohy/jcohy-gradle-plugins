package io.github.jcohy.gradle.antora;

import org.antora.gradle.AntoraExtension;
import org.antora.gradle.AntoraPlugin;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description: https://resources.jcohy.com/antora/ui-bundle.zip
 *
 * @author jiac
 * @version 2024.0.1 2024/1/16 11:22
 * @since 2024.0.1
 */
public class GenerateAntoraYmlPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getPlugins().apply(AntoraPlugin.class);
		configurationAntora(project);
		configurationGenerateAntoraYmlTask(project);
	}

	private void configurationAntora(Project project) {
		AntoraExtension antoraExtension = project.getExtensions().getByType(AntoraExtension.class);
		antoraExtension.setPlaybook("cached-antora-playbook.yml");
		antoraExtension.getPlaybookProvider().getRepository().convention("jcohy/antora");
		antoraExtension.getPlaybookProvider().getBranch().convention(project.getName()+ "-build");
		antoraExtension.getPlaybookProvider().getPath().convention("lib/antora/templates/per-branch-antora-playbook.yml");
		antoraExtension.getPlaybookProvider().getCheckLocalBranch().convention(true);
		antoraExtension.setOptions(Map.of("clean",true,"fetch",!project.getGradle().getStartParameter().isOffline(),"stacktrace",true));
	}


	private void configurationGenerateAntoraYmlTask(Project project) {
		project.getTasks().register("generateAntoraYml", GenerateAntoraYmlTask.class, generateAntoraYmlTask -> {
            generateAntoraYmlTask.setGroup("Documentation");
            generateAntoraYmlTask.setDescription("Generates an antora.yml file with information from the build");
            String name = project.getName();
            generateAntoraYmlTask.getComponentName().convention(name);
            project.getVersion();
            String projectVersion = project.getVersion().toString();
            if (!Project.DEFAULT_VERSION.equals(projectVersion)) {
                generateAntoraYmlTask.getVersion().convention(projectVersion);
            }
            RegularFile defaultBaseAntoraYmlFile = project.getLayout().getProjectDirectory().file("antora.yml");
            if (defaultBaseAntoraYmlFile.getAsFile().exists()) {
                generateAntoraYmlTask.getBaseAntoraYmlFile().convention(defaultBaseAntoraYmlFile);
            }
            generateAntoraYmlTask.getAsciidocAttributes().convention(addAttribute(project));
            generateAntoraYmlTask.getOutputFile().convention(project.getLayout().getBuildDirectory().file("generated-antora-resources/antora.yml"));
        });
	}

	private Map<String,String> addAttribute(Project project) {
		Map<String,String> attributes = new HashMap<>();
		attributes.put("version",(String) project.getVersion());
		attributes.put("image-resource", project.getBuildDir() + "/docs/src/antora/images");
		attributes.put("docs-java", project.getProjectDir() + "/src/main/java");
		attributes.put("docs-kotlin", project.getProjectDir() + "/src/main/kotlin");
		attributes.put("docs-groovy", project.getProjectDir() + "/src/main/groovy");
		attributes.put("docs-url", "https://docs.jcohy.com");
		attributes.put("resource-url", "https://resources.jcohy.com");
		attributes.put("software-url", "https://software.jcohy.com");
		attributes.put("study-url", "https://study.jcohy.com");
		attributes.put("project-url", "https://project.jcohy.com");
		return attributes;
	}
}
