package io.github.jcohy.gradle.maven.convention;

import java.util.Collections;
import java.util.Objects;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.VariantVersionMappingStrategy;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPomDeveloperSpec;
import org.gradle.api.publish.maven.MavenPomIssueManagement;
import org.gradle.api.publish.maven.MavenPomLicenseSpec;
import org.gradle.api.publish.maven.MavenPomOrganization;
import org.gradle.api.publish.maven.MavenPomScm;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/24 11:14
 * @since 2024.0.1
 */
public class SpringMavenPublishingConventionsPlugin implements Plugin<Project> {


	@Override
	public void apply(Project project) {
		project.getPlugins().withType(MavenPublishPlugin.class, (mavenPublish) -> {
			PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
			publishing.getPublications().withType(MavenPublication.class, (mavenPublication) ->
					customizePom(mavenPublication.getPom(), project));
			SpringMavenPublishingConventionsPlugin.this.customizeJavaPlugin(project);

			publishing.getPublications().withType(MavenPublication.class)
					.all(((mavenPublication) -> customizeMavenPublication(mavenPublication, project)));
		});
	}

	/**
	 * 自定义 maven 满足 Maven Central 的要求
	 *
	 * @param publication publication
	 * @param project     project
	 */
	private void customizeMavenPublication(MavenPublication publication, Project project) {
		customizePom(publication.getPom(), project);
		project.getPlugins().withType(JavaPlugin.class)
				.all(javaPlugin -> customizeJavaMavenPublication(publication, project));
		suppressMavenOptionalFeatureWarnings(publication);
	}


	/**
	 * 定义 pom 文件
	 *
	 * @param pom     pom
	 * @param project project
	 */
	private void customizePom(MavenPom pom, Project project) {
		pom.getUrl().set("https://github.com/jcohy/jcohy-gradle-plugins.git");
		pom.getName().set(project.provider(project::getName));
		pom.getDescription().set(project.provider(project::getDescription));
		pom.organization(this::customizeOrganization);
		pom.licenses(this::customizeLicenses);
		pom.developers(this::customizeDevelopers);
		pom.scm(this::customizeScm);
		pom.issueManagement(this::customizeIssueManagement);
	}


	/**
	 * 定义组织
	 *
	 * @param organization organization
	 */
	private void customizeOrganization(MavenPomOrganization organization) {
		organization.getName().set("jcohy");
		organization.getUrl().set("https://github.com/jcohy");
	}


	/**
	 * 定义 licenses
	 *
	 * @param licenses licenses
	 */
	private void customizeLicenses(MavenPomLicenseSpec licenses) {
		licenses.license(licence -> {
			licence.getName().set("Apache License, Version 2.0");
			licence.getUrl().set("https://www.apache.org/licenses/LICENSE-2.0");
		});
	}


	/**
	 * 定义开发者
	 *
	 * @param developers developers
	 */
	private void customizeDevelopers(MavenPomDeveloperSpec developers) {
		developers.developer(developer -> {
			developer.getName().set("jiachao23");
			developer.getEmail().set("jia_chao23@126.com");
			developer.getOrganization().set("jcohy");
			developer.getOrganizationUrl().set("https://github.com/jcohy");
			developer.getRoles().set(Collections.singletonList("Project lead"));
		});
	}


	/**
	 * 定义 scm
	 *
	 * @param scm     scm
	 */
	private void customizeScm(MavenPomScm scm) {
		scm.getConnection().set("scm:git:git://github.com/jcohy/jcohy-gradle-plugins");
		scm.getDeveloperConnection().set("scm:git:git://github.com/jcohy/jiachao23");
		scm.getUrl().set("https://github.com/jcohy/jcohy-gradle-plugins.git");
	}

	/**
	 * 定义 issueManagement
	 *
	 * @param issueManagement issueManagement
	 */
	private void customizeIssueManagement(MavenPomIssueManagement issueManagement) {
		issueManagement.getSystem().set("GitHub");
		issueManagement.getUrl().set("https://github.com/jcohy/jcohy-gradle-plugins/issues");
	}

	private void customizeJavaPlugin(Project project) {
		project.getPlugins().withType(JavaPlugin.class, (javaPlugin) -> {
			JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
			extension.withJavadocJar();
			extension.withSourcesJar();
		});
	}

	/**
	 * 添加发布兼容性警告
	 *
	 * @param publication publication
	 */
	private void suppressMavenOptionalFeatureWarnings(MavenPublication publication) {
		publication.suppressPomMetadataWarningsFor("mavenOptionalApiElements");
		publication.suppressPomMetadataWarningsFor("mavenOptionalRuntimeElements");
	}


	/**
	 * 允许在 pom 中添加 optional 依赖，这是使 Eclipse 中的 m2e 所必需的。
	 *
	 * @param project 要添加功能的项目
	 */
	private void addMavenOptionalFeature(Project project) {
		JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
		extension.registerFeature("mavenOptional", (feature) -> {
			feature.usingSourceSet(extension.getSourceSets().getByName("main"));
		});

		AdhocComponentWithVariants javaComponent = (AdhocComponentWithVariants) project.getComponents()
				.findByName("java");
		if (javaComponent != null) {
			javaComponent.addVariantsFromConfiguration(
					Objects.requireNonNull(project.getConfigurations().findByName("mavenOptionalRuntimeElements")),
					ConfigurationVariantDetails::mapToOptional);
		}
	}

	/**
	 * 版本解析
	 *
	 * @param publication publication
	 * @param project     project
	 */
	private void customizeJavaMavenPublication(MavenPublication publication, Project project) {
		addMavenOptionalFeature(project);
		publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_API, (mappingStrategy) -> mappingStrategy
				.fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)));
		publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_RUNTIME,
				VariantVersionMappingStrategy::fromResolutionResult));
	}
}
