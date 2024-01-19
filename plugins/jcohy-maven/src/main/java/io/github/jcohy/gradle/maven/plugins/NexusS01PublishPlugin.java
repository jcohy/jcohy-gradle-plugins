package io.github.jcohy.gradle.maven.plugins;

import io.github.gradlenexus.publishplugin.NexusPublishExtension;
import io.github.gradlenexus.publishplugin.NexusPublishPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:16:01
 * @since 2022.04.0
 */
public class NexusS01PublishPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(NexusPublishPlugin.class);
        NexusPublishExtension nexusPublish = project.getExtensions().findByType(NexusPublishExtension.class);
        nexusPublish.getRepositories().create("s01");

    }
}
