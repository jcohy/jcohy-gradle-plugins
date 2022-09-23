package com.jcohy.gradle.javadoc;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:15:46
 * @since 2022.0.1
 */
public class AggregateJavadocPluginTest {

    @Test
    void classpathThenIncludesAggregatedModuleClasspath() {
        Project root = rootProject();
        Project module1 = projectWithPlugins("module1", root);
        addImplementationDependencies(module1, "io.projectreactor:reactor-core:3.3.5.RELEASE");

        Project module2 = projectWithPlugins("module2", root);
        addImplementationDependencies(module2, "org.slf4j:slf4j-api:1.7.30");

        Javadoc aggregateJavadoc = (Javadoc) root.getTasks()
                .findByName(AggregateJavadocPlugin.AGGREGATE_JAVADOC_TASK_NAME);

        FileCollection classpath = aggregateJavadoc.getClasspath();
        classpath.getFiles().forEach((file) -> System.out.println(file.getName()));
//        System.out.println();
        assertThat(classpath.getFiles()).extracting(File::getName).contains("reactor-core-3.3.5.RELEASE.jar");
        assertThat(classpath.getFiles()).extracting(File::getName).contains("slf4j-api-1.7.30.jar");
    }

    private void addImplementationDependencies(Project project, String... dependencies) {
        Configuration implementation = project.getConfigurations().getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME);
        for(String dependency : dependencies) {
            implementation.getDependencies().add(project.getDependencies().create(dependency));
        }
    }

    public Project projectWithPlugins(String name, Project parent) {
        Project project = ProjectBuilder.builder().withParent(parent).withName(name).build();
        PluginContainer plugins = project.getPlugins();
        plugins.apply(JavadocPlugin.class);
        plugins.apply(JavaPlugin.class);
        addMavenCentral(project);
        return project;
    }
    private Project rootProject() {
        Project project = ProjectBuilder.builder().withName("root").build();
        PluginContainer plugins = project.getPlugins();
        plugins.apply(AggregateJavadocPlugin.class);
        addMavenCentral(project);
        return project;
    }

    private void addMavenCentral(Project project) {
        project.getRepositories().mavenCentral();
    }
}
