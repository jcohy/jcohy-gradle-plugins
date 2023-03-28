package io.github.jcohy.gradle.maven;

import io.github.jcohy.gradle.maven.constant.PomConstant;
import io.github.jcohy.gradle.maven.convention.JavaConvention;
import io.github.jcohy.gradle.maven.convention.MavenPublishConvention;
import io.github.jcohy.gradle.maven.version.AliYunRepository;
import io.github.jcohy.gradle.maven.version.ReleaseStatus;
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
public class AliyunPublishPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply(MavenPublishPlugin.class);
        new JavaConvention().apply(project);
        new MavenPublishConvention().apply(project);

        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);

        publishingExtension.getRepositories().maven(mavenRepository -> {
            AliYunRepository aliYunRepository = AliYunRepository.of(ReleaseStatus.ofProject(project));
            mavenRepository.setUrl(aliYunRepository.getUrl());
            mavenRepository.setName(aliYunRepository.getName());
            mavenRepository.credentials((passwordCredentials -> {
                passwordCredentials.setUsername(Utils.getProperties(project, "ALIYUN_USERNAME", PomConstant.NEXUS_USER_NAME));
                passwordCredentials.setPassword(Utils.getProperties(project, "ALIYUN_PASSWORD", PomConstant.NEXUS_USER_NAME));
            }));
        });
    }
}
