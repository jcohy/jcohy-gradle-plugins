package io.github.jcohy.gradle.maven;

import java.io.File;
import java.io.IOException;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/2/21 15:25
 * @since 1.0.0
 */
class ArchivesPublishingPluginsTest {


    @Test
    void buildWithSonatype() {
        File projectDir = new File("src/test/maven");
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withGradleVersion("7.6")
                .withProjectDir(projectDir)
                .withDebug(true)
                .forwardOutput()
                .withArguments("clean","build")
                .build();
        assertThat(result.task(":build").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    }
}