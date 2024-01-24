package io.github.jcohy.gradle.maven;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlatformPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/24 10:56
 * @since 2024.0.1
 */
public class SpringPublishAllJavaComponentsPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getPlugins().withType(MavenPublishPlugin.class, (mavenPublish) -> {
			PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
			publishing.getPublications().create("mavenJava", MavenPublication.class, (maven) -> {
				project.getPlugins().withType(JavaPlugin.class, (plugin) ->
						maven.from(project.getComponents().getByName("java")));
				project.getPlugins().withType(JavaPlatformPlugin.class, (plugin) ->
						maven.from(project.getComponents().getByName("javaPlatform")));
			});
		});
	}
}
