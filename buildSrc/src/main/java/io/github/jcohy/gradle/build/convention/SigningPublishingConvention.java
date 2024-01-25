package io.github.jcohy.gradle.build.convention;

import org.gradle.api.Plugin;
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
 * Copyright © 2024 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2024.0.1 2023/2/17 17:18
 * @since 1.0.0
 */
public class SigningPublishingConvention implements Plugin<Project> {

	@Override
	public void apply(Project project) {

		project.getPlugins().apply(SigningPlugin.class);

		project.getPlugins().withType(SigningPlugin.class,sign -> {
			configureCommonAttribute(project);
			configureSigning(project);
		});
	}

	private void configureCommonAttribute(Project project) {
		ExtraPropertiesExtension ext = project.getExtensions().getByType(ExtraPropertiesExtension.class);
		ext.set("signing.keyId",System.getenv("SIGNING_KEYID"));
		ext.set("signing.password",System.getenv("SIGNING_PASSWORD"));
		ext.set("signing.secretKeyRingFile",System.getenv("SIGNING_SECRETKEYRINGFILE"));
		ext.set("gradle.publish.key",System.getenv("GRADLE_PUBLISH_KEY"));
		ext.set("gradle.publish.secret",System.getenv("GRADLE_PUBLISH_SECRET"));
	}

	private void configureSigning(Project project) {
		PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
		publishing.getPublications().withType(MavenPublication.class)
				.all(((mavenPublication) -> project.getExtensions().getByType(SigningExtension.class).sign(mavenPublication)
				));
	}
}
