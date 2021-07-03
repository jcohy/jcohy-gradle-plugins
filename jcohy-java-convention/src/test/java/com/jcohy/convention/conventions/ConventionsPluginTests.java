package com.jcohy.convention.conventions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/6/19:16:37
 * @since 1.0.0
 */
public class ConventionsPluginTests {
    
    private File projectDir;
    
    private File buildFile;
    
    @BeforeEach
    void setup(@TempDir File projectDir) throws IOException {
        this.projectDir = projectDir;
        this.buildFile = new File(this.projectDir, "build.gradle");
        File settingsFile = new File(this.projectDir, "settings.gradle");
        // 创建根目录 settings.gradle 并填充内容
        try (PrintWriter out = new PrintWriter(new FileWriter(settingsFile))) {
            out.println("include ':jcohy-project'");
        }
        
        File jcohyParentProject = new File(this.projectDir, "jcohy-project/build.gradle");
        jcohyParentProject.getParentFile().mkdirs();
        try (PrintWriter out = new PrintWriter(new FileWriter(jcohyParentProject))) {
            out.println("plugins {");
            out.println("    id 'java-platform'");
            out.println("}");
        }
    }
    
    @Test
    void jarIncludesLegalFiles() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(this.buildFile))) {
            out.println("plugins {");
            out.println("    id 'java'");
            out.println("    id 'com.jcohy.conventions'");
            out.println("}");
            out.println("version = '1.2.3'");
            out.println("sourceCompatibility = '1.8'");
            out.println("description 'Test project for manifest customization'");
            out.println("jar.archiveFileName = 'test.jar'");
        }
        runGradle("jar");
        File file = new File(this.projectDir, "/build/libs/test.jar");
        assertThat(file).exists();
        try (JarFile jar = new JarFile(file)) {
            assertThatLicenseIsPresent(jar);
            assertThatNoticeIsPresent(jar);
            Attributes mainAttributes = jar.getManifest().getMainAttributes();
            assertThat(mainAttributes.getValue("Implementation-Title"))
                    .isEqualTo("Test project for manifest customization");
            assertThat(mainAttributes.getValue("Automatic-Module-Name"))
                    .isEqualTo(this.projectDir.getName().replace("-", "."));
            assertThat(mainAttributes.getValue("Implementation-Version")).isEqualTo("1.2.3");
            assertThat(mainAttributes.getValue("Built-By")).isEqualTo("Jcohy");
            assertThat(mainAttributes.getValue("Build-Jdk-Spec")).isEqualTo("1.8");
        }
    }
    
    @Test
    void sourceJarIsBuilt() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(this.buildFile))) {
            out.println("plugins {");
            out.println("    id 'java'");
            out.println("    id 'maven-publish'");
            out.println("    id 'com.jcohy.conventions'");
            out.println("}");
            out.println("version = '1.2.3'");
            out.println("sourceCompatibility = '1.8'");
            out.println("description 'Test'");
        }
        runGradle("build");
        File file = new File(this.projectDir, "/build/libs/" + this.projectDir.getName() + "-1.2.3-sources.jar");
        assertThat(file).exists();
        try (JarFile jar = new JarFile(file)) {
            assertThatLicenseIsPresent(jar);
            assertThatNoticeIsPresent(jar);
            Attributes mainAttributes = jar.getManifest().getMainAttributes();
            assertThat(mainAttributes.getValue("Implementation-Title"))
                    .isEqualTo("Source for " + this.projectDir.getName());
            assertThat(mainAttributes.getValue("Automatic-Module-Name"))
                    .isEqualTo(this.projectDir.getName().replace("-", "."));
            assertThat(mainAttributes.getValue("Implementation-Version")).isEqualTo("1.2.3");
            assertThat(mainAttributes.getValue("Built-By")).isEqualTo("Jcohy");
            assertThat(mainAttributes.getValue("Build-Jdk-Spec")).isEqualTo("1.8");
        }
    }
    
    @Test
    void javadocJarIsBuilt() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(this.buildFile))) {
            out.println("plugins {");
            out.println("    id 'java'");
            out.println("    id 'maven-publish'");
            out.println("    id 'com.jcohy.conventions'");
            out.println("}");
            out.println("version = '1.2.3'");
            out.println("sourceCompatibility = '1.8'");
            out.println("description 'Test'");
        }
        runGradle("build");
        File file = new File(this.projectDir, "/build/libs/" + this.projectDir.getName() + "-1.2.3-javadoc.jar");
        assertThat(file).exists();
        try (JarFile jar = new JarFile(file)) {
            assertThatLicenseIsPresent(jar);
            assertThatNoticeIsPresent(jar);
            Attributes mainAttributes = jar.getManifest().getMainAttributes();
            assertThat(mainAttributes.getValue("Implementation-Title"))
                    .isEqualTo("Javadoc for " + this.projectDir.getName());
            assertThat(mainAttributes.getValue("Automatic-Module-Name"))
                    .isEqualTo(this.projectDir.getName().replace("-", "."));
            assertThat(mainAttributes.getValue("Implementation-Version")).isEqualTo("1.2.3");
            assertThat(mainAttributes.getValue("Built-By")).isEqualTo("Jcohy");
            assertThat(mainAttributes.getValue("Build-Jdk-Spec")).isEqualTo("1.8");
        }
    }
    
    
    @Test
    void testRetryIsConfiguredWithThreeRetriesOnCI() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(this.buildFile))) {
            out.println("plugins {");
            out.println("    id 'java'");
            out.println("    id 'com.jcohy.conventions'");
            out.println("}");
            out.println("description 'Test'");
            out.println("task retryConfig {");
            out.println("    doLast {");
            out.println("        println \"Retry plugin applied: ${plugins.hasPlugin('org.gradle.test-retry')}\"");
            out.println("    test.retry {");
            out.println("            println \"maxRetries: ${maxRetries.get()}\"");
            out.println("            println \"failOnPassedAfterRetry: ${failOnPassedAfterRetry.get()}\"");
            out.println("        }");
            out.println("    }");
            out.println("}");
        }
        assertThat(runGradle(Collections.singletonMap("CI", "true"), "retryConfig", "--stacktrace").getOutput())
                .contains("Retry plugin applied: true").contains("maxRetries: 3")
                .contains("failOnPassedAfterRetry: true");
    }
    
    @Test
    void testRetryIsConfiguredWithZeroRetriesLocally() throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(this.buildFile))) {
            out.println("plugins {");
            out.println("    id 'java'");
            out.println("    id 'com.jcohy.conventions'");
            out.println("}");
            out.println("description 'Test'");
            out.println("task retryConfig {");
            out.println("    doLast {");
            out.println("        println \"Retry plugin applied: ${plugins.hasPlugin('org.gradle.test-retry')}\"");
            out.println("    test.retry {");
            out.println("            println \"maxRetries: ${maxRetries.get()}\"");
            out.println("            println \"failOnPassedAfterRetry: ${failOnPassedAfterRetry.get()}\"");
            out.println("        }");
            out.println("    }");
            out.println("}");
        }
        assertThat(runGradle(Collections.singletonMap("CI", "local"), "retryConfig", "--stacktrace").getOutput())
                .contains("Retry plugin applied: true").contains("maxRetries: 0")
                .contains("failOnPassedAfterRetry: true");
    }
    
    private void assertThatLicenseIsPresent(JarFile jar) {
        JarEntry license = jar.getJarEntry("META-INF/LICENSE.txt");
        assertThat(license).isNotNull();
    }
    
    private void assertThatNoticeIsPresent(JarFile jar) throws IOException {
        JarEntry notice = jar.getJarEntry("META-INF/NOTICE.txt");
        assertThat(notice).isNotNull();
        String noticeContent = FileCopyUtils.copyToString(new InputStreamReader(jar.getInputStream(notice)));
        // Test that variables were replaced
        assertThat(noticeContent).doesNotContain("${");
    }
    
    private BuildResult runGradle(String... args) {
        return runGradle(Collections.emptyMap(), args);
    }
    
    private BuildResult runGradle(Map<String, String> environment, String... args) {
        return GradleRunner.create().withProjectDir(this.projectDir).withEnvironment(environment).withArguments(args)
                .withPluginClasspath().build();
    }
}
