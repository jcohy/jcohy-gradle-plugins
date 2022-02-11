package com.jcohy.convention.optional;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 增加了对 Maven 风格的可选依赖的支持.
 * 创建一个 {@code optional} 配置
 * {@code optional} 配置是项目 compile 和 runtime 时 classpath 的一部分，但是不影响依赖项目的 classpath
 * @author jiac
 * @version 0.0.5.1 2021/6/11:16:43
 * @since 0.0.5.1
 */
public class OptionalDependenciesPlugin implements Plugin<Project> {
    
    public static final String OPTIONAL_CONFIGURATION_NAME = "optional";
    
    @Override
    public void apply(Project project) {
        Configuration optional = project.getConfigurations().create(OPTIONAL_CONFIGURATION_NAME);
        optional.attributes((attribute) -> {
            attribute.attribute(Usage.USAGE_ATTRIBUTE, project.getObjects().named(Usage.class, Usage.JAVA_RUNTIME));
        });
        
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            SourceSetContainer sourceSets = project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
            sourceSets.all(sourceSet -> {
                sourceSet.setCompileClasspath(sourceSet.getCompileClasspath().plus(optional));
                sourceSet.setRuntimeClasspath(sourceSet.getRuntimeClasspath().plus(optional));
            });
            
            project.getTasks().withType(Javadoc.class)
                    .all(javadoc -> javadoc.setClasspath(javadoc.getClasspath().plus(optional)));
        });
        
        project.getPlugins().withType(EclipsePlugin.class,
                (eclipsePlugin) -> project.getExtensions().getByType(EclipseModel.class)
                        .classpath((classpath) -> classpath.getPlusConfigurations().add(optional)));
    }
}
