package io.github.jcohy.gradle.maven.convention;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
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
public class JavaConventionPlugin implements Plugin<Project> {

	@Override
    public void apply(Project project) {
        project.getPlugins().withType(JavaBasePlugin.class, (java) -> {
            configureJarManifest(project);
            configureJavadoc(project);
        });
    }

    /**
     * manifest 文件配置
     *
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

    /**
     * JavaDoc 配置
     *
     * @param project project
     */
    private void configureJavadoc(Project project) {
        project.getTasks().withType(Javadoc.class, javadoc -> {
            javadoc.setDescription("Generates project-level javadoc for use in -javadoc jar");
            javadoc.options((option) -> {
                option.encoding("UTF-8");
                option.source("17");
                option.header(project.getName());
            });
            // 跨模块的 @see 和 @link 引用消除警告
            javadoc.getLogging().captureStandardError(LogLevel.INFO);
            // 消除 "## warnings" 消息
            javadoc.getLogging().captureStandardOutput(LogLevel.INFO);
        });
    }

}
