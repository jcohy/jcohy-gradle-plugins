package com.jcohy.gradle.javadoc;


import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.DocsType;
import org.gradle.api.attributes.Usage;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.javadoc.Javadoc;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:10:48
 * @since 2022.0.1
 */
public class AggregateJavadocPlugin implements Plugin<Project> {

    /**
     * 聚合文档名.
     */
    public static final String AGGREGATE_JAVADOC_TASK_NAME = "aggregateJavadoc";

    /**
     * The javadoc classpath configuration name.
     */
    public static final String AGGREGATE_JAVADOC_CLASSPATH_CONFIGURATION_NAME = "aggregateJavadocClasspath";
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);
        // aggregateJavadocClasspath 配置
        Configuration aggregatedConfiguration = aggregatedConfiguration(project);
        Configuration sourcesPath = sourcesPath(project, aggregatedConfiguration);
        aggregatedJavadoc(project, sourcesPath, aggregatedConfiguration);
    }

    private Configuration aggregatedConfiguration(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        // 创建 aggregateJavadocClasspath 配置项
        Configuration aggregatedConfiguration = configurations.maybeCreate(AGGREGATE_JAVADOC_CLASSPATH_CONFIGURATION_NAME);
        // 使 implementation 继承 aggregateJavadocClasspath 配置。
        configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME)
                .extendsFrom(aggregatedConfiguration);
        // 配置默认依赖，将使用了 JavadocPlugin 插件的子项目的依赖添加到此配置上
        aggregatedConfiguration.defaultDependencies((dependencies) -> {
            project.getGradle().getRootProject().subprojects((subjectProject) -> {
                subjectProject.getPlugins().withType(JavadocPlugin.class,(javadoc) -> {
                    Dependency dependency = project.getDependencies().create(subjectProject);
                    dependencies.add(dependency);
                });
            });
        });
        return aggregatedConfiguration;
    }

    private Configuration sourcesPath(Project project, Configuration aggregatedConfiguration) {
        ConfigurationContainer configurations = project.getConfigurations();
        return configurations.create("sourcesPath",(sourcesPath) -> {
            sourcesPath.setCanBeResolved(true);
            sourcesPath.setCanBeConsumed(false);
            sourcesPath.extendsFrom(aggregatedConfiguration);
            sourcesPath.attributes((attributes) -> {
                ObjectFactory objects = project.getObjects();
                attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class, Usage.JAVA_RUNTIME));
                attributes.attribute(Category.CATEGORY_ATTRIBUTE,
                        objects.named(Category.class, Category.DOCUMENTATION));
                attributes.attribute(DocsType.DOCS_TYPE_ATTRIBUTE,
                        objects.named(DocsType.class, DocsType.SOURCES));
                attributes.attribute(Attribute.of("org.gradle.docselements", String.class), "sources");
            });
            sourcesPath.outgoing((publications) -> {
                JavaPluginConvention javaPlugin = project.getConvention().getPlugin(JavaPluginConvention.class);
                SourceSet mainSrc = javaPlugin.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                mainSrc.getAllJava().forEach(publications::artifact);
            });
        });
    }


    private void aggregatedJavadoc(Project project, Configuration sourcesPath, Configuration aggregatedConfiguration) {
        project.getTasks().create(AGGREGATE_JAVADOC_TASK_NAME, Javadoc.class,(javadoc) -> {
            javadoc.setGroup("Documentation");
            javadoc.setDescription("Generates the aggregate Javadoc");
            javadoc.setSource(sourcesPath);
            javadoc.setClasspath(aggregatedConfiguration);
        });
    }
}
