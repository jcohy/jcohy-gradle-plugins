package io.github.jcohy.gradle.build;

import com.gradle.publish.PublishPlugin;
import io.github.jcohy.gradle.build.convention.AliYunPublishConvention;
import io.github.jcohy.gradle.build.convention.JavaConvention;
import io.github.jcohy.gradle.build.convention.MavenPublishConvention;
import io.github.jcohy.gradle.build.convention.NexusPublishingConvention;
import io.github.jcohy.gradle.build.convention.SigningPublishingConvention;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension;
import org.gradle.plugins.signing.SigningPlugin;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/25 10:22
 * @since 2024.0.1
 */
public class JavaModulePlugins implements Plugin<Project> {
	@Override
	public void apply(Project project) {

		PluginContainer plugins = project.getPlugins();

		plugins.apply(JavaConvention.class);
		plugins.apply(MavenPublishConvention.class);
		plugins.apply(NexusPublishingConvention.class);
		plugins.apply(SigningPublishingConvention.class);
		plugins.apply(AliYunPublishConvention.class);
	}
}
