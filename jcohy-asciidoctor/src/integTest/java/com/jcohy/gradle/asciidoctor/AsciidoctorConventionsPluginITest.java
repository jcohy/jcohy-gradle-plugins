package com.jcohy.gradle.asciidoctor;

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
 * @version 2022.0.1 2022/10/13:16:35
 * @since 2022.0.1
 */
public class AsciidoctorConventionsPluginITest {

    @TempDir
    File workDir;

    @Test
    void asciidoctorTaskThenSuccess() throws IOException, URISyntaxException {
        CopyUtils.fromResourceNameToDir("asciidoctor/",this.workDir);
        String task = ":asciidoctor";
        BuildResult buildResult = GradleRunner.create()
                .withProjectDir(this.workDir)
                .withPluginClasspath()
                .withArguments(task)
                .withDebug(true)
                .withGradleVersion("6.4")
                .forwardOutput()
                .build();
        assertThat(buildResult.task(task).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    }
}
