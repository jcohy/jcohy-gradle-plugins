package com.jcohy.convention.conventions;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.jcohy.convention.constant.BomCoordinates;
import com.jcohy.convention.optional.OptionalDependenciesPlugin;
import com.jcohy.convention.testing.TestFailuresPlugin;
import com.jcohy.convention.toolchain.ToolchainPlugin;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import io.spring.javaformat.gradle.FormatTask;
import io.spring.javaformat.gradle.SpringJavaFormatPlugin;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.plugins.quality.CheckstylePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.jvm.tasks.Jar;
import org.gradle.testretry.TestRetryPlugin;
import org.gradle.testretry.TestRetryTaskExtension;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:  当使用 {@link JavaBasePlugin} 时的约定. 当使用此插件时:
 * <ul>
 * <li>{@code sourceCompatibility} 设置为 {@code 1.8}
 * <li>{@code targetCompatibility} 设置为 {@code 1.8}
 * <li>应用 {@link SpringJavaFormatPlugin Spring Java Format}, {@link CheckstylePlugin Checkstyle}, {@link TestFailuresPlugin Test Failures}, 和 {@link TestRetryPlugin TestRetry} 插件。
 *  {@link CheckstylePlugin Checkstyle} 插件使用我们自定义的代码规则检查配置对项目进行检查。代码检查规则查看 jcohy-checkstyle.xml 文件
 * <li>{@link Test} 任务使用 JUnit Platform 并且配置最大堆为 1024M
 * <li>{@link JavaCompile}, {@link Javadoc}, 和 {@link FormatTask} 任务编码为 UTF-8
 * <li>{@link JavaCompile} 任务配置为使用 {@code -parameters}, 并且当使用 Java8 时
 * <ul>
 * <li> 将警告视为错误
 * <li>启用 {@code unchecked}, {@code deprecation}, {@code rawtypes}, 和 {@code varags} 警告
 * </ul>
 * <li> 为每个项目配置以下 maven 仓库。
 * <ul>
 * <li> http://192.168.11.230:8081/repository/releases </li>
 * <li> http://192.168.11.230:8081/repository/snapshot </li>
 * <li> https://maven.aliyun.com/repository/central </li>
 * <li> https://repo.spring.io/artifactory/release </li>
 * </ul>
 *  <li>自动为项目添加一下依赖管理 BOM </li>
 * <ul>
 * <li> org.springframework.boot:spring-boot-dependencies:2.4.5 </li>
 * <li> com.alibaba.cloud:aliyun-spring-boot-dependencies:1.0.0 </li>
 * <li> com.alibaba.cloud:spring-cloud-alibaba-dependencies:2.2.2.RELEASE </li>
 * </ul>
 * <li>  在使用 {@link JavaPlugin} 插件的项目中添加以下测试相关的依赖, 并且测试最大重试次数为 {@code 3} 次 </li>
 * <ul>
 * <li> testRuntimeOnly(org.junit.platform:junit-platform-launcher) </li>
 * <li> testRuntimeOnly(org.junit.jupiter:junit-jupiter) </li>
 * <li> testRuntimeOnly(org.assertj:assertj-core) </li>
 * <li> testImplementation(org.springframework.boot:spring-boot-starter-test) </li>
 * </ul>
 * <li>{@link Jar} 任务的生成带有 LICENSE.txt,README.txt 和 NOTICE.txt 和以下清单属性:
 * <ul>
 * <li>{@code Automatic-Module-Name}
 * <li>{@code Build-Jdk-Spec}
 * <li>{@code Built-By}
 * <li>{@code Implementation-Title}
 * <li>{@code Implementation-Version}
 * </ul>
 * </ul>
 *
 * <p/>
 *
 * @author jiac
 * @version 1.0.0 2021/6/11:15:26
 * @since 1.0.0
 */
class JavaConventions {
    void apply(Project project) {
        project.getPlugins().withType(JavaBasePlugin.class, (java) -> {
            // 此插件用于生成测试失败报告
            project.getPlugins().apply(TestFailuresPlugin.class);
            // 为插件配置 SpringJavaFromat
            configureSpringJavaFormat(project);
            project.setProperty("sourceCompatibility", "1.8");
            configureMavenRepository(project);
            configureJavaCompileConventions(project);
            configureJavadocConventions(project);
            configureTestConventions(project);
            configureJarManifestConventions(project);
            configureDependencyManagement(project);
            configureToolchain(project);
        });
    }

    private void configureMavenRepository(Project project) {
        project.getRepositories().maven((mavenRepo) -> {
           mavenRepo.setUrl(URI.create("http://192.168.11.230:8081/repository/releases/"));
           mavenRepo.setName("xw-release");
        });

        project.getRepositories().maven((mavenRepo) -> {
            mavenRepo.setUrl(URI.create("http://192.168.11.230:8081/repository/snapshot"));
            mavenRepo.setName("xw-snapshot");
        });

        project.getRepositories().maven((mavenRepo) -> {
            mavenRepo.setUrl(URI.create("https://maven.aliyun.com/repository/central"));
            mavenRepo.setName("ali");
        });

        project.getRepositories().maven((mavenRepo) -> {
            mavenRepo.setUrl(URI.create("https://repo.spring.io/artifactory/release/"));
            mavenRepo.setName("spring");
        });
    }

    private void configureToolchain(Project project) {
        project.getPlugins().apply(ToolchainPlugin.class);
    }
    
    /**
     * 配置项目依赖
     * @param project project
     */
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
            importsHandler.mavenBom(BomCoordinates.SPRING_BOM_COORDINATES);
            importsHandler.mavenBom(BomCoordinates.ALI_YUN_BOM_COORDINATES);
            importsHandler.mavenBom(BomCoordinates.ALI_CLOUD_BOM_COORDINATES);
        }));
        

        configurations
                .matching((configuration) ->
                        configuration.getName().endsWith("Classpath") || JavaPlugin.ANNOTATION_PROCESSOR_CONFIGURATION_NAME.equals(configuration.getName()))
                .all((configuration) -> {
                    configuration.extendsFrom(dependencyManagement);
                });

        project.getPlugins().withType(OptionalDependenciesPlugin.class, (optionalDependencies) -> configurations
                .getByName(OptionalDependenciesPlugin.OPTIONAL_CONFIGURATION_NAME).extendsFrom(dependencyManagement));
    }
    
    /**
     * 配置生成的 Jar 清单文件
     * @param project project
     */
    private void configureJarManifestConventions(Project project) {
        
        ExtractResources extractLegalResources = project.getTasks().create("extractLegalResources", ExtractResources.class);
        extractLegalResources.getDestinationDirectory().set(project.getLayout().getBuildDirectory().dir("legal"));
        extractLegalResources.setResourcesNames(Arrays.asList("LICENSE.txt", "NOTICE.txt", "README.txt"));
        extractLegalResources.property("version", project.getVersion().toString());
        extractLegalResources.property("copyright", DateTimeFormatter.ofPattern("yyyy").format(LocalDateTime.now()));
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        
        Set<String> sourceJarTaskNames = sourceSets.stream().map(SourceSet::getSourcesJarTaskName).collect(Collectors.toSet());
        Set<String> javadocJarTaskNames = sourceSets.stream().map(SourceSet::getJavadocJarTaskName).collect(Collectors.toSet());
        
        project.getTasks().withType(Jar.class, jar -> project.afterEvaluate((evaluated) -> {
            jar.metaInf((metaInf) -> metaInf.from(extractLegalResources));
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
    
    /**
     * 配置测试的约定
     * 当 系统中存在 System.getenv("CI") 属性时，最大重试次数为 3 次
     * @param project project
     */
    private void configureTestConventions(Project project) {
        project.getTasks().withType(Test.class, (test) -> {
            test.useJUnitPlatform();
            test.setMaxHeapSize("1024M");
        });
        
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.platform:junit-platform-launcher");
            project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter");
            project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.assertj:assertj-core");
            project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.springframework.boot:spring-boot-starter-test");
        });
        
        project.getPlugins().apply(TestRetryPlugin.class);
        
        project.getTasks().withType(Test.class, (test -> {
            project.getPlugins().withType(TestRetryPlugin.class, testRetryPlugin -> {
                TestRetryTaskExtension testRetry = test.getExtensions().getByType(TestRetryTaskExtension.class);
                testRetry.getFailOnPassedAfterRetry().set(true);
                testRetry.getMaxRetries().set(isCi() ? 3 : 0);
            });
        }));
    }
    
    private boolean isCi() {
        return Boolean.parseBoolean(System.getenv("CI"));
    }
    
    /**
     * 配置 JavaDoc 约定
     * @param project project
     */
    private void configureJavadocConventions(Project project) {
        project.getTasks().withType(Javadoc.class, javadoc -> {
            javadoc.setDescription("Generates project-level javadoc for use in -javadoc jar");
            javadoc.options((option) -> {
                option.encoding("UTF-8");
                option.source("1.8");
                option.setMemberLevel(JavadocMemberLevel.PROTECTED);
                option.header(project.getName());
            });
        });
    }
    
    /**
     * 配置 Java 编译
     * @param project project
     */
    private void configureJavaCompileConventions(Project project) {
        project.getTasks().withType(JavaCompile.class, compile -> {
            compile.getOptions().setEncoding("UTF-8");
            compile.setSourceCompatibility("1.8");
            compile.setTargetCompatibility("1.8");
            List<String> args = compile.getOptions().getCompilerArgs();
            if (!args.contains("-parameters")) {
                args.add("-parameters");
            }
            if (!project.hasProperty("toolchainVersion") && JavaVersion.current() == JavaVersion.VERSION_1_8) {
                args.addAll(Arrays.asList("-Werror", "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:rawtypes",
                        "-Xlint:varargs"));
            }
        });
    }
    
    /**
     * 配置 {@link SpringJavaFormatPlugin} 和 {@link CheckstylePlugin} 插件
     * @param project project
     */
    private void configureSpringJavaFormat(Project project) {
        project.getPlugins().apply(SpringJavaFormatPlugin.class);
        project.getTasks().withType(FormatTask.class, (formatTask -> formatTask.setEncoding("UTF-8")));

        project.getPlugins().apply(CheckstylePlugin.class);
        CheckstyleExtension checkstyle = project.getExtensions().getByType(CheckstyleExtension.class);
        checkstyle.setToolVersion("8.44");
        checkstyle.getConfigDirectory().set(project.getRootProject().file("src/checkstyle"));

        DependencySet checkstyleDependencies = project.getConfigurations().getByName("checkstyle").getDependencies();
        checkstyleDependencies.add(project.getDependencies().create(BomCoordinates.JCOHY_CHECKSTYLE));
    }
}
