package com.jcohy.oss;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:12:03
 * @since 0.0.5.1
 */
public class OssUploadTest {

    @Test
	@Disabled
    void uploadOssFiles() {
        File projectDir = new File("src/test/gradle");
        Map<String, String> environment = new LinkedHashMap<>();

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withGradleVersion("6.8.3")
                .withProjectDir(projectDir)
                .withDebug(true)
                .forwardOutput()
                .withArguments("clean", "uploadOssFiles")
                .build();
        assertThat(result.task(":uploadOssFiles").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    }
}
