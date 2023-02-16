package com.jcohy.gradle.build;


import java.net.URI;
import java.util.Collections;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.java.DefaultJavaPlatformExtension;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlatformExtension;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/2/16 17:15
 * @since 1.0.0
 */
public class GradlePluginModulePlugins implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginContainer plugins = project.getPlugins();
		if(project.getName().startsWith("jcohy-")) {
			plugins.apply(JavaLibraryPlugin.class);
			plugins.apply(JavaGradlePluginPlugin.class);
			plugins.apply(MavenPublishPlugin.class);
			project.setProperty("sourceCompatibility", "17");
			project.setProperty("targetCompatibility", "17");
			configureMavenRepository(project);
			configureDependencyManagement(project);
		}

	}

	private void configureMavenRepository(Project project) {
		project.getRepositories().maven((mavenRepo) -> {
			mavenRepo.setUrl(URI.create("https://maven.aliyun.com/repository/central"));
			mavenRepo.setName("ali");
		});

		project.getRepositories().maven((mavenRepo) -> {
			mavenRepo.setUrl(URI.create("https://maven.aliyun.com/repository/central"));
			mavenRepo.setName("ali");
		});

		project.getRepositories().mavenCentral();
		project.getRepositories().gradlePluginPortal();

		project.getRepositories().maven((mavenRepo) -> {
			mavenRepo.setUrl(URI.create("https://repo.spring.io/artifactory/release/"));
			mavenRepo.setName("spring");
		});
	}


	private void configureDependencyManagement(Project project) {

		ConfigurationContainer configurations = project.getConfigurations();
		Configuration dependencyManagement = configurations.create("dependencyManagement", (configuration) -> {
			configuration.setVisible(false);
			configuration.setCanBeConsumed(false);
			configuration.setCanBeResolved(false);
		});

		project.getPlugins().apply(DependencyManagementPlugin.class);

		DependencyManagementExtension dependencyManagementExtension = project.getExtensions().getByType(DependencyManagementExtension.class);
		dependencyManagementExtension.imports((importsHandler -> {
			importsHandler.mavenBom("org.springframework.boot:spring-boot-dependencies:3.0.0");
		}));

		configurations
				.matching((configuration) ->
						configuration.getName().endsWith("Classpath") || JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.equals(configuration.getName()))
				.all((configuration) -> {
					configuration.extendsFrom(dependencyManagement);
				});

//		project.getDependencies().getConstraints().add(JavaPlugin.API_CONFIGURATION_NAME,"org.asciidoctor:asciidoctor-gradle-jvm:3.3.2");
		Dependency parent = project.getDependencies().enforcedPlatform(project.getDependencies()
				.project(Collections.singletonMap("path", ":bom")));

		project.getConfigurations().getByName("dependencyManagement", (dependency) -> {
			dependency.getDependencies().add(parent);
		});

	}
}
