package io.github.jcohy.gradle.convention.toolchain;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description: {@link ToolchainPlugin} 扩展 DSL
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/11:16:55
 * @since 0.0.5.1
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
