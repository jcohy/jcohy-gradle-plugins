package io.github.jcohy.gradle.convention.sonar;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.sonarqube.gradle.SonarQubeExtension;
import org.sonarqube.gradle.SonarQubePlugin;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description: 集成 SonarQube 代码质量管理平台
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/1:18:17
 * @since 0.0.5.1
 */
public class SonarScannerPlugin implements Plugin<Project> {

    private static final String HOST_URL = "http://192.168.11.244:9000";

    private static final String LOGIN = "3abb5ed05e3cf7650e28bea0c29fdfb3803348d2";

    private static final String LINKS_HOMEPAGE = "https://github.com/jcohy/jcohy-gradle-plugins";

    private static final String JACOCO_ISSUE = "https://github.com/jcohy/jcohy-gradle-plugins/issues";

    private static final String JACOCO_SCM = "https://github.com/jcohy/jcohy-gradle-plugins";

    private final BiFunction<Object, String, String> valueFunction = (str1, str2) -> {
        if (Objects.isNull(str1)) {
            return str2;
        }
        return (String) str1;
    };


    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JacocoPlugin.class);
        project.getPlugins().apply(SonarQubePlugin.class);
        SonarQubeExtension extension = project.getExtensions().getByType(SonarQubeExtension.class);
        extension.properties(properties -> {
            Map<String, ?> projectProperties = project.getProperties();
            properties.property("sonar.projectKey", "TD");
//            properties.property("sonar.scm.provider",valueFunction.apply(projectProperties.get("sonar.links.homepage"),LINKS_HOMEPAGE));
            properties.property("sonar.host.url", valueFunction.apply(projectProperties.get("sonar.host.url"), HOST_URL));
            properties.property("sonar.projectVersion", valueFunction.apply(project.getVersion(), "Sample"));
            properties.property("sonar.projectDescription", project.getDescription());
            properties.property("sonar.login", valueFunction.apply(projectProperties.get("sonar.login"), LOGIN));
            properties.property("sonar.jacoco.reportPath", project.getBuildDir() + "/jacoco/test.exec");
            properties.property("sonar.links.homepage", valueFunction.apply(projectProperties.get("sonar.links.homepage"), LINKS_HOMEPAGE));
            properties.property("sonar.jacoco.issue", valueFunction.apply(projectProperties.get("sonar.links.issue"), JACOCO_ISSUE));
            properties.property("sonar.jacoco.scm", valueFunction.apply(projectProperties.get("sonar.jacoco.scm"), JACOCO_SCM));
        });
    }
}
