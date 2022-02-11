package com.jcohy.convention.deployed;

import com.jcohy.convention.constant.PomConstant;
import com.jcohy.convention.dsl.PomExtension;
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
 * Description: 当项目需要部署时，可以使用该插件。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/17:12:39
 * @since 0.0.5.1
 */
public class DeployedPlugin implements Plugin<Project> {

    private PomExtension pomExtension = null;

    @Override
    public void apply(Project project) {
        PluginContainer plugins = project.getPlugins();
        plugins.apply(MavenPublishPlugin.class);
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        MavenPublication mavenPublication = publishing.getPublications().create("maven", MavenPublication.class);

        pomExtension = project.getRootProject().getExtensions().create(PomExtension.OSS_EXTENSION_NAME,
                PomExtension.class);

        publishing.getRepositories().maven(mavenRepository -> {
            Repository repository = Repository.of(ReleaseStatus.ofProject(project));
            mavenRepository.setUrl(repository.getUrl());
            mavenRepository.setName(repository.getName());
            mavenRepository.credentials((passwordCredentials -> {
                passwordCredentials.setUsername(pomExtension.getUsername() != null ? pomExtension.getUsername() : PomConstant.NEXUS_USER_NAME);
                passwordCredentials.setPassword(pomExtension.getPassword() != null ? pomExtension.getPassword() : PomConstant.NEXUS_PASSWORD);
            }));
        });
        
        project.afterEvaluate((evaluated) -> {
            project.getPlugins().withType(JavaPlugin.class).all((javaPlugin) -> {
                if (((Jar) project.getTasks().getByName(JavaPlugin.JAR_TASK_NAME)).isEnabled()) {
                    project.getComponents().matching((component) -> "java".equals(component.getName()))
                            .all(mavenPublication::from);
                }
            });
        });
        project.getPlugins().withType(JavaPlatformPlugin.class)
                .all((javaPlugin) -> project.getComponents()
                        .matching((component) -> "javaPlatform".equals(component.getName()))
                        .all(mavenPublication::from));
    }
}
