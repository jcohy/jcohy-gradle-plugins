package io.github.jcohy.gradle.maven.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:11:26
 * @since 2022.04.0
 */
public class ArchivesMavenPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply(MavenPublishPlugin.class);
        pluginManager.apply(ArchivesPublishingPlugins.class);
    }
}
