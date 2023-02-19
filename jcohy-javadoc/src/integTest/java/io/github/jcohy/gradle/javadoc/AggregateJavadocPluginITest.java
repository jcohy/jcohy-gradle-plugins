package io.github.jcohy.gradle.javadoc;

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
 * @version 2022.0.1 2022/10/11:17:20
 * @since 2022.0.1
 */
public class AggregateJavadocPluginITest {

    @TempDir
    File workDir;

    @Test
    void aggregateJavadocWhenSimpleThenSuccess() throws Exception {
        runAggregateJavadocTask("simple");
        assertThat(aggregateJavadocPath("module1/M1")).exists();
        assertThat(aggregateJavadocPath("module2/M2")).exists();
        assertThat(aggregateJavadocPath("test")).doesNotExist();
    }

    @Test
    void aggregateJavadocWhenAspectsThenSuccess() throws Exception {
        runAggregateJavadocTask("aspects");
        assertThat(aggregateJavadocPath("module1/M1")).exists();
        assertThat(aggregateJavadocPath("module2/M2")).exists();
        assertThat(aggregateJavadocPath("test")).doesNotExist();
    }

    @Test
    void aggregateJavadocWhenCustomProjectsThenSuccess() throws Exception {
        runAggregateJavadocTask("custom-projects");
        assertThat(aggregateJavadocPath("module1/M1")).exists();
        assertThat(aggregateJavadocPath("module2")).doesNotExist();
        assertThat(aggregateJavadocPath("module3/M3")).exists();
        assertThat(aggregateJavadocPath("test")).doesNotExist();
    }

    @Test
    void aggregateJavadocWhenExcludeThenSuccess() throws Exception {
        runAggregateJavadocTask("exclude");
        assertThat(aggregateJavadocPath("module1/M1")).exists();
        assertThat(aggregateJavadocPath("module2")).doesNotExist();
        assertThat(aggregateJavadocPath("test")).doesNotExist();
    }

    private File aggregateJavadocPath(String path) {
        return new File(this.workDir,"aggregator/build/docs/javadoc/" + path + ".html");
    }

    private void runAggregateJavadocTask(String project) throws IOException, URISyntaxException {
        CopyUtils.fromResourceNameToDir("javadoc/aggregate/" + project,this.workDir);
        String task = ":aggregator:" + AggregateJavadocPlugin.AGGREGATE_JAVADOC_TASK_NAME;

        BuildResult buildResult = GradleRunner.create()
                .withProjectDir(this.workDir)
                .withPluginClasspath()
                .withArguments(task)
                .withDebug(true)
                .forwardOutput()
                .build();

        assertThat(buildResult.task(task).getOutcome()).isEqualTo(TaskOutcome.SUCCESS);
    }
}
