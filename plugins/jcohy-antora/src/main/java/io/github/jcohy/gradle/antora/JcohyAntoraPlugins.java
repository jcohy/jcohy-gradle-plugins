package io.github.jcohy.gradle.antora;

import org.antora.gradle.AntoraExtension;
import org.antora.gradle.AntoraPlaybookProvider;
import org.antora.gradle.AntoraPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/19 8:46
 * @since 2024.0.1
 */
public class JcohyAntoraPlugins implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPlugins().apply(AntoraPlugin.class);
		AntoraExtension extension = project.getExtensions().getByType(AntoraExtension.class);
		AntoraPlaybookProvider playbookProvider = extension.getPlaybookProvider();
	}
}
