package com.jcohy.convention.asciidoctor;

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
 * @version 1.0.0 2021/7/8:11:13
 * @since 1.0.0
 */
public class AsciidoctorTest {

    @Test
    void gradleBuild() throws Exception {
        File projectDir = new File("src/test/gradle");
        Map<String, String> environment = new LinkedHashMap<>();

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withGradleVersion("6.8.3")
                .withProjectDir(projectDir)
                .withDebug(true)
                .forwardOutput()
                .withArguments("clean", "asciidoctor","asciidoctorPdf")
//                .withArguments("clean","asciidoctorPdf")
                .build();
        assertThat(result.task(":asciidoctor").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
        File generatedHtml = new File(projectDir, "build/docs/asciidoc");
        File htmlFile = new File(generatedHtml, "index.html");
        assertThat(new File(generatedHtml, "css/site.css")).exists();
        assertThat(new File(generatedHtml, "js/site.js")).exists();
        assertThat(htmlFile).exists();
        assertThat(new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8))
                .contains("<title>Flight 文档</title>")
                .contains("<p>doc-url: <a href=\"http://docs.jcohy.com\">doc-url</a></p>")
                .contains("<p>resource-url: <a href=\"http://resources.jcohy.com\">resource-url</a></p>")
                .contains("<p>software-url: <a href=\"http://software.jcohy.com\">software-url</a></p>")
                .contains("<p>study-url: <a href=\"http://study.jcohy.com\">study-url</a></p>")
                .contains("<p>project-url: <a href=\"http://project.jcohy.com\">project-url</a></p>");
        File generatedPdf = new File(projectDir, "build/docs/asciidocPdf");
        File pdfFile = new File(generatedPdf, "index.pdf");
        assertThat(pdfFile).exists();
    }
}
