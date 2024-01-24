package io.github.jcohy.gradle.maven.publishing;

import com.gradle.publish.PublishPlugin;
import io.github.jcohy.gradle.maven.JcohySigningPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.PluginManager;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:17:20
 * @since 2022.04.0
 */
public class GradlePluginPublishPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply(PublishPlugin.class);
        pluginManager.apply(JcohySigningPlugin.class);
        configureCommonAttribute(project);
    }

    private void configureCommonAttribute(Project project) {
        ExtraPropertiesExtension ext = project.getExtensions().getByType(ExtraPropertiesExtension.class);
        ext.set("gradle.publish.key", System.getenv("GRADLE_PUBLISH_KEY"));
        ext.set("gradle.publish.secret", System.getenv("GRADLE_PUBLISH_SECRET"));
    }
}
