package com.jcohy.convention.conventions;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.gradle.jvm.AbstractAsciidoctorTask;
import org.asciidoctor.gradle.jvm.AsciidoctorJExtension;
import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Sync;

import org.springframework.util.StringUtils;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 在 {@link AsciidoctorJPlugin} 存在的情况下应用的约定。 应用插件时：
 *
 * <ul>
 * <li>配置 {@code https://repo.spring.io/release} 仓库，并限制只能引入以下组的依赖:
 * <ul>
 * <li>{@code io.spring.asciidoctor}
 * <li>{@code io.spring.asciidoctor.backends}
 * <li>{@code io.spring.docresources}
 * </ul>
 * <li>设置所有的警告都是致命的.
 * <li> AsciidoctorJ 版本更新为 2.4.3.
 * <li>创建一个 {@code asciidoctorExtensions} configuration.
 * <li>对于每个 {@link AsciidoctorTask} (HTML only):
 * <ul>
 * <li>创建一个任务以将文档资源同步到其输出目录。
 * <li>配置 {@code doctype} {@link AsciidoctorTask#options(Map) option}.
 * <li>配置 {@code backend}.
 * </ul>
 * <li>对于每个 {@link AsciidoctorTask} (PDF only):
 * <ul>
 * <li> 添加中文支持。
 * </ul>
 * <li>对于每个 {@link AbstractAsciidoctorTask} (HTML 和 PDF):
 * <ul>
 * <li>{@link AsciidoctorTask#attributes(Map) Attributes} are configured to enable
 * warnings for references to missing attributes, etc.
 * <li>{@link AbstractAsciidoctorTask#baseDirFollowsSourceDir() baseDirFollowsSourceDir()}
 * is enabled.
 * <li>{@code asciidoctorExtensions} is added to the task's configurations.
 * </ul>
 * </ul>
 * @author jiac
 * @version 1.0.0 2021/7/2:12:25
 * @since 1.0.0
 */
public class AsciidoctorConventions {

    private static final String ASCIIDOCTORJ_VERSION = "2.4.3";

    private static final String EXTENSIONS_CONFIGURATION_NAME = "asciidoctorExtensions";

    void apply(Project project){
        project.getPlugins().withType(AsciidoctorJPlugin.class,(asciidoctorPlugin) -> {
            configureDocumentationDependenciesRepository(project);
            makeAllWarningsFatal(project);
            upgradeAsciidoctorJVersion(project);
            createAsciidoctorExtensionsConfiguration(project);
            createAsciidoctorPdfTask(project);
            project.getTasks().withType(AbstractAsciidoctorTask.class,
                    (asciidoctorTask) -> configureAsciidoctorTask(project, asciidoctorTask));
        });
    }

    /**
     * 配置 asciidoctorTask
     * @param project project
     * @param asciidoctorTask asciidoctorTask
     */
    private void configureAsciidoctorTask(Project project, AbstractAsciidoctorTask asciidoctorTask){
        asciidoctorTask.configurations(EXTENSIONS_CONFIGURATION_NAME);
        configureCommonAttributes(project, asciidoctorTask);
        configureOptions(asciidoctorTask);
        asciidoctorTask.baseDirFollowsSourceDir();
        createSyncDocumentationSourceTask(project, asciidoctorTask);
        if (asciidoctorTask instanceof AsciidoctorTask) {
            boolean pdf = asciidoctorTask.getName().toLowerCase().contains("pdf");
            if(!pdf){
                replaceLogo(project,asciidoctorTask);
            }
            String backend = (!pdf) ? "spring-html" : "spring-pdf";
            ((AsciidoctorTask) asciidoctorTask).outputOptions((outputOptions) -> outputOptions.backends(backend));
        }
    }

    private void replaceLogo(Project project, AbstractAsciidoctorTask asciidoctorTask) {
        // 替换 logo
        asciidoctorTask.doLast((replaceIcon) -> {
            String language = asciidoctorTask.getLanguages().contains("zh-cn") ? "/zh-cn": "";
            project.delete(project.getBuildDir() + "/docs/asciidoc" + language+ "/img/banner-logo.svg");
            try {
                Files.copy(AsciidoctorConventions.class.getResourceAsStream("/data/images/banner-logo.svg"),
                        Paths.get(project.getBuildDir() + "/docs/asciidoc" + language+ "/img/banner-logo.svg"));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createAsciidoctorPdfTask(Project project) {
        project.getTasks().register("asciidoctorPdf",AsciidoctorTask.class,(asciidoctorPdf) -> {
            asciidoctorPdf.sources("index.adoc");
            configureAsciidoctorPdfTask(project,asciidoctorPdf);
        });
    }

    private void configureAsciidoctorPdfTask(Project project, AsciidoctorTask asciidoctorPdf) {
        try {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("pdf-fontsdir", AsciidoctorConventions.class.getResource("/data/fonts").toURI());
            attributes.put("pdf-stylesdir",AsciidoctorConventions.class.getResource("/data/themes").toURI());
            attributes.put("pdf-style","Chinese");
            asciidoctorPdf.attributes(attributes);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加文档依赖仓库
     * @param project project
     */
    private void configureDocumentationDependenciesRepository(Project project) {
        project.getRepositories().maven((mavenRepo) -> {
            mavenRepo.setUrl(URI.create("https://repo.spring.io/release"));
            mavenRepo.mavenContent((mavenContent) -> {
                mavenContent.includeGroup("io.spring.asciidoctor");
                mavenContent.includeGroup("io.spring.asciidoctor.backends");
                mavenContent.includeGroup("io.spring.docresources");
            });
        });
    }

    /**
     *  添加警告错误
     * @param project project
     */
    private void makeAllWarningsFatal(Project project) {
        project.getExtensions().getByType(AsciidoctorJExtension.class).fatalWarnings(".*");
    }

    /**
     * 创建 asciidoctorExtensions 配置
     * @param project project
     */
    private void createAsciidoctorExtensionsConfiguration(Project project) {
        project.getConfigurations().create(EXTENSIONS_CONFIGURATION_NAME, (configuration) -> {
            project.getConfigurations().matching((candidate) -> "dependencyManagement".equals(candidate.getName()))
                    .all(configuration::extendsFrom);
            configuration.getDependencies().add(project.getDependencies()
                    .create("io.spring.asciidoctor.backends:spring-asciidoctor-backends:0.0.2"));
            configuration.getDependencies()
                    .add(project.getDependencies().create("org.asciidoctor:asciidoctorj-pdf:1.5.3"));
        });
    }


    /**
     * AsciidoctorJ 版本为 2.4.3.
     * @param project project
     */
    private void upgradeAsciidoctorJVersion(Project project) {
        project.getExtensions().getByType(AsciidoctorJExtension.class).setVersion(ASCIIDOCTORJ_VERSION);
    }

    /**
     * 设置文档的 doctpye 类型
     * @param asciidoctorTask asciidoctorTask
     */
    private void configureOptions(AbstractAsciidoctorTask asciidoctorTask) {
        asciidoctorTask.options(Collections.singletonMap("doctype", "book"));
    }

    /**
     * 设置通用属性
     * @param project project
     * @param asciidoctorTask asciidoctorTask
     */
    private void configureCommonAttributes(Project project, AbstractAsciidoctorTask asciidoctorTask) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("doc-url", "http://docs.jcohy.com");
        attributes.put("resource-url", "http://resources.jcohy.com");
        attributes.put("software-url", "http://software.jcohy.com");
        attributes.put("study-url", "http://study.jcohy.com");
        attributes.put("project-url", "http://project.jcohy.com");
        attributes.put("allow-uri-read", true);
        attributes.put("revnumber", null);
        asciidoctorTask.attributes(attributes);

    }

    /**
     * 异步创建文档源文件
     * @param project project
     * @param asciidoctorTask asciidoctorTask
     * @return /
     */
    private Sync createSyncDocumentationSourceTask(Project project, AbstractAsciidoctorTask asciidoctorTask) {
        Sync syncDocumentationSource = project.getTasks()
                .create("syncDocumentationSourceFor" + StringUtils.capitalize(asciidoctorTask.getName()), Sync.class);
        File syncedSource = new File(project.getBuildDir(), "docs/src/" + asciidoctorTask.getName());
        syncDocumentationSource.setDestinationDir(syncedSource);
        syncDocumentationSource.from("src/docs");
        asciidoctorTask.dependsOn(syncDocumentationSource);
        asciidoctorTask.getInputs().dir(syncedSource);
        asciidoctorTask.setSourceDir(project.relativePath(new File(syncedSource, "asciidoc/")));
        return syncDocumentationSource;
    }
}
