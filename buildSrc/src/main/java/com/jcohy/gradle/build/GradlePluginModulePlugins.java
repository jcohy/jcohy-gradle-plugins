package com.jcohy.gradle.build;


import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlatformPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.jvm.tasks.Jar;
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
			project.setProperty("sourceCompatibility", "17");
			project.setProperty("targetCompatibility", "17");
			configureTestConventions(project);
			configureMavenRepository(project);
			configureJarManifest(project);
			configureJavadoc(project);
			configureDependencyManagement(project);
		}

	}

	private void configureTestConventions(Project project) {
		project.getTasks().withType(Test.class, (test) -> {
			test.useJUnitPlatform();
			test.setMaxHeapSize("1024M");
		});
		project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
			project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter-engine");
			project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter-api");
			project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.assertj:assertj-core");
			project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.springframework.boot:spring-boot-starter-test");
		});

	}
	private void configureJarManifest(Project project) {

		SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
		Set<String> sourceJarTaskNames = sourceSets.stream().map(SourceSet::getSourcesJarTaskName).collect(Collectors.toSet());
		Set<String> javadocJarTaskNames = sourceSets.stream().map(SourceSet::getJavadocJarTaskName).collect(Collectors.toSet());

		project.getTasks().withType(Jar.class, jar -> project.afterEvaluate((evaluated) -> {
			jar.manifest(manifest -> {
				Map<String, Object> attributes = new TreeMap<>();
				attributes.put("Automatic-Module-Name", project.getName().replace("-", "."));
				attributes.put("Build-Jdk-Spec", project.property("sourceCompatibility"));
				attributes.put("Built-By", "Jcohy");
				attributes.put("Implementation-Title",
						determineImplementationTitle(project, sourceJarTaskNames, javadocJarTaskNames, jar));
				attributes.put("Implementation-Version", project.getVersion());
				manifest.attributes(attributes);
			});
		}));
	}

	private void configureJavadoc(Project project) {
		project.getTasks().withType(Javadoc.class, javadoc -> {
			javadoc.setDescription("Generates project-level javadoc for use in -javadoc jar");
			javadoc.options((option) -> {
				option.encoding("UTF-8");
				option.source("17");
				option.setMemberLevel(JavadocMemberLevel.PROTECTED);
				option.header(project.getName());
			});
			// 跨模块的 @see 和 @link 引用消除警告
			javadoc.getLogging().captureStandardError(LogLevel.INFO);
			// 消除 "## warnings" 消息
			javadoc.getLogging().captureStandardOutput(LogLevel.INFO);
		});
	}

	private String determineImplementationTitle(Project project, Set<String> sourceJarTaskNames,
												Set<String> javadocJarTaskNames, Jar jar) {
		if (sourceJarTaskNames.contains(jar.getName())) {
			return "Source for " + project.getName();
		}
		if (javadocJarTaskNames.contains(jar.getName())) {
			return "Javadoc for " + project.getName();
		}
		return project.getDescription();
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
			Dependency parent = project.getDependencies().enforcedPlatform(project.getDependencies()
					.platform(Collections.singletonMap("path", ":bom")));
			configuration.getDependencies().add(parent);
		});

		project.getDependencies().add(JavaPlatformPlugin.API_CONFIGURATION_NAME, ":bom");
		project.getPlugins().apply(DependencyManagementPlugin.class);

		DependencyManagementExtension dependencyManagementExtension = project.getExtensions().getByType(DependencyManagementExtension.class);
		dependencyManagementExtension.imports((importsHandler -> importsHandler.mavenBom("org.springframework.boot:spring-boot-dependencies:3.0.0")));

		configurations
				.matching((configuration) ->
						configuration.getName().endsWith("Classpath") || JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.equals(configuration.getName()))
				.all((configuration) -> {
					configuration.extendsFrom(dependencyManagement);
				});

//		project.getDependencies().getConstraints().add(JavaPlugin.API_CONFIGURATION_NAME,"org.asciidoctor:asciidoctor-gradle-jvm:3.3.2");
//


	}
}
