package com.jcohy.convention.sonar;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.sonarqube.gradle.SonarQubeExtension;
import org.sonarqube.gradle.SonarQubePlugin;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/7/1:18:17
 * @since 1.0.0
 */
public class SonarScannerPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JacocoPlugin.class);
        project.getPlugins().apply(SonarQubePlugin.class);
        SonarQubeExtension extension = project.getExtensions().getByType(SonarQubeExtension.class);
        extension.properties( properties -> {
            properties.property("sonar.projectKey", "TD");
            properties.property("sonar.host.url","http://192.168.11.244:9000");
            properties.property("sonar.login", "3abb5ed05e3cf7650e28bea0c29fdfb3803348d2");
            properties.property("sonar.jacoco.reportPath",project.getBuildDir()+"/jacoco/test.exec");
            properties.property("sonar.links.homepage","https://github.com/jcohy/jcohy-gradle-plugins");
            properties.property("sonar.jacoco.issue","https://github.com/jcohy/jcohy-gradle-plugins/issues");
            properties.property("sonar.jacoco.scm","https://github.com/jcohy/jcohy-gradle-plugins");
        });

    }
}
