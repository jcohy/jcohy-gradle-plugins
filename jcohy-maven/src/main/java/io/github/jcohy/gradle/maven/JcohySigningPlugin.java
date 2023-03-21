package io.github.jcohy.gradle.maven;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/2/17 17:18
 * @since 1.0.0
 */
public class JcohySigningPlugin {

    public void apply(Project project) {

        project.getPluginManager().apply(SigningPlugin.class);

        project.getPlugins().withType(SigningPlugin.class, sign -> {
            configureCommonAttribute(project);
            sign(project);
        });
    }

    private void configureCommonAttribute(Project project) {
        ExtraPropertiesExtension ext = project.getExtensions().getByType(ExtraPropertiesExtension.class);
        ext.set("signing.keyId", System.getenv("SIGNING_KEYID"));
        ext.set("signing.password", System.getenv("SIGNING_PASSWORD"));
        ext.set("signing.secretKeyRingFile", System.getenv("SIGNING_SECRETKEYRINGFILE"));
        ext.set("gradle.publish.key", System.getenv("GRADLE_PUBLISH_KEY"));
        ext.set("gradle.publish.secret", System.getenv("GRADLE_PUBLISH_SECRET"));
    }


    private void sign(Project project) {

        SigningExtension signing = project.getExtensions().findByType(SigningExtension.class);

        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        publishing.getPublications().withType(MavenPublication.class)
                .all(((mavenPublication) -> signing.sign(mavenPublication)
                ));
    }
}
