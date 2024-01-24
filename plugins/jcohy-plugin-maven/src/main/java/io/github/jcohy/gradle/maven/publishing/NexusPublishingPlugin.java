package io.github.jcohy.gradle.maven.publishing;

import java.net.URI;
import java.time.Duration;

import io.github.gradlenexus.publishplugin.NexusPublishExtension;
import io.github.gradlenexus.publishplugin.NexusPublishPlugin;
import io.github.jcohy.gradle.utils.VersionUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/24 10:59
 * @since 2024.0.1
 */
public class NexusPublishingPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {

		// Apply nexus publish plugin
		project.getPlugins().apply(NexusPublishPlugin.class);

		NexusPublishExtension nexusPublishing = project.getExtensions().getByType(NexusPublishExtension.class);
		nexusPublishing.getRepositories().create("sonatype", (nexusRepository) -> {
			nexusRepository.getUsername().set((String) project.findProperty("NEXUS_OSS_USER_NAME"));
			nexusRepository.getPassword().set((String) project.findProperty("NEXUS_OSS_PASS_WORD"));
			nexusRepository.getNexusUrl().set(URI.create("https://oss.sonatype.org/service/local/staging/deploy/maven2"));
			nexusRepository.getSnapshotRepositoryUrl().set(URI.create("https://oss.sonatype.org/content/repositories/snapshots"));
		});

		// 配置超时
		nexusPublishing.getConnectTimeout().set(Duration.ofMinutes(3));
		nexusPublishing.getClientTimeout().set(Duration.ofMinutes(3));

		// 确保 release 版本自动关闭
		Task finalizeDeployArtifacts = project.task("finalizeDeployArtifacts");
		if (VersionUtils.isRelease(project) && project.hasProperty("NEXUS_OSS_USER_NAME")) {
			Task closeAndReleaseOssrhStagingRepository = project.getTasks().findByName("closeAndReleaseOssrhStagingRepository");
			finalizeDeployArtifacts.dependsOn(closeAndReleaseOssrhStagingRepository);
		}
	}
}
