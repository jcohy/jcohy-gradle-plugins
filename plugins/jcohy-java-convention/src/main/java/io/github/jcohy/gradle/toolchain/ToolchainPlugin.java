package io.github.jcohy.gradle.toolchain;

import java.util.Arrays;
import java.util.List;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.GradleBuild;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainSpec;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/11:16:55
 * @since 0.0.5.1
 */
public class ToolchainPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        configureToolchain(project);
    }

    private void configureToolchain(Project project) {
        ToolchainExtension toolchain = project.getExtensions().create("toolchain", ToolchainExtension.class, project);
        JavaLanguageVersion toolchainVersion = toolchain.getJavaVersion();
        if (toolchainVersion != null) {
            project.afterEvaluate((evaluated) -> configure(evaluated, toolchain));
        }
    }

    private void configure(Project project, ToolchainExtension toolchain) {
        if (!isJavaVersionSupported(toolchain, toolchain.getJavaVersion())) {
            disableToolchainTasks(project);
        }
        else {
            JavaToolchainSpec toolchainSpec = project.getExtensions().getByType(JavaPluginExtension.class)
                    .getToolchain();
            toolchainSpec.getLanguageVersion().set(toolchain.getJavaVersion());
            configureJavaCompileToolchain(project, toolchain);
            configureTestToolchain(project, toolchain);
        }
    }

    private boolean isJavaVersionSupported(ToolchainExtension toolchain, JavaLanguageVersion toolchainVersion) {
        return toolchain.getMaximumCompatibleJavaVersion().map((version) -> version.canCompileOrRun(toolchainVersion))
                .getOrElse(true);
    }

    private void disableToolchainTasks(Project project) {
        project.getTasks().withType(JavaCompile.class, (task) -> task.setEnabled(false));
        project.getTasks().withType(Javadoc.class, (task) -> task.setEnabled(false));
        project.getTasks().withType(Test.class, (task) -> task.setEnabled(false));
        project.getTasks().withType(GradleBuild.class, (task) -> task.setEnabled(false));
    }

    private void configureJavaCompileToolchain(Project project, ToolchainExtension toolchain) {
        project.getTasks().withType(JavaCompile.class, (compile) -> {
            compile.getOptions().setFork(true);
            // See https://github.com/gradle/gradle/issues/15538
            List<String> forkArgs = Arrays.asList("--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED");
            compile.getOptions().getForkOptions().getJvmArgs().addAll(forkArgs);
        });
    }

    private void configureTestToolchain(Project project, ToolchainExtension toolchain) {
        project.getTasks().withType(Test.class, (test) -> {
            // See https://github.com/spring-projects/spring-ldap/issues/570
            List<String> arguments = Arrays.asList("--add-exports=java.naming/com.sun.jndi.ldap=ALL-UNNAMED",
                    "--illegal-access=warn");
            test.jvmArgs(arguments);
        });
    }
}
