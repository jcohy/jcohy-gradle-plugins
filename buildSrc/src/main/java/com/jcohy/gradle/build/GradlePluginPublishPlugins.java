package com.jcohy.gradle.build;

import com.jcohy.gradle.build.publishing.PomConstant;
import com.jcohy.gradle.build.publishing.Repository;
import org.gradle.api.Project;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.*;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.VariantVersionMappingStrategy;
import org.gradle.api.publish.maven.*;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.springframework.util.StringUtils;

public class GradlePluginPublishPlugins {
	void apply(Project project) {
        configureMavenPublish(project);
    }

    private void configureMavenPublish(Project project) {
        project.getPlugins().withType(MavenPublishPlugin.class).all((mavenPublishPlugin) -> {
            PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
            publishing.getRepositories().maven(mavenRepository -> {
                Repository repository = Repository.ofProject(project);
                mavenRepository.setUrl(repository.getUrl());
                mavenRepository.setName(repository.getName());
                mavenRepository.credentials((passwordCredentials -> {
                    String username = StringUtils.hasText(getExtraProperties(project,"username")) ? getExtraProperties(project,"username") : PomConstant.NEXUS_USER_NAME;
                    String password = StringUtils.hasText(getExtraProperties(project,"password")) ? getExtraProperties(project,"password") : PomConstant.NEXUS_PASSWORD;
                    passwordCredentials.setUsername(username);
                    passwordCredentials.setPassword(password);
                }));
            });

            publishing.getPublications().withType(MavenPublication.class)
                    .all(((mavenPublication) -> {
                        customizeMavenPublication(mavenPublication, project);
//                        SigningExtension extension = project.getExtensions().getByType(SigningExtension.class);
//                        extension.sign(mavenPublication);
                            }
                        ));

            project.getPlugins().withType(JavaPlugin.class).all(javaPlugin -> {
                JavaPluginExtension extension = project.getExtensions().getByType(JavaPluginExtension.class);
                extension.withJavadocJar();
                extension.withSourcesJar();
            });
        });
    }

    public String getExtraProperties(Project project,String property){
        ExtraPropertiesExtension extra = project.getExtensions().getExtraProperties();
        return extra.has(property) ? (String) extra.get(property) : "";
    }

    /**
     * 自定义 maven 满足 Maven Central 的要求
     *
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
     *
     * @param publication publication
     */
    private void suppressMavenOptionalFeatureWarnings(MavenPublication publication) {
        publication.suppressPomMetadataWarningsFor("mavenOptionalApiElements");
        publication.suppressPomMetadataWarningsFor("mavenOptionalRuntimeElements");
    }

    /**
     * 版本解析
     *
     * @param publication publication
     * @param project project
     */
    private void customizeJavaMavenPublication(MavenPublication publication, Project project) {
        publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_API, (mappingStrategy) -> mappingStrategy
                .fromResolutionOf(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)));
        publication.versionMapping((strategy) -> strategy.usage(Usage.JAVA_RUNTIME,
                VariantVersionMappingStrategy::fromResolutionResult));
    }

    /**
     * 定义 pom 文件
     *
     * @param pom pom
     * @param project project
     */
    private void customizePom(MavenPom pom, Project project) {
        pom.getUrl().set(PomConstant.GIT_URL);
        pom.getName().set(project.provider(project::getName));
        pom.getDescription().set(project.provider(project::getDescription));
        pom.organization(this::customizeOrganization);

        pom.licenses(this::customizeLicenses);
        pom.developers(this::customizeDevelopers);
        pom.scm((scm) -> customizeScm(scm, project));
        pom.issueManagement(this::customizeIssueManagement);
    }

    /**
     * 定义 issueManagement
     *
     * @param issueManagement issueManagement
     */
    private void customizeIssueManagement(MavenPomIssueManagement issueManagement) {
        issueManagement.getSystem().set(PomConstant.ISSUE_SYSTEM);
        issueManagement.getUrl().set(PomConstant.ISSUE_URL);
    }

    /**
     * 定义 scm
     *
     * @param scm scm
     * @param project project
     */
    private void customizeScm(MavenPomScm scm, Project project) {
        scm.getConnection().set(PomConstant.GIT_SCM_CONNECTION);
        scm.getDeveloperConnection().set(PomConstant.GIT_SCM_DEVELOPER_CONNECTION);
        scm.getUrl().set(PomConstant.GIT_URL);
    }

    /**
     * 定义开发者
     *
     * @param developers developers
     */
    private void customizeDevelopers(MavenPomDeveloperSpec developers) {
        developers.developer(developer -> {
            developer.getName().set(PomConstant.DEVELOPER_NAME);
            developer.getEmail().set(PomConstant.DEVELOPER_EMAIL);
            developer.getOrganization().set(PomConstant.POM_ORGANIZATION_NAME);
            developer.getOrganizationUrl().set(PomConstant.POM_ORGANIZATION_URL);
        });
    }

    /**
     * 定义 licenses
     *
     * @param licenses licenses
     */
    private void customizeLicenses(MavenPomLicenseSpec licenses) {
        licenses.license(licence -> {
            licence.getName().set(PomConstant.LICENSE_NAME);
            licence.getUrl().set(PomConstant.LICENSE_URL);
        });
    }

    /**
     * 定义组织
     *
     * @param organization organization
     */
    private void customizeOrganization(MavenPomOrganization organization) {
        organization.getName().set(PomConstant.POM_ORGANIZATION_NAME);
        organization.getUrl().set(PomConstant.POM_ORGANIZATION_URL);
    }
}
