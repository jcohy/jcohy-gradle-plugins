package io.github.jcohy.gradle.asciidoctor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

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
    void asciidoctorPdfTaskThenSuccess() throws IOException, URISyntaxException {
        runnerWithTask(":asciidoctorPdf");
        File generatedPdf = new File(this.workDir, "build/docs/asciidocPdf");
        assertThat(new File(generatedPdf,"index.pdf")).exists();
    }

    @Test
    void asciidoctorTaskThenSuccess() throws IOException, URISyntaxException {
        runnerWithTask(":asciidoctor");
        File generatedHtml = new File(this.workDir, "build/docs/asciidoc");
        File htmlFile = new File(generatedHtml, "index.html");
        assertThat(new File(generatedHtml, "index.html")).exists();
        assertThat(new File(generatedHtml, "css/site.css")).exists();
        assertThat(new File(generatedHtml, "js/site.js")).exists();
        assertThat(Files.readString(htmlFile.toPath()))
                .contains("<title>Asciidoctor Test Document</title>")
                .contains("<p>docs-url: <a href=\"https://docs.jcohy.com\">docs-url</a></p>")
                .contains("<p>resource-url: <a href=\"https://resources.jcohy.com\">resource-url</a></p>")
                .contains("<p>software-url: <a href=\"https://software.jcohy.com\">software-url</a></p>")
                .contains("<p>study-url: <a href=\"https://study.jcohy.com\">study-url</a></p>")
                .contains("<p>project-url: <a href=\"https://project.jcohy.com\">project-url</a></p>");
        checkCodeFileExist();
    }

    private void checkCodeFileExist() {
        File GroovyExample = new File(this.workDir, "build/docs/src/asciidoctor/main/groovy/com/jcohy/gradle/GroovyExample.groovy");
        File JavaExample = new File(this.workDir, "build/docs/src/asciidoctor/main/java/com/jcohy/gradle/JavaExample.java");
        File KotlinExample = new File(this.workDir, "build/docs/src/asciidoctor/main/kotlin/com/jcohy/gradle/KotlinExample.kts");
        File GroovyTestExample = new File(this.workDir, "build/docs/src/asciidoctor/test/groovy/com/jcohy/gradle/GroovyTestExample.groovy");
        File JavaTestExample = new File(this.workDir, "build/docs/src/asciidoctor/test/java/com/jcohy/gradle/JavaTestExample.java");
        File KotlinTestExample = new File(this.workDir, "build/docs/src/asciidoctor/test/kotlin/com/jcohy/gradle/KotlinTestExample.kts");
        assertThat(GroovyExample).exists();
        assertThat(JavaExample).exists();
        assertThat(KotlinExample).exists();
        assertThat(GroovyTestExample).exists();
        assertThat(JavaTestExample).exists();
        assertThat(KotlinTestExample).exists();
    }

    private void runnerWithTask(String task) throws IOException, URISyntaxException {
        CopyUtils.fromResourceNameToDir("asciidoctor/",this.workDir);
        BuildResult result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(this.workDir)
                .withDebug(true)
                .forwardOutput()
                .withArguments(task)
                .build();
        assertThat(result.task(task).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    }
}
