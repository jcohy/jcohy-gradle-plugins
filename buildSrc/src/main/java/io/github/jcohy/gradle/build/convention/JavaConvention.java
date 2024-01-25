package io.github.jcohy.gradle.build.convention;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.external.javadoc.CoreJavadocOptions;
import org.gradle.jvm.tasks.Jar;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2024 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2024.0.1 2023/2/17 17:24
 * @since 1.0.0
 */
public class JavaConvention implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.getPlugins().apply(JavaPlugin.class);

		project.getPlugins().withType(JavaPlugin.class, (java) -> {
			configureTestConventions(project);
			configureMavenRepository(project);
			configureJarManifest(project);
			configureJavadoc(project);
			configureDependencyManagement(project);
		});
	}

	/**
	 * 添加测试依赖
	 * @param project project
	 */
	private void configureTestConventions(Project project) {
		project.getTasks().withType(Test.class, (test) -> {
			test.useJUnitPlatform();
			test.setMaxHeapSize("1024M");
		});
		// 添加测试依赖
		project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
			project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.platform:junit-platform-launcher");
			project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter-engine");
			project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter-api");
			project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.assertj:assertj-core");
			project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.springframework.boot:spring-boot-starter-test");
		});

		if (!project.getName().equals("jcohy-plugins-utils")) {
			project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, project.getDependencies().project(Collections.singletonMap("path",
					":projects:jcohy-plugins-utils")));
		}
	}

	/**
	 * manifest 文件配置
	 * @param project project
	 */
	private void configureJarManifest(Project project) {


		project.setProperty("sourceCompatibility", "17");
		project.setProperty("targetCompatibility", "17");

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
			mavenRepo.setUrl(URI.create("https://maven.aliyun.com/repository/public"));
			mavenRepo.setName("ali");
		});
		project.getRepositories().mavenCentral();
		project.getRepositories().gradlePluginPortal();
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
		dependencyManagementExtension.imports((importsHandler -> importsHandler.mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")));

		configurations
				.matching((configuration) ->
						configuration.getName().endsWith("Classpath") || JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.equals(configuration.getName()))
				.all((configuration) -> {
					configuration.extendsFrom(dependencyManagement);
				});

		Dependency parent = project.getDependencies().enforcedPlatform(project.getDependencies()
				.project(Collections.singletonMap("path", ":projects:bom")));

		project.getConfigurations().getByName("dependencyManagement", (dependency) -> {
			dependency.getDependencies().add(parent);
		});
	}

	/**
	 * JavaDoc 配置
	 * @param project project
	 */
	private void configureJavadoc(Project project) {
		project.getTasks().withType(Javadoc.class, (javadoc) -> {
			CoreJavadocOptions options = (CoreJavadocOptions) javadoc.getOptions();
			options.source("17");
			options.encoding("UTF-8");
			options.addStringOption("Xdoclint:none", "-quiet");
		});
	}

}
