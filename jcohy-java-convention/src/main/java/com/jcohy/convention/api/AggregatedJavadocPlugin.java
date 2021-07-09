package com.jcohy.convention.api;

import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/7/9:15:24
 * @since 1.0.0
 */
public class AggregatedJavadocPlugin implements Plugin<Project> {

    private static final String TASK_NAME = "api";

    @Override
    public void apply(Project project) {
        AggregateJavadocExtension extension = project.getExtensions().create("aggregateJavadoc",AggregateJavadocExtension.class);
        Javadoc javadoc = project.getTasks().create(TASK_NAME, Javadoc.class, extension);

        Set<Project> publishedProjects = extension.getPublishedProjects();
        project.getRootProject().getGradle().projectsEvaluated((gradle) -> {
            gradle.getRootProject().subprojects((sub) -> {
                javadoc.dependsOn(publishedProjects);
            });
        });

    }
}
