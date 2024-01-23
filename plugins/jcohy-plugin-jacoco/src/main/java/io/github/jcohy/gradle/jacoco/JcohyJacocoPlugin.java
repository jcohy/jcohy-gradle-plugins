package io.github.jcohy.gradle.jacoco;

import java.util.Objects;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/23 14:42
 * @since 2024.0.1
 */
public class JcohyJacocoPlugin implements Plugin<Project> {
	private static final String JACOCO_TOOL_VERSION_PROPERTY = "jacocoToolVersion";
	private static final String DEFAULT_JACOCO_TOOL_VERSION = "0.8.7";

	@Override
	public void apply(Project project) {
		project.getPlugins().withType(JavaPlugin.class, (javaPlugin) -> {
			project.getPluginManager().apply(JacocoPlugin.class);
			project.getTasks().getByName("check").dependsOn(project.getTasks().getByName("jacocoTestReport"));

			JacocoPluginExtension jacoco = project.getExtensions().getByType(JacocoPluginExtension.class);
			// NOTE: See gradle.properties#jacocoToolVersion for actual version number
			jacoco.setToolVersion(getJacocoToolVersion(project));
		});
	}

	private static String getJacocoToolVersion(Project project) {
		String jacocoToolVersion = DEFAULT_JACOCO_TOOL_VERSION;
		if (project.hasProperty(JACOCO_TOOL_VERSION_PROPERTY)) {
			jacocoToolVersion = Objects.requireNonNull(project.findProperty(JACOCO_TOOL_VERSION_PROPERTY)).toString();
		}
		return jacocoToolVersion;
	}
}

