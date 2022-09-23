package com.jcohy.gradle.javadoc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:16:12
 * @since 2022.0.1
 */
class JavadocConventionsPluginITest {

    @TempDir
    File workingDir;

    @Test
    void syncJavaDocStylesheet() throws Exception {
        CopyUtils.fromResourceNameToDir("javadoc/conventions/simple", this.workingDir);
        String task = ":syncJavadocStylesheet";

        // @formatter:off
        BuildResult buildResult = GradleRunner.create()
                .withProjectDir(this.workingDir)
                .withPluginClasspath()
                .withArguments(task)
                .withDebug(true)
                .forwardOutput()
                .withGradleVersion("6.8.3")
                .build();
        // @formatter:on

        assertThat(buildResult.task(task).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
        File stylesheet = styleSheet();
        assertThat(stylesheet).exists();
        assertThat(stylesheet).isFile();
    }

    private File styleSheet() {
        return new File(this.workingDir, JavadocConventionsPlugin.STYLESHEET_FILE_NAME);
    }
}
