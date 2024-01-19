package io.github.jcohy.gradle.utils;

import org.gradle.api.Project;

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

	public static boolean isSnapshot(Project project) {
		String projectVersion = projectVersion(project);
		return projectVersion.matches("^.*([.-]BUILD)?-SNAPSHOT$");
	}

	public static boolean isMilestone(Project project) {
		String projectVersion = projectVersion(project);
		return projectVersion.matches("^.*[.-]M\\d+$") || projectVersion.matches("^.*[.-]RC\\d+$");
	}

	public static boolean isRelease(Project project) {
		return !(isSnapshot(project) || isMilestone(project));
	}

	private static String projectVersion(Project project) {
		return String.valueOf(project.getVersion());
	}
}
