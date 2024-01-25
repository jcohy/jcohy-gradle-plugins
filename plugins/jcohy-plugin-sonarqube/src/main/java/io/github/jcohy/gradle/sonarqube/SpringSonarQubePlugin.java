/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jcohy.gradle.sonarqube;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import io.github.jcohy.gradle.utils.ProjectUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.sonarqube.gradle.SonarQubeExtension;
import org.sonarqube.gradle.SonarQubePlugin;


/**
 * @author Steve Riesenberg
 */
public class SpringSonarQubePlugin implements Plugin<Project> {

	private static final String HOST_URL = "http://192.168.11.244:9000";

	private static final String LOGIN = "3abb5ed05e3cf7650e28bea0c29fdfb3803348d2";

	private static final String LINKS_HOMEPAGE = "https://github.com/jcohy/jcohy-gradle-plugins";

	private static final String JACOCO_ISSUE = "https://github.com/jcohy/jcohy-gradle-plugins/issues";

	private static final String JACOCO_SCM = "https://github.com/jcohy/jcohy-gradle-plugins";
	private static final String JACOCO_SCM_DEV = "https://github.com/jcohy/jcohy-gradle-plugins.git";
	private static final String JACOCO_CI = "https://jenkins.spring.io/job/jcohy";

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
			properties.property("sonar.projectKey", valueFunction.apply(projectProperties.get("sonar.projectKey"), "Jcohy"));
//            properties.property("sonar.scm.provider",valueFunction.apply(projectProperties.get("sonar.links.homepage"),LINKS_HOMEPAGE));
			properties.property("sonar.java.coveragePlugin", "jacoco");
			properties.property("sonar.projectName", valueFunction.apply(projectProperties.get("sonar.projectName"), "Jcohy"));
			properties.property("sonar.projectVersion", valueFunction.apply(project.getVersion(), "Sample"));
			properties.property("sonar.projectDescription", Objects.requireNonNull(project.getDescription()));

			properties.property("sonar.host.url", valueFunction.apply(projectProperties.get("sonar.host.url"), HOST_URL));
			properties.property("sonar.login", valueFunction.apply(projectProperties.get("sonar.login"), LOGIN));

			properties.property("sonar.links.homepage", valueFunction.apply(projectProperties.get("sonar.links.homepage"), LINKS_HOMEPAGE));
			properties.property("sonar.links.ci", valueFunction.apply(projectProperties.get("sonar.links.ci"), JACOCO_CI));
			properties.property("sonar.links.scm_dev", valueFunction.apply(projectProperties.get("sonar.links.scm_dev"), JACOCO_SCM_DEV));

			properties.property("sonar.jacoco.reportPath", project.getBuildDir().getName() + "/jacoco.exec");
			properties.property("sonar.jacoco.issue", valueFunction.apply(projectProperties.get("sonar.links.issue"), JACOCO_ISSUE));
			properties.property("sonar.jacoco.scm", valueFunction.apply(projectProperties.get("sonar.jacoco.scm"), JACOCO_SCM));
		});
	}
}
