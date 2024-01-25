package io.github.jcohy.gradle.build;


import com.gradle.publish.PublishPlugin;
import io.github.jcohy.gradle.build.convention.*;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugins.signing.SigningPlugin;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2024 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2024.0.1 2023/2/16 17:15
 * @since 1.0.0
 */
public class GradlePluginModulePlugins implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		PluginContainer plugins = project.getPlugins();
		plugins.apply(JavaModulePlugins.class);

		// PublishingPlugin 自动应用 Java Gradle Development Plugin (java-gradle-plugin) 和 Maven Publish Plugin (maven-publish).
		// Java Gradle Plugin (java-gradle-plugin) 自动应用 Java Library(java-library),并添加 api gradleApi() 依赖
		// https://docs.gradle.org/current/userguide/java_gradle_plugin.html
		plugins.apply(PublishPlugin.class);
		configureCommonAttributes(project);
	}

	private void configureCommonAttributes(Project project) {
		project.getPlugins().withType(PublishPlugin.class,publishPlugin -> {
			GradlePluginDevelopmentExtension extension = project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
			extension.getWebsite().set("https://github.com/jcohy/jcohy-gradle-plugins");
			extension.getVcsUrl().set("https://github.com/jcohy/jcohy-gradle-plugins");
		});
	}

}
