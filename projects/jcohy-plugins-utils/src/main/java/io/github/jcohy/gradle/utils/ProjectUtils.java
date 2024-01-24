package io.github.jcohy.gradle.utils;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/19 9:15
 * @since 2024.0.1
 */
public class ProjectUtils {

	private ProjectUtils() {
	}

	public static String getProjectName(Project project) {
		String projectName = project.getRootProject().getName();
		if (projectName.endsWith("-build")) {
			projectName = projectName.substring(0, projectName.length() - "-build".length());
		}
		return projectName;
	}

	public static String getProjectProperties(Project project, String key) {
		return getProjectProperties(project, key, "");
	}

//	public static String getProperties(Project project, String key) {
//		return getProperties(project,key,"");
//	}

	public static String getProjectProperties(Project project, String key, String defaultValue) {
		var ref = new Object() {
			String value = defaultValue;
		};

		if (StringUtils.isNotEmpty(System.getenv(key))) {
			ref.value = System.getenv(key);
		}

		project.afterEvaluate(project1 -> {
			Object o = project1.getExtensions().getByType(ExtraPropertiesExtension.class).getProperties()
					.get(key);
			ref.value = (String) o;
		});

		if (project.hasProperty(key)) {
			ref.value = (String) project.findProperty(key);
		}
		return ref.value;
	}

}
