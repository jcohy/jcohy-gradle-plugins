package com.jcohy.gradle.plugins.conventions;

import com.jcohy.gradle.plugins.constant.PomConstant;
import com.jcohy.gradle.plugins.version.ReleaseStatus;
import com.jcohy.gradle.plugins.version.Repository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPomDeveloperSpec;
import org.gradle.api.publish.maven.MavenPomIssueManagement;
import org.gradle.api.publish.maven.MavenPomLicenseSpec;
import org.gradle.api.publish.maven.MavenPomOrganization;
import org.gradle.api.publish.maven.MavenPomScm;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 当 {@link MavenPublishPlugin} 插件存在时使用
 *
 * <ul>
 * <li>设置 {@link MavenArtifactRepository Maven artifact repository} 仓库.
 * <li>所有的 {@link MavenPublication Maven publications} 都满足 Maven Central 的要求.
 * <li>如果使用了 {@link JavaPlugin Java plugin} 插件，则:
 * <ul>
 * <li> 创建  Javadoc 和 source jars。
 * <li>发布元数据 (poms 和 Gradle module metadata),配置为使用解析的版本.
 * </ul>
 * </ul>
 *
 * @author jiac
 * @version 1.0.0 2021/6/11:17:08
 * @since 1.0.0
 */
class MavenPublishingConventions {

	void apply(Project project){
		project.getPlugins().withType(MavenPublishPlugin.class).all((mavenPublishPlugin) -> {
			PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);

			if(project.hasProperty("deploymentRepository")){
				publishing.getRepositories().maven(mavenRepository -> {
					Repository repository = Repository.of(ReleaseStatus.ofProject(project));
					mavenRepository.setUrl(repository.getUrl());
					mavenRepository.setName(repository.getName());
					mavenRepository.credentials( (passwordCredentials -> {
						passwordCredentials.setUsername(PomConstant.NEXUS_USER_NAME);
						passwordCredentials.setPassword(PomConstant.NEXUS_PASSWORD);
					}));
				});
			}

//			publishing.getRepositories().maven( mavenRepository -> {
//				if(mavenRepository.getUrl().toString().startsWith("http://192.168.11.230:8081/repository")){
//					mavenRepository.credentials( (passwordCredentials -> {
//						passwordCredentials.setUsername(PomConstant.NEXUS_USER_NAME);
//						passwordCredentials.setPassword(PomConstant.NEXUS_PASSWORD);
//					}));
//				}
//			});
			publishing.getPublications().withType(MavenPublication.class)
					.all((mavenPublication ->
						customizeMavenPublication(mavenPublication, project)));

			project.getPlugins().withType(JavaPlugin.class).all(javaPlugin -> {
				JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
				extension.withJavadocJar();
				extension.withSourcesJar();
			});
		});
	}

	/**
	 * 自定义 maven 满足 Maven Central 的要求
	 * @param publication publication
	 * @param project project
	 */
	private void customizeMavenPublication(MavenPublication publication, Project project) {
		customizePom(publication.getPom(), project);
		project.getPlugins().withType(JavaPlugin.class)
				.all(javaPlugin -> customizeJavaMavenPublication(publication, project));
		suppressMavenOptionalFeatureWarnings(publication);
	}

	/**
	 * 添加发布兼容性警告
	 * @param publication publication
	 */
	private void suppressMavenOptionalFeatureWarnings(MavenPublication publication) {
		publication.suppressPomMetadataWarningsFor("mavenOptionalApiElements");
		publication.suppressPomMetadataWarningsFor("mavenOptionalRuntimeElements");
	}

	/**
	 * 版本解析
	 * @param publication publication
	 * @param project project
	 */
	private void customizeJavaMavenPublication(MavenPublication publication, Project project) {
		addMavenOptionalFeature(project);
		publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_API, (mappingStrategy) -> mappingStrategy
				.fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)));
		publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_RUNTIME,
				(mappingStrategy) -> mappingStrategy.fromResolutionResult()));
	}

	/**
	 * 允许在 pom 中添加 optional 依赖，这是使 Eclipse 中的 m2e 所必需的。
	 * @param project 要添加功能的项目
	 */
	private void addMavenOptionalFeature(Project project) {
		JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
		JavaPluginConvention convention = project.getConvention().getPlugin(JavaPluginConvention.class);
		extension.registerFeature("mavenOptional",(feature) -> {
			feature.usingSourceSet(convention.getSourceSets().getByName("main"));
		});

		AdhocComponentWithVariants javaComponent = (AdhocComponentWithVariants) project.getComponents()
				.findByName("java");
		javaComponent.addVariantsFromConfiguration(
				project.getConfigurations().findByName("mavenOptionalRuntimeElements"),
				ConfigurationVariantDetails::mapToOptional);
	}

	/**
	 * 定义 pom 文件
	 * @param pom pom
	 * @param project project
	 */
	private void customizePom(MavenPom pom, Project project) {
		pom.getUrl().set(PomConstant.GIT_URL);
		pom.getName().set(project.provider(project::getName));
		pom.getDescription().set(project.provider(project::getDescription));
		if(!isUserInherited(project)){
			pom.organization(this::customizeOrganization);
		}

		pom.licenses(this::customizeLicenses);
		pom.developers(this::customizeDevelopers);
		pom.scm((scm) -> customizeScm(scm, project));
		if (!isUserInherited(project)) {
			pom.issueManagement(this::customizeIssueManagement);
		}
	}

	/**
	 * 定义 issueManagement
	 * @param issueManagement issueManagement
	 */
	private void customizeIssueManagement(MavenPomIssueManagement issueManagement) {
		issueManagement.getSystem().set(PomConstant.ISSUE_SYSTEM);
		issueManagement.getUrl().set(PomConstant.ISSUE_URL);
	}

	/**
	 * 定义 scm
	 * @param scm scm
	 * @param project project
	 */
	private void customizeScm(MavenPomScm scm, Project project) {
		if (!isUserInherited(project)) {
			scm.getConnection().set(PomConstant.GIT_SCM_CONNECTION);
			scm.getDeveloperConnection().set(PomConstant.GIT_SCM_DEVELOPER_CONNECTION);
		}
		scm.getUrl().set(PomConstant.GIT_URL);
	}

	/**
	 * 定义开发者
	 * @param developers developers
	 */
	private void customizeDevelopers(MavenPomDeveloperSpec developers) {
		developers.developer( developer -> {
			developer.getName().set(PomConstant.DEVELOPER_NAME);
			developer.getEmail().set(PomConstant.DEVELOPER_EMAIL);
			developer.getOrganization().set(PomConstant.POM_ORGANIZATION_NAME);
			developer.getOrganizationUrl().set(PomConstant.POM_ORGANIZATION_URL);
		});
	}

	/**
	 * 定义 licenses
	 * @param licenses licenses
	 */
	private void customizeLicenses(MavenPomLicenseSpec licenses) {
		licenses.license( licence -> {
			licence.getName().set(PomConstant.LICENSE_NAME);
			licence.getUrl().set(PomConstant.LICENSE_URL);
		});
	}

	/**
	 * 定义组织
	 * @param organization  organization
	 */
	private void customizeOrganization(MavenPomOrganization organization) {
		organization.getName().set(PomConstant.POM_ORGANIZATION_NAME);
		organization.getUrl().set(PomConstant.POM_ORGANIZATION_URL);
	}

	/**
	 * 排除
	 * @param project project
	 * @return /
	 */
	private boolean isUserInherited(Project project) {
		return "flight-framework-bom".equals(project.getName());
	}

}
