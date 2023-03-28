package io.github.jcohy.gradle.maven.plugins;

import io.github.jcohy.gradle.maven.constant.RepositoryType;
import io.github.jcohy.gradle.maven.dsl.ArchivesPublishingExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/10/17:17:01
 * @since 2022.0.1
 */
public class ArchivesPublishingPlugins implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        ArchivesPublishingExtension extension = project.getExtensions()
                .create(ArchivesPublishingExtension.JCOHY_MAVEN_PUBLISHING_EXTENSION_NAME,
                        ArchivesPublishingExtension.class, project);
        project.afterEvaluate(p -> {
            RepositoryType name = extension.getName();
            System.out.println(name.getName());
            System.out.println(extension.getUsername());
        });
    }
}
