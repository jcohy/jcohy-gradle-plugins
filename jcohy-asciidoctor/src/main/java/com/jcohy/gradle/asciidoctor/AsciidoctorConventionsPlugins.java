package com.jcohy.gradle.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.asciidoctor.gradle.jvm.AsciidoctorJExtension;
import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.Sync;

import org.springframework.util.StringUtils;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/10/12:15:15
 * @since 2022.0.1
 */
public class AsciidoctorConventionsPlugins implements Plugin<Project> {

    private static final String ASCIIDOCTORJ_VERSION = "2.4.3";

    private static final String EXTENSIONS_CONFIGURATION_NAME = "asciidoctorExtensions";

    @Override
    public void apply(Project project) {
        project.getPlugins().withType(AsciidoctorJPlugin.class,(asciidoctorJPlugin -> {
            configureDocumentationDependenciesRepository(project);
            makeAllWarningsFatal(project);
            upgradeAsciidoctorJVersion(project);
            createAsciidoctorExtensionsConfiguration(project);
            createAsciidoctorPdfTask(project);
            configurationAsciidoctorTask(project);
        }));
    }

    /**
     * 添加依赖仓库坐标
     * @param project project
     */
    private void configureDocumentationDependenciesRepository(Project project) {
        project.getRepositories().maven((mavenArtifactRepository -> {
            mavenArtifactRepository.setUrl(URI.create("https://repo.spring.io/release"));
            mavenArtifactRepository.mavenContent((mavenContent) -> {
                mavenContent.includeGroup("io.spring.asciidoctor");
                mavenContent.includeGroup("io.spring.asciidoctor.backends");
                mavenContent.includeGroup("io.spring.docresources");
            });
        }));
    }

    /**
     * 配置 {@link AsciidoctorTask}
     * @param project project
     */
    private void configurationAsciidoctorTask(Project project) {
        project.getTasks().withType(AsciidoctorTask.class,(asciidoctorTask) -> {
            asciidoctorTask.configurations(EXTENSIONS_CONFIGURATION_NAME);
            configureCommonAttributes(project,asciidoctorTask);
            configureOptions(asciidoctorTask);
            asciidoctorTask.baseDirFollowsSourceDir();
            createSyncDocumentationSourceTask(project,asciidoctorTask);
            boolean pdf = asciidoctorTask.getName().toLowerCase().contains("pdf");
            if(!pdf) {
                replaceLogo(project,asciidoctorTask);
            }
            String backend = (!pdf) ? "spring-html" : "spring-pdf";
            asciidoctorTask.outputOptions(outputOptions -> outputOptions.backends(backend));
        });
    }

    /**
     * 替换 spring logo 为自己的 logo.
     * @param project project
     * @param asciidoctorTask asciidoctorTask
     */
    private void replaceLogo(Project project, AsciidoctorTask asciidoctorTask) {
        asciidoctorTask.doLast((replaceLogo) -> {
            try {
                String language = asciidoctorTask.getLanguages().contains("zh-cn") ? "/zh-cn" : "";
                project.delete(project.getBuildDir() + "/docs/asciidoc/" + language + "/img/banner-logo.svg");
                Files.copy(Objects.requireNonNull(this.getClass().getResourceAsStream("/data/images/banner-logo.svg")),
                        Paths.get(project.getBuildDir() + "/docs/asciidoc/" + language + "/img/banner-logo.svg"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    /**
     * 复制源文件
     * @param project project
     * @param asciidoctorTask asciidoctorTask
     */
    private Sync createSyncDocumentationSourceTask(Project project, AsciidoctorTask asciidoctorTask) {
        Sync syncDocumentationSource = project.getTasks()
                .create("syncDocumentationSourceFor" + StringUtils.capitalize(asciidoctorTask.getName()), Sync.class);
        File syncSource = new File(project.getBuildDir(),"docs/src/" + asciidoctorTask.getName());

        syncDocumentationSource.setDestinationDir(syncSource);

        syncDocumentationSource.from("src/docs");
        syncDocumentationSource.from("src/main/java",(spec) -> {
            spec.into("main/java");
        });
        syncDocumentationSource.from("src/main/groovy",(spec) -> {
            spec.into("main/groovy");
        });
        syncDocumentationSource.from("src/main/kotlin",(spec) -> {
            spec.into("main/kotlin");
        });
        syncDocumentationSource.from("src/test/java",(spec) -> {
            spec.into("test/java");
        });
        syncDocumentationSource.from("src/test/groovy",(spec) -> {
            spec.into("test/groovy");
        });
        syncDocumentationSource.from("src/test/kotlin",(spec) -> {
            spec.into("test/kotlin");
        });
        syncDocumentationSource.from("src/main/resources",(spec) -> {
            spec.into("main/resources");
        });
        syncDocumentationSource.from("src/test/resources",(spec) -> {
            spec.into("test/resources");
        });

        asciidoctorTask.dependsOn(syncDocumentationSource);
        asciidoctorTask.getInputs().dir(syncSource).withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName("synced source");
        asciidoctorTask.setSourceDir(project.relativePath(new File(syncSource,"asciidoc/")));
        return syncDocumentationSource;
    }

    /**
     * 配置文档的 doctype 类型
     * @param asciidoctorTask asciidoctorTask
     */
    private void configureOptions(AsciidoctorTask asciidoctorTask) {
        asciidoctorTask.options(Collections.singletonMap("doctype","book"));
        asciidoctorTask.setLogDocuments(true);
    }

    /**
     * 配置 {@link AsciidoctorTask} 通用属性
     * @param project project
     * @param asciidoctorTask asciidoctorTask
     */
    private void configureCommonAttributes(Project project, AsciidoctorTask asciidoctorTask) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("idprefix", "");
        attributes.put("idseparator", "-");
        attributes.put("toc", "left");
        attributes.put("toclevels", 4);
        attributes.put("tabsize", 4);
        attributes.put("numbered", "");
        attributes.put("source-indent", 0);
        attributes.put("sectanchors", "");
        attributes.put("icons", "font");
        attributes.put("hide-uri-scheme", "font");
        attributes.put("allow-uri-read", true);
        attributes.put("version",attributes.get("version") != null ? attributes.get("version"): project.getVersion());
        attributes.put("revnumber", attributes.get("revnumber") != null ? attributes.get("version"): project.getVersion());
        attributes.put("docinfo", "shared,private");
        attributes.put("attribute-missing", "warn");

        attributes.put("docs-url", "https://docs.jcohy.com");
        attributes.put("resource-url", "https://resources.jcohy.com");
        attributes.put("software-url", "https://software.jcohy.com");
        attributes.put("study-url", "https://study.jcohy.com");
        attributes.put("project-url", "https://project.jcohy.com");
        asciidoctorTask.attributes(attributes);
    }

    /**
     * 创建 asciidoctorPdf task.
     * @param project project
     */
    private void createAsciidoctorPdfTask(Project project) {
        project.getTasks().register("asciidoctorPdf", AsciidoctorTask.class,(asciidoctorPdf -> {
            asciidoctorPdf.sources("*.singleadoc");
            // 添加属性，解决中文乱码问题
            try {
                Map<String,Object> attributes = new HashMap<>();
                attributes.put("pdf-fontsdir", Objects.requireNonNull(this.getClass().getResource("/data/fonts")).toURI());
                attributes.put("pdf-stylesdir", Objects.requireNonNull(this.getClass().getResource("/data/themes")).toURI());
                attributes.put("pdf-style","Chinese");
                asciidoctorPdf.attributes(attributes);
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * 创建并配置 asciidoctorExtensions
     * @param project project
     */
    private void createAsciidoctorExtensionsConfiguration(Project project) {
        // asciidoctorExtensions 配置继承 dependencyManagement
        project.getConfigurations().create(EXTENSIONS_CONFIGURATION_NAME,(configuration) -> {
           project.getConfigurations().matching((candidate) -> "dependencyManagement".equals(candidate.getName()))
                   .all(configuration::extendsFrom);
           // 添加 spring-asciidoctor-backends 依赖
           configuration.getDependencies().add(project.getDependencies()
                   .create("io.spring.asciidoctor.backends:spring-asciidoctor-backends:0.0.3"));
            // 添加 asciidoctorj-pdf 依赖
            configuration.getDependencies().add(project.getDependencies()
                    .create("org.asciidoctor:asciidoctorj-pdf:1.5.3"));
        });
    }

    /**
     * 升级版本 {@code ASCIIDOCTORJ_VERSION}
     * @param project project
     */
    private void upgradeAsciidoctorJVersion(Project project) {
        project.getExtensions().getByType(AsciidoctorJExtension.class).setVersion(ASCIIDOCTORJ_VERSION);

    }

    /**
     * 添加错误警告
     * @param project project
     */
    private void makeAllWarningsFatal(Project project) {
        project.getExtensions().getByType(AsciidoctorJExtension.class).fatalWarnings(".*");
    }
}
