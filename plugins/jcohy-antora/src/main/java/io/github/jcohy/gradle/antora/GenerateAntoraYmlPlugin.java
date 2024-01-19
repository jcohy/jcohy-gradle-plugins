package io.github.jcohy.gradle.antora;

import org.antora.gradle.AntoraPlugin;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFile;

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

		configurationGenerateAntoraYmlTask(project);



	}

	private void configurationGenerateAntoraYmlTask(Project project) {
		project.getTasks().register("generateAntoraYml", GenerateAntoraYmlTask.class, new Action<GenerateAntoraYmlTask>() {
			@Override
			public void execute(GenerateAntoraYmlTask generateAntoraYmlTask) {
				generateAntoraYmlTask.setGroup("Documentation");
				generateAntoraYmlTask.setDescription("Generates an antora.yml file with information from the build");
				String name = project.getName();
				generateAntoraYmlTask.getComponentName().convention(name);
				String projectVersion = project.getVersion() == null ? null : project.getVersion().toString();
				if (!Project.DEFAULT_VERSION.equals(projectVersion)) {
					generateAntoraYmlTask.getVersion().convention(projectVersion);
				}
				RegularFile defaultBaseAntoraYmlFile = project.getLayout().getProjectDirectory().file("antora.yml");
				if (defaultBaseAntoraYmlFile.getAsFile().exists()) {
					generateAntoraYmlTask.getBaseAntoraYmlFile().convention(defaultBaseAntoraYmlFile);
				}
				generateAntoraYmlTask.getOutputFile().convention(project.getLayout().getBuildDirectory().file("generated-antora-resources/antora.yml"));
			}
		});

	}
}
