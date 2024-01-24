package io.github.jcohy.gradle.maven;

import io.github.jcohy.gradle.maven.convention.JavaConventionPlugin;
import io.github.jcohy.gradle.maven.convention.SpringMavenPublishingConventionsPlugin;
import io.github.jcohy.gradle.maven.publishing.LocalPublishingPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/24 11:13
 * @since 2024.0.1
 */
public class SpringMavenPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		// Apply default plugins
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(MavenPublishPlugin.class);

		pluginManager.apply(JcohySigningPlugin.class);
		pluginManager.apply(SpringMavenPublishingConventionsPlugin.class);
		pluginManager.apply(JavaConventionPlugin.class);
		pluginManager.apply(SpringPublishAllJavaComponentsPlugin.class);
		pluginManager.apply(LocalPublishingPlugin.class);
		pluginManager.apply(SpringPublishArtifactsPlugin.class);
	}
}
