package io.github.jcohy.gradle.build.convention;

import java.net.URI;

import io.github.gradlenexus.publishplugin.NexusPublishExtension;
import io.github.gradlenexus.publishplugin.NexusPublishPlugin;
import org.gradle.api.Project;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/2/17 16:43
 * @since 1.0.0
 */
public class NexusPublishingConvention {

	public void apply(Project project) {
		project.getPlugins().withType(NexusPublishPlugin.class,nexusPublishPlugin -> {
			NexusPublishExtension extension = project.getExtensions().getByType(NexusPublishExtension.class);
			extension.repositories(repository -> {
				repository.sonatype((sonatype) ->{
					sonatype.getUsername().set(System.getenv("NEXUS_OSS_USER_NAME"));
					sonatype.getPassword().set(System.getenv("NEXUS_OSS_PASS_WORD"));
					sonatype.getNexusUrl().set(URI.create("https://oss.sonatype.org/service/local/staging/deploy/maven2"));
					sonatype.getSnapshotRepositoryUrl().set(URI.create("https://oss.sonatype.org/content/repositories/snapshots"));
				});
			});
		});
	}
}
