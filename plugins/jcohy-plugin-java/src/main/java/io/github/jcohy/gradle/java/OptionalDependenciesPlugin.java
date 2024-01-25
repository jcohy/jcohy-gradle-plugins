package io.github.jcohy.gradle.java;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Usage;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/25 11:53
 * @since 2024.0.1
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
			SourceSetContainer sourceSets = project.getExtensions().getByType(JavaPluginExtension.class).getSourceSets();
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