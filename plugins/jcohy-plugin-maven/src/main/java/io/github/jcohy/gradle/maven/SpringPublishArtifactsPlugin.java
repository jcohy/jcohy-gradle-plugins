package io.github.jcohy.gradle.maven;

import io.github.jcohy.gradle.utils.ProjectUtils;
import io.github.jcohy.gradle.utils.VersionUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/24 14:11
 * @since 2024.0.1
 */
public class SpringPublishArtifactsPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getTasks().register("publishArtifacts", (publishArtifacts) -> {
			publishArtifacts.setGroup("Publishing");
			publishArtifacts.setDescription("Publish the artifacts to either Artifactory or Maven Central based on the version");
			if (VersionUtils.isRelease(project)) {
				publishArtifacts.dependsOn("publishToOssrh");
			} else {
				publishArtifacts.dependsOn("artifactoryPublish");
			}
		});
	}
}
