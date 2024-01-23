package io.github.jcohy.gradle.asciidoctor;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.asciidoctor.gradle.jvm.AsciidoctorJExtension;
import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorJPdfPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.Sync;

import org.springframework.util.StringUtils;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/19 9:57
 * @since 2024.0.1
 */
public class JcohyAsciidoctorPlugin implements Plugin<Project> {

	public static final String ASCIIDOCTORJ_VERSION = "2.5.7";
	public static final String EXTENSIONS_CONFIGURATION_NAME = "asciidoctorExtensions";

	@Override
	public void apply(Project project) {
		// Apply asciidoctor plugin
		project.getPluginManager().apply(AsciidoctorJPlugin.class);
		project.getPluginManager().apply(AsciidoctorJPdfPlugin.class);

		project.getPlugins().withType(AsciidoctorJPlugin.class,(asciidoctorJPlugin -> {
			configureDocumentationDependenciesRepository(project);
			makeAllWarningsFatal(project);
			upgradeAsciidoctorJVersion(project);
			createAsciidoctorExtensionsConfiguration(project);
			createAsciidoctorPdfTask(project);
			createAsciidoctorMultiPageTask(project);
			configurationAsciidoctorTask(project);
		}));
	}

	/**
	 * 添加依赖仓库坐标
	 * @param project project
	 */
	private void configureDocumentationDependenciesRepository(Project project) {
		project.getRepositories().maven((mavenArtifactRepository -> {
			mavenArtifactRepository.setUrl(URI.create("https://maven.aliyun.com/repository/public"));
			mavenArtifactRepository.mavenContent((mavenContent) -> {
				mavenContent.includeGroup("io.spring.asciidoctor");
				mavenContent.includeGroup("io.spring.asciidoctor.backends");
				mavenContent.includeGroup("io.spring.docresources");
			});
		}));
		project.getRepositories().mavenCentral();
	}

	/**
	 * 设置所有的警告都是致命的
	 * @param project project
	 */
	private void makeAllWarningsFatal(Project project) {
		project.getExtensions().getByType(AsciidoctorJExtension.class).fatalWarnings(".*");
	}

	private void upgradeAsciidoctorJVersion(Project project) {
		project.getExtensions().getByType(AsciidoctorJExtension.class).setVersion(ASCIIDOCTORJ_VERSION);
	}

	/**
	 * 创建并配置 asciidoctorExtensions
	 * @param project project
	 */
	private void createAsciidoctorExtensionsConfiguration(Project project) {
		// asciidoctorExtensions 配置继承 dependencyManagement
		project.getConfigurations().create(EXTENSIONS_CONFIGURATION_NAME,(configuration) -> {
			// 主要针对 spring-boot-dependencies 的模块依赖管理
			project.getConfigurations().matching((candidate) -> "management".equals(candidate.getName()))
					.all(configuration::extendsFrom);
			// 添加 spring-asciidoctor-backends 依赖
			configuration.getDependencies().add(project.getDependencies().create("com.jcohy.asciidoctor.backends:spring-asciidoctor-backends:0.0.6.3"));
			// 添加 asciidoctorj-pdf 依赖
			configuration.getDependencies().add(project.getDependencies().create("org.asciidoctor:asciidoctorj-pdf:2.3.4"));
		});
	}

	/**
	 * 配置 {@link AsciidoctorTask}
	 * @param project project
	 */
	private void configurationAsciidoctorTask(Project project) {
		project.getTasks().withType(AsciidoctorTask.class,(asciidoctorTask) -> {
			asciidoctorTask.setGroup("documentation");
			asciidoctorTask.configurations(EXTENSIONS_CONFIGURATION_NAME);
			// 设置属性
			configureCommonAttributes(project,asciidoctorTask);
			configureOptions(asciidoctorTask);
			asciidoctorTask.baseDirFollowsSourceDir();

			// 设置 asciidoctor 和 asciidoctorPdf sources 为 index.singleadoc
			if(asciidoctorTask.getName().equals("asciidoctor") || asciidoctorTask.getName().equals("asciidoctorPdf")) {
				asciidoctorTask.sources("index.adoc");
			}

			if(asciidoctorTask.getName().equals("asciidoctorMultiPage")) {
				asciidoctorTask.sources("*.adoc","index.multiadoc");
				asciidoctorTask.sources(pattern -> {
					pattern.include("*.adoc","index.multiadoc");
					pattern.exclude("index.adoc");
				});
			}

			createSyncDocumentationSourceTask(project,asciidoctorTask);
			boolean pdf = asciidoctorTask.getName().toLowerCase().contains("pdf");
			String backend = (!pdf) ? "spring-html" : "spring-pdf";
			asciidoctorTask.outputOptions(outputOptions -> outputOptions.backends(backend));
		});
	}

	/**
	 * 复制源文件
	 *
	 * @param project         project
	 * @param asciidoctorTask asciidoctorTask
	 */
	private void createSyncDocumentationSourceTask(Project project, AsciidoctorTask asciidoctorTask) {
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
	}

	/**
	 * 配置文档的 doctype 类型
	 * @param asciidoctorTask asciidoctorTask
	 */
	private void configureOptions(AsciidoctorTask asciidoctorTask) {
		asciidoctorTask.options(Collections.singletonMap("doctype","book"));
		asciidoctorTask.forkOptions(fork -> {
			fork.jvmArgs("--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.base/java.io=ALL-UNNAMED");
		});
		asciidoctorTask.setLogDocuments(true);
	}

	/**
	 * 配置 {@link AsciidoctorTask} 通用属性
	 * @param project project
	 * @param asciidoctorTask asciidoctorTask
	 */
	private void configureCommonAttributes(Project project, AsciidoctorTask asciidoctorTask) {
		Map<String, Object> attributes = new HashMap<>();
		// https://docs.asciidoctor.org/asciidoc/latest/attributes/document-attributes-ref/
		// 文档元数据
		attributes.put("author", "Author：Jcohy");
		attributes.put("email","Email：jia_chao23@126.com");
		attributes.put("revnumber", attributes.get("revnumber") != null ? attributes.get("version"): project.getVersion());
		attributes.put("revdate", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now()));
		attributes.put("revremark", "");

		// 章节，标题和目录属性
		attributes.put("idprefix", "");
		attributes.put("idseparator", "-");
		attributes.put("toc", "left");
		attributes.put("toclevels", 4);
		attributes.put("tabsize", 4);
		attributes.put("numbered", "");
		attributes.put("source-indent", 0);
		attributes.put("sectanchors", "");
		attributes.put("hide-uri-scheme", "font");
		attributes.put("allow-uri-read", true);
		attributes.put("docinfo", "shared,private");

		// Image and icon attributes
		attributes.put("icons", "font");

		// Compliance attributes
//        attributes.put("attribute-missing", "warn");

		// Custom attributes
		attributes.put("version",attributes.get("version") != null ? attributes.get("version"): project.getVersion());
		attributes.put("image-resource", project.getBuildDir() + "/docs/src/"+ asciidoctorTask.getName() + "/images");
		attributes.put("docs-java", project.getProjectDir() + "/src/main/java");
		attributes.put("docs-kotlin", project.getProjectDir() + "/src/main/kotlin");
		attributes.put("docs-groovy", project.getProjectDir() + "/src/main/groovy");
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
		project.getTasks().withType(AsciidoctorTask.class,(asciidoctorPdf -> {
			// 添加属性，解决 PDF 中文乱码问题
			try {
				Map<String,Object> attributes = new HashMap<>();
				attributes.put("pdf-fontsdir", Objects.requireNonNull(this.getClass().getResource("/data/fonts")).toURI());
				attributes.put("pdf-themesdir", Objects.requireNonNull(this.getClass().getResource("/data/themes")).toURI());
				attributes.put("pdf-theme","Chinese");
				asciidoctorPdf.attributes(attributes);
			}
			catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}));
	}

	private void createAsciidoctorMultiPageTask(Project project) {
		project.getTasks().withType(AsciidoctorTask.class,asciidoctorTask -> {
			File multiFile = new File(asciidoctorTask.getSourceDir().getPath() + "/index.multiadoc");
			if(multiFile.exists()) {
				project.getTasks().register("asciidoctorMultiPage", AsciidoctorTask.class,(asciidoctorMultiPage -> {
				}));
			}
		});
	}
}
