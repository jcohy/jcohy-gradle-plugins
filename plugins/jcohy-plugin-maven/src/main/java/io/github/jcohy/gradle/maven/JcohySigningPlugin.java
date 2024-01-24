package io.github.jcohy.gradle.maven;

import java.util.concurrent.Callable;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.publish.Publication;
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
public class JcohySigningPlugin {

    public void apply(Project project) {

        project.getPluginManager().apply(SigningPlugin.class);

		project.getPlugins().withType(SigningPlugin.class, (signingPlugin) -> {
			boolean hasSigningKey = project.hasProperty("signing.keyId") || project.hasProperty("signingKey");
			if (hasSigningKey) {
				sign(project);
			}
		});
    }

	private void sign(Project project) {
		SigningExtension signing = project.getExtensions().getByType(SigningExtension.class);
		signing.setRequired((Callable<Boolean>) () -> project.getGradle().getTaskGraph().hasTask("publishArtifacts"));

		String signingKeyId = (String) project.findProperty("signingKeyId");
		String signingKey = (String) project.findProperty("signingKey");
		String signingPassword = (String) project.findProperty("signingPassword");

		if (signingKeyId != null) {
			signing.useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword);
		} else {
			signing.useInMemoryPgpKeys(signingKey, signingPassword);
		}
		project.getPlugins().withType(SpringPublishAllJavaComponentsPlugin.class, (publishingPlugin) -> {
			PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
			Publication maven = publishing.getPublications().getByName("mavenJava");
			signing.sign(maven);
		});
	}
}
