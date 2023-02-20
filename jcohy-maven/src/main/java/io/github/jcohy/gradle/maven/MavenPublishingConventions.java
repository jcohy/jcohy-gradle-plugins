package io.github.jcohy.gradle.maven;

import io.github.jcohy.gradle.maven.dsl.JcohyPublishingExtension;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/10/17:17:01
 * @since 2022.0.1
 */
public class MavenPublishingConventions {

    void apply(Project project) {
        JcohyPublishingExtension extension = project.getExtensions().create(JcohyPublishingExtension.JCOHY_MAVEN_PUBLISHING_EXTENSION_NAME, JcohyPublishingExtension.class);
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        project.getPlugins().withType(MavenPublishPlugin.class).all((mavenPublishPlugin) -> {
           if(project.hasProperty("deploymentRepository")) {

           }

        });
    }
}
