package io.github.jcohy.gradle.build.convention;

import io.github.jcohy.gradle.build.Utils;
import io.github.jcohy.gradle.build.publishing.PomConstant;
import io.github.jcohy.gradle.build.version.AliYunRepository;
import io.github.jcohy.gradle.build.version.ReleaseStatus;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.publish.PublishingExtension;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:18:13
 * @since 2022.04.0
 */
public class AliYunPublishConvention implements Plugin<Project> {

	@Override
    public void apply(Project project) {
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
