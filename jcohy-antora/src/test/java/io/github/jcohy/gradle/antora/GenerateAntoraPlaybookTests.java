package io.github.jcohy.gradle.antora;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.util.function.ThrowingConsumer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class GenerateAntoraPlaybookTests {

    @TempDir
    File temp;

    @Test
    void writePlaybookGeneratesExpectedContent() throws Exception {
        writePlaybookYml((task) -> {
            task.getXrefStubs().addAll("appendix:.*", "api:.*", "reference:.*");
            task.getAlwaysInclude().set(Map.of("name", "test", "classifier", "local-aggregate-content"));
        });
        String actual = Files.readString(this.temp.toPath()
            .resolve("rootproject/project/build/generated/docs/antora-playbook/antora-playbook.yml"));
        String expected = Files
            .readString(Path.of("src/test/resources/org/springframework/boot/build/antora/expected-playbook.yml"));
        System.out.println(actual);
        assertThat(actual.replace('\\', '/')).isEqualToNormalizingNewlines(expected.replace('\\', '/'));
    }

    private void writePlaybookYml(ThrowingConsumer<GenerateAntoraPlaybook> customizer) throws Exception {
        File rootProjectDir = new File(this.temp, "rootproject").getCanonicalFile();
        rootProjectDir.mkdirs();
        Project rootProject = ProjectBuilder.builder().withProjectDir(rootProjectDir).build();
        File projectDir = new File(rootProjectDir, "project");
        projectDir.mkdirs();
        Project project = ProjectBuilder.builder().withProjectDir(projectDir).withParent(rootProject).build();
        GenerateAntoraPlaybook task = project.getTasks().create("generateAntoraPlaybook", GenerateAntoraPlaybook.class);
        customizer.accept(task);
        task.writePlaybookYml();
    }
}
