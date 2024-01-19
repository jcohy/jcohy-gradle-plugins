package io.github.jcohy.gradle.asciidoctor;

import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorJPdfPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/19 9:57
 * @since 2024.0.1
 */
public class JcohyAsciidoctorPlugin implements Plugin<Project> {

	private static final String ASCIIDOCTORJ_VERSION = "2.4.3";
	private static final String EXTENSIONS_CONFIGURATION_NAME = "asciidoctorExtensions";

	@Override
	public void apply(Project project) {
		// Apply asciidoctor plugin
		project.getPluginManager().apply(AsciidoctorJPlugin.class);
		project.getPluginManager().apply(AsciidoctorJPdfPlugin.class);
	}
}
