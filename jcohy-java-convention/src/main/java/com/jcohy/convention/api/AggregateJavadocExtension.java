package com.jcohy.convention.api;

import java.util.Set;

import org.gradle.api.Project;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/7/9:15:26
 * @since 1.0.0
 */
public class AggregateJavadocExtension {

    private Set<Project> publishedProjects;

    public Set<Project> getPublishedProjects() {
        return publishedProjects;
    }
}
