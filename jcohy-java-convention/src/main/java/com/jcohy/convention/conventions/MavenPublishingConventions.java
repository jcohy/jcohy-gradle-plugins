package com.jcohy.convention.conventions;

import com.jcohy.convention.constant.PomConstant;
import com.jcohy.convention.maven.ReleaseStatus;
import com.jcohy.convention.maven.Repository;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.component.AdhocComponentWithVariants;
import org.gradle.api.component.ConfigurationVariantDetails;
import org.gradle.api.plugins.JavaPlatformPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
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
import org.gradle.api.tasks.bundling.Jar;

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
 * @version 0.0.5.1 2021/6/11:17:08
 * @since 0.0.5.1
 */
class MavenPublishingConventions {

    private PomExtension pomExtension = null;

    void apply(Project project) {

        pomExtension = new PomExtension(project.getRootProject());
        project.getRootProject().getExtensions().add(PomExtension.POM_EXTENSION,pomExtension);

        project.getPlugins().withType(MavenPublishPlugin.class).all((mavenPublishPlugin) -> {
            PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
            configurationDeploy(project);
            project.getPlugins().withType(JavaPlugin.class).all(javaPlugin -> {
                JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
                extension.withJavadocJar();
                extension.withSourcesJar();
            });

            project.afterEvaluate( p -> {
                publishing.getPublications().withType(MavenPublication.class)
                        .all((mavenPublication ->
                                customizeMavenPublication(mavenPublication, project)));
            });
        });
    }

    private void configurationDeploy(Project project) {

        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        MavenPublication mavenPublication = publishing.getPublications().create("maven", MavenPublication.class);

        project.afterEvaluate((evaluated) -> {
            PomExtension pomExtension = project.getRootProject().getExtensions().getByType(PomExtension.class);
            publishing.getRepositories().maven(mavenRepository -> {
                Repository repository = Repository.of(ReleaseStatus.ofProject(project));
                mavenRepository.setUrl(repository.getUrl());
                mavenRepository.setName(repository.getName());
                mavenRepository.credentials((passwordCredentials -> {
                    passwordCredentials.setUsername(pomExtension.getUsername() != null ? pomExtension.getUsername() : PomConstant.NEXUS_USER_NAME);
                    passwordCredentials.setPassword(pomExtension.getPassword() != null ? pomExtension.getPassword() : PomConstant.NEXUS_PASSWORD);
                }));
            });

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

    /**
     * 自定义 maven 满足 Maven Central 的要求.
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
     * 添加发布兼容性警告.
     * @param publication publication
     */
    private void suppressMavenOptionalFeatureWarnings(MavenPublication publication) {
        publication.suppressPomMetadataWarningsFor("mavenOptionalApiElements");
        publication.suppressPomMetadataWarningsFor("mavenOptionalRuntimeElements");
    }

    /**
     * 版本解析.
     * @param publication publication
     * @param project project
     */
    private void customizeJavaMavenPublication(MavenPublication publication, Project project) {
        addMavenOptionalFeature(project);
        publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_API, (mappingStrategy) -> mappingStrategy
                .fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)));
        publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_RUNTIME,
                VariantVersionMappingStrategy::fromResolutionResult));
    }

    /**
     * 允许在 pom 中添加 optional 依赖，这是使 Eclipse 中的 m2e 所必需的.
     * @param project 要添加功能的项目
     */
    private void addMavenOptionalFeature(Project project) {
        JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
        JavaPluginConvention convention = project.getConvention().getPlugin(JavaPluginConvention.class);
        extension.registerFeature("mavenOptional", (feature) -> {
            feature.usingSourceSet(convention.getSourceSets().getByName("main"));
        });

        AdhocComponentWithVariants javaComponent = (AdhocComponentWithVariants) project.getComponents()
                .findByName("java");
        javaComponent.addVariantsFromConfiguration(
                project.getConfigurations().findByName("mavenOptionalRuntimeElements"),
                ConfigurationVariantDetails::mapToOptional);
    }

    /**
     * 定义 pom 文件.
     * @param pom pom
     * @param project project
     */
    private void customizePom(MavenPom pom, Project project) {
        pom.getUrl().set(pomExtension.getGitUrl() != null ? pomExtension.getGitUrl() : PomConstant.GIT_URL);
        pom.getName().set(project.provider(project::getName));
        pom.getDescription().set(project.provider(project::getDescription));
        if (!isUserInherited(project)) {
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
     * 定义 issueManagement.
     * @param issueManagement issueManagement
     */
    private void customizeIssueManagement(MavenPomIssueManagement issueManagement) {
        issueManagement.getSystem().set(pomExtension.getIssueSystem() != null ? pomExtension.getIssueSystem() : PomConstant.ISSUE_SYSTEM);
        issueManagement.getUrl().set(pomExtension.getIssueUrl() != null ? pomExtension.getIssueUrl() : PomConstant.ISSUE_URL);
    }

    /**
     * 定义 scm.
     * @param scm scm
     * @param project project
     */
    private void customizeScm(MavenPomScm scm, Project project) {
        if (!isUserInherited(project)) {
            scm.getConnection().set(pomExtension.getScmConnection() != null ? pomExtension.getScmConnection() : PomConstant.GIT_SCM_CONNECTION);
            scm.getDeveloperConnection().set(pomExtension.getScmDeveloperConnection() != null ? pomExtension.getScmDeveloperConnection() : PomConstant.GIT_SCM_DEVELOPER_CONNECTION);
        }
        scm.getUrl().set(pomExtension.getGitUrl() != null ? pomExtension.getGitUrl() : PomConstant.GIT_URL);
    }

    /**
     * 定义开发者.
     * @param developers developers
     */
    private void customizeDevelopers(MavenPomDeveloperSpec developers) {
        developers.developer(developer -> {
            developer.getName().set(pomExtension.getDeveloperName() != null ? pomExtension.getDeveloperName() : PomConstant.DEVELOPER_NAME);
            developer.getEmail().set(pomExtension.getDeveloperEmail() != null ? pomExtension.getDeveloperEmail() : PomConstant.DEVELOPER_EMAIL);
            developer.getOrganization().set(pomExtension.getOrganizationName() != null ? pomExtension.getOrganizationName() : PomConstant.POM_ORGANIZATION_NAME);
            developer.getOrganizationUrl().set(pomExtension.getOrganizationUrl() != null ? pomExtension.getOrganizationUrl() : PomConstant.POM_ORGANIZATION_URL);
        });
    }

    /**
     * 定义 licenses.
     * @param licenses licenses
     */
    private void customizeLicenses(MavenPomLicenseSpec licenses) {
        licenses.license(licence -> {
            licence.getName().set(pomExtension.getLicenseName() != null ? pomExtension.getLicenseName() : PomConstant.LICENSE_NAME);
            licence.getUrl().set(pomExtension.getLicenseUrl() != null ? pomExtension.getLicenseUrl() : PomConstant.LICENSE_URL);
        });
    }

    /**
     * 定义组织.
     * @param organization  organization
     */
    private void customizeOrganization(MavenPomOrganization organization) {
        organization.getName().set(pomExtension.getOrganizationName() != null ? pomExtension.getOrganizationName() : PomConstant.POM_ORGANIZATION_NAME);
        organization.getUrl().set(pomExtension.getOrganizationUrl() != null ? pomExtension.getOrganizationUrl() : PomConstant.POM_ORGANIZATION_URL);
    }

    /**
     * 排除.
     * @param project project
     * @return /
     */
    private boolean isUserInherited(Project project) {
        return "jcohy-framework-bom".equals(project.getName());
    }

}
