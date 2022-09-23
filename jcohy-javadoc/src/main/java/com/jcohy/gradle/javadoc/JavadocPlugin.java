package com.jcohy.gradle.javadoc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.Category;
import org.gradle.api.attributes.DocsType;
import org.gradle.api.attributes.Usage;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

/**
 * 描述: 聚合 javadoc 中要包含的配置.
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:10:48
 * @since 2022.0.1
 */
public class JavadocPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().withType(JavaPlugin.class)
                .all((javaPlugin) -> {
                    withSourcesElements(project);
                });
    }

    private void withSourcesElements(Project project) {
        project.getConfigurations().create("sourcesElements",(config) -> {
            /**
             * 一个可被解析 configuration 往往继承了一个或多个不可被解析 configuration。它们的关系类似于抽象类和子类：不可被解析 configuration 不能实例化，可解析 configuration 是它的子类，可实例化（将 dependencies 解析为文件）。
             * 所以可被解析 configuration 用于管理 dependency，相应的，可被消费 configuration 用于管理 artifact。
             */
            config.setCanBeResolved(false);
            config.setCanBeConsumed(true);
            config.attributes((attributes -> {
                ObjectFactory objects = project.getObjects();
                attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.class,Usage.JAVA_RUNTIME));
                attributes.attribute(Category.CATEGORY_ATTRIBUTE,objects.named(Category.class,Category.DOCUMENTATION));
                attributes.attribute(DocsType.DOCS_TYPE_ATTRIBUTE,objects.named(DocsType.class,DocsType.SOURCES));
                attributes.attribute(Attribute.of("org.gradle.docselements", String.class), "sources");
            }));

            // 输出
            config.outgoing((publications) -> {
                JavaPluginConvention javaPlugin = project.getConvention().getPlugin(JavaPluginConvention.class);
                SourceSet mainSrc = javaPlugin.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                mainSrc.getAllJava().forEach(publications::artifact);
            });
        });
    }
}
