package com.jcohy.convention.conventions;

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
public class AsciidoctorTest {

    @Test
    void gradleBuild() throws Exception {
        File projectDir = new File("src/test/asciidoctor");
        Map<String, String> environment = new LinkedHashMap<>();

        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withGradleVersion("6.8.3")
                .withProjectDir(projectDir)
                .withDebug(true)
                .forwardOutput()
//                .withArguments("clean", "asciidoctor", "asciidoctorPdf")
                .withArguments("clean","asciidoctorPdf")
                .build();
        assertThat(result.task(":asciidoctor").getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
        File generatedHtml = new File(projectDir, "build/docs/asciidoc");
        File htmlFile = new File(generatedHtml, "index.html");
        assertThat(new File(generatedHtml, "css/site.css")).exists();
        assertThat(new File(generatedHtml, "js/site.js")).exists();
        assertThat(htmlFile).exists();
        assertThat(new String(Files.readAllBytes(htmlFile.toPath()), StandardCharsets.UTF_8))
                .contains("<title>Flight 文档</title>")
                .contains("<p>doc-url: <a href=\"https://docs.jcohy.com\">doc-url</a></p>")
                .contains("<p>resource-url: <a href=\"https://resources.jcohy.com\">resource-url</a></p>")
                .contains("<p>software-url: <a href=\"https://software.jcohy.com\">software-url</a></p>")
                .contains("<p>study-url: <a href=\"https://study.jcohy.com\">study-url</a></p>")
                .contains("<p>project-url: <a href=\"https://project.jcohy.com\">project-url</a></p>");

//        File generatedPdf = new File(projectDir, "build/docs/asciidocPdf");
//        File pdfFile = new File(generatedPdf, "index.pdf");
//        assertThat(pdfFile).exists();

        File GroovyExample = new File(projectDir, "build/docs/src/asciidoctor/main/groovy/com/jcohy/gradle/GroovyExample.groovy");
        File JavaExample = new File(projectDir, "build/docs/src/asciidoctor/main/java/com/jcohy/gradle/JavaExample.java");
        File KotlinExample = new File(projectDir, "build/docs/src/asciidoctor/main/kotlin/com/jcohy/gradle/KotlinExample.kts");
        File GroovyTestExample = new File(projectDir, "build/docs/src/asciidoctor/test/groovy/com/jcohy/gradle/GroovyTestExample.groovy");
        File JavaTestExample = new File(projectDir, "build/docs/src/asciidoctor/test/java/com/jcohy/gradle/JavaTestExample.java");
        File KotlinTestExample = new File(projectDir, "build/docs/src/asciidoctor/test/kotlin/com/jcohy/gradle/KotlinTestExample.kts");
        assertThat(GroovyExample).exists();
        assertThat(JavaExample).exists();
        assertThat(KotlinExample).exists();
        assertThat(GroovyTestExample).exists();
        assertThat(JavaTestExample).exists();
        assertThat(KotlinTestExample).exists();
    }
}
