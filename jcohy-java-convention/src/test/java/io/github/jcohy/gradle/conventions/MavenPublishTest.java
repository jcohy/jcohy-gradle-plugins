package io.github.jcohy.gradle.conventions;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/8:11:13
 * @since 0.0.5.1
 */
public class MavenPublishTest {

    @Test
    void gradleBuild() throws Exception {
        File projectDir = new File("src/test/maven");
        Map<String, String> environment = new LinkedHashMap<>();


        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withGradleVersion("7.6")
                .withProjectDir(projectDir)
                .withDebug(true)
                .forwardOutput()
                .withArguments("clean", "publish")
                .build();
		result.task(":publish");
//        assertThat(result.task(":publish").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
//        File generatedHtml = new File(projectDir, "build/docs/asciidoc");
//        File htmlFile = new File(generatedHtml, "index.html");
    }
}
