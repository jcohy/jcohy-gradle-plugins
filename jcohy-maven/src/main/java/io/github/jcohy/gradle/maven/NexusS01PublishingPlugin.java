package io.github.jcohy.gradle.maven;

import io.github.gradlenexus.publishplugin.NexusPublishExtension;
import io.github.gradlenexus.publishplugin.NexusPublishPlugin;
import io.github.jcohy.gradle.maven.convention.JavaConvention;
import io.github.jcohy.gradle.maven.convention.MavenPublishConvention;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;

import java.net.URI;
import java.time.Duration;

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
public class NexusS01PublishingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply(NexusPublishPlugin.class);
        pluginManager.apply(JcohySigningPlugin.class);
        new JavaConvention().apply(project);
        new MavenPublishConvention().apply(project);

        NexusPublishExtension extension = project.getExtensions().getByType(NexusPublishExtension.class);
        extension.getRepositories().create("sonatypeS01", repository -> {
            repository.getUsername().set(System.getenv("NEXUS_OSS_USER_NAME"));
            repository.getPassword().set(System.getenv("NEXUS_OSS_PASS_WORD"));
            repository.getNexusUrl().set(URI.create("https://s01.oss.sonatype.org/service/local"));
            repository.getSnapshotRepositoryUrl().set(URI.create("https://s01.oss..sonatype.org/content/repositories/snapshots"));
        });
        extension.getConnectTimeout().set(Duration.ofMinutes(3));
        extension.getClientTimeout().set(Duration.ofMinutes(3));
    }
}
