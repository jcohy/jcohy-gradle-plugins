package com.jcohy.convention.deployed;

import com.jcohy.convention.constant.PomConstant;
import com.jcohy.convention.version.ReleaseStatus;
import com.jcohy.convention.version.Repository;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlatformPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.bundling.Jar;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/6/17:12:39
 * @since 1.0.0
 */
public class DeployedPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        PluginContainer plugins = project.getPlugins();
        plugins.apply(MavenPublishPlugin.class);
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        MavenPublication mavenPublication = publishing.getPublications().create("maven", MavenPublication.class);
        
        publishing.getRepositories().maven(mavenRepository -> {
            Repository repository = Repository.of(ReleaseStatus.ofProject(project));
            mavenRepository.setUrl(repository.getUrl());
            mavenRepository.setName(repository.getName());
            mavenRepository.credentials((passwordCredentials -> {
                passwordCredentials.setUsername(PomConstant.NEXUS_USER_NAME);
                passwordCredentials.setPassword(PomConstant.NEXUS_PASSWORD);
            }));
        });
        
        project.afterEvaluate((evaluated) -> {
            project.getPlugins().withType(JavaPlugin.class).all((javaPlugin) -> {
                if (((Jar) project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME)).isEnabled()) {
                    project.getComponents().matching((component) -> component.getName().equals("java"))
                            .all((javaComponent) -> mavenPublication.from(javaComponent));
                }
            });
        });
        project.getPlugins().withType(JavaPlatformPlugin.class)
                .all((javaPlugin) -> project.getComponents()
                        .matching((component) -> component.getName().equals("javaPlatform"))
                        .all((javaComponent) -> mavenPublication.from(javaComponent)));
    }
}
