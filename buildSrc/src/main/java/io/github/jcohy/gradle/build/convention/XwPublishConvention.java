package io.github.jcohy.gradle.build.convention;

import io.github.jcohy.gradle.build.Utils;
import io.github.jcohy.gradle.build.publishing.PomConstant;
import io.github.jcohy.gradle.build.version.AliYunRepository;
import io.github.jcohy.gradle.build.version.ReleaseStatus;
import io.github.jcohy.gradle.build.version.XwRepository;
import org.gradle.api.Project;
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
public class XwPublishConvention {

    public void apply(Project project) {
        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
        publishingExtension.getRepositories().maven(mavenRepository -> {
            XwRepository repository = XwRepository.of(ReleaseStatus.ofProject(project));
            mavenRepository.setUrl(repository.getUrl());
            mavenRepository.setName(repository.getName());
            mavenRepository.setAllowInsecureProtocol(true);
            mavenRepository.credentials((passwordCredentials -> {
                passwordCredentials.setUsername("xw-developer");
                passwordCredentials.setPassword("xx");
            }));
        });
    }
}
