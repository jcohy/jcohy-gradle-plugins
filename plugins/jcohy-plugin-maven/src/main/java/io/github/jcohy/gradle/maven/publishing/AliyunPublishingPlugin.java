package io.github.jcohy.gradle.maven.publishing;

import io.github.jcohy.gradle.maven.repository.AliYunRepository;
import io.github.jcohy.gradle.utils.ProjectUtils;
import io.github.jcohy.gradle.utils.VersionUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:17:39
 * @since 2022.04.0
 */
public class AliyunPublishingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
		// Apply nexus publish plugin
		PluginManager pluginManager = project.getPluginManager();
		pluginManager.apply(MavenPublishPlugin.class);

		PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);

		publishingExtension.getRepositories().maven(mavenRepository -> {
			AliYunRepository aliYunRepository = AliYunRepository.of(VersionUtils.isRelease(project));
			mavenRepository.setUrl(aliYunRepository.getUrl());
			mavenRepository.setName(aliYunRepository.getName());
			mavenRepository.credentials((passwordCredentials -> {
				passwordCredentials.setUsername(ProjectUtils.getProjectProperties(project, "ALIYUN_USERNAME"));
				passwordCredentials.setPassword(ProjectUtils.getProjectProperties(project, "ALIYUN_PASSWORD"));
			}));
		});
    }
}
