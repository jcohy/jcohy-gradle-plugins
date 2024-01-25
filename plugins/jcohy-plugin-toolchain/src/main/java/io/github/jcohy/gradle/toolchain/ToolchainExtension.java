package io.github.jcohy.gradle.toolchain;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/25 11:55
 * @since 2024.0.1
 */
public class ToolchainExtension {

	private final Property<JavaLanguageVersion> maximumCompatibleJavaVersion;

	private final JavaLanguageVersion javaVersion;

	public ToolchainExtension(Project project) {
		this.maximumCompatibleJavaVersion = project.getObjects().property(JavaLanguageVersion.class);
		String toolchainVersion = (String) project.findProperty("toolchainVersion");
		this.javaVersion = (toolchainVersion != null) ? JavaLanguageVersion.of(toolchainVersion) : null;
	}

	public Property<JavaLanguageVersion> getMaximumCompatibleJavaVersion() {
		return this.maximumCompatibleJavaVersion;
	}

	JavaLanguageVersion getJavaVersion() {
		return this.javaVersion;
	}
}