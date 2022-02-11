package com.jcohy.convention.conventions;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Description:
 *
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/11:15:26
 * @since 0.0.5.1
 */
public class ConventionsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        new JavaConventions().apply(project);
        new MavenPublishingConventions().apply(project);
        new AsciidoctorConventions().apply(project);
    }
}
