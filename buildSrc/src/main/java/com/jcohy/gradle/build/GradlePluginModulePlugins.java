package com.jcohy.gradle.build;


import com.gradle.publish.PublishPlugin;
import com.jcohy.gradle.build.convention.JavaConvention;
import com.jcohy.gradle.build.convention.MavenPublishConvention;
import com.jcohy.gradle.build.convention.NexusPublishingConvention;
import com.jcohy.gradle.build.convention.SigningPublishingConvention;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugins.signing.SigningPlugin;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/2/16 17:15
 * @since 1.0.0
 */
public class GradlePluginModulePlugins implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		new JavaConvention().apply(project);
		new MavenPublishConvention().apply(project);
		new NexusPublishingConvention().apply(project);
		new SigningPublishingConvention().apply(project);

		if(project.getName().startsWith("jcohy-")) {
			PluginContainer plugins = project.getPlugins();
			// PublishingPlugin 自动应用 Java Gradle Development Plugin (java-gradle-plugin) 和 Maven Publish Plugin (maven-publish).
			// Java Gradle Plugin (java-gradle-plugin) 自动应用 Java Library(java-library),并添加 api gradleApi() 依赖
			// https://docs.gradle.org/current/userguide/java_gradle_plugin.html
			plugins.apply(PublishPlugin.class);
			plugins.apply(SigningPlugin.class);
			configureCommonAttributes(project);
		}
	}

	private void configureCommonAttributes(Project project) {
		project.getPlugins().withType(PublishPlugin.class,publishPlugin -> {
			GradlePluginDevelopmentExtension extension = project.getExtensions().getByType(GradlePluginDevelopmentExtension.class);
			extension.getWebsite().set("https://github.com/jcohy/jcohy-gradle-plugins");
			extension.getVcsUrl().set("https://github.com/jcohy/jcohy-gradle-plugins");
		});
	}

}
