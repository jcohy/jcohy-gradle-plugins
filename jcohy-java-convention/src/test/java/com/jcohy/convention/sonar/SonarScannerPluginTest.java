//package com.jcohy.convention.sonar;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.io.StringReader;
//import java.util.List;
//import java.util.function.Consumer;
//import java.util.stream.Collectors;
//
//import org.gradle.testkit.runner.BuildResult;
//import org.gradle.testkit.runner.GradleRunner;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//
//import static org.assertj.core.api.Assertions.assertThat;
///**
// * Copyright: Copyright (c) 2021.
// * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
// *
// * <p>
// * Description:
// *
// * @author jiac
// * @version 0.0.5.1 2021/7/1:18:48
// * @since 0.0.5.1
// */
//class SonarScannerPluginTest {
//
//    private File projectDir;
//
//    @BeforeEach
//    void setup(@TempDir File projectDir) throws IOException {
//        this.projectDir = projectDir;
//    }
//
//    @Test
//    void sonarTestOK(){
//        createProject(this.projectDir);
//        createBuildScript(this.projectDir);
//        BuildResult result = GradleRunner.create().withDebug(true).withProjectDir(this.projectDir)
//                .withArguments("sonar").withPluginClasspath().build();
//        assertThat(readLines(result.getOutput())).containsSequence("2 actionable tasks: 2 executed");
//    }
//
//
//    @Test
//    void sonarTestOKWithProperties(){
//        createProject(this.projectDir);
//        createBuildScriptOKWithProperties(this.projectDir);
//        BuildResult result = GradleRunner.create().withDebug(true).withProjectDir(this.projectDir)
//                .withArguments("sonar").withPluginClasspath().build();
//        assertThat(readLines(result.getOutput())).containsSequence("2 actionable tasks: 2 executed");
//    }
//
//    @Test
//    void sonarTestFailWithHostUrl(){
//        createProject(this.projectDir);
//        createBuildScriptFailWithHostUrl(this.projectDir);
//        BuildResult result = GradleRunner.create().withDebug(true).withProjectDir(this.projectDir)
//                .withArguments("sonar").withPluginClasspath().buildAndFail();
//        assertThat(readLines(result.getOutput())).containsSequence(
//                "> Task :sonarqube FAILED",
//                "SonarQube server [http://www.baidu.com] can not be reached");
//    }
//
//    @Test
//    void sonarTestFailWithLogin(){
//        createProject(this.projectDir);
//        createBuildScriptFailWithLogin(this.projectDir);
//        BuildResult result = GradleRunner.create().withDebug(true).withProjectDir(this.projectDir)
//                .withArguments("sonar").withPluginClasspath().buildAndFail();
//
//        assertThat( readLines(result.getOutput())).containsSequence(
//                "Execution failed for task ':sonarqube'.",
//                "> Not authorized. Please check the properties sonar.login and sonar.password.");
//    }
//
//    private void createBuildScriptOKWithProperties(File dir) {
//        withPrintWriter(new File(dir, "build.gradle"), (writer) -> {
//            writer.println("plugins {");
//            writer.println("	id 'java'");
//            writer.println("	id 'com.jcohy.sonar.scanner'");
//            writer.println("}");
//            writer.println();
//            writer.println("ext {");
//            writer.println("	set('sonar.host.url', 'http://192.168.11.244:9000')");
//            writer.println("	set('sonar.projectVersion', '1.0.0')");
//            writer.println("	set('sonar.projectDescription', 'Test Sonar With Properties！')");
//            writer.println("	set('sonar.login', '3abb5ed05e3cf7650e28bea0c29fdfb3803348d2')");
//            writer.println("	set('sonar.links.homepage', 'https://github.com/jcohy/jcohy-gradle-plugins')");
//            writer.println("	set('sonar.jacoco.issue', 'https://github.com/jcohy/jcohy-gradle-plugins/issues')");
//            writer.println("	set('sonar.jacoco.scm', 'https://github.com/jcohy/jcohy-gradle-plugins')");
//            writer.println("}");
//            writer.println();
//            writer.println("repositories {");
//            writer.println("	maven { url 'https://maven.aliyun.com/repository/public' } ");
//            writer.println("	mavenCentral()");
//            writer.println("}");
//            writer.println();
//            writer.println("dependencies {");
//            writer.println("	testImplementation 'org.junit.jupiter:junit-jupiter:5.6.0'");
//            writer.println("	testImplementation 'org.assertj:assertj-core:3.11.1'");
//            writer.println("}");
//            writer.println();
//            writer.println("test {");
//            writer.println("	useJUnitPlatform()");
//            writer.println("}");
//        });
//    }
//
//    private void createBuildScriptFailWithLogin(File dir) {
//        withPrintWriter(new File(dir, "build.gradle"), (writer) -> {
//            writer.println("plugins {");
//            writer.println("	id 'java'");
//            writer.println("	id 'com.jcohy.sonar.scanner'");
//            writer.println("}");
//            writer.println();
//            writer.println("ext {");
//            writer.println("	set('sonar.login', 'test')");
//            writer.println("}");
//            writer.println();
//            writer.println("repositories {");
//            writer.println("	maven { url 'https://maven.aliyun.com/repository/public' } ");
//            writer.println("	mavenCentral()");
//            writer.println("}");
//            writer.println();
//            writer.println("dependencies {");
//            writer.println("	testImplementation 'org.junit.jupiter:junit-jupiter:5.6.0'");
//            writer.println("	testImplementation 'org.assertj:assertj-core:3.11.1'");
//            writer.println("}");
//            writer.println();
//            writer.println("test {");
//            writer.println("	useJUnitPlatform()");
//            writer.println("}");
//        });
//    }
//
//    private void createProject(File dir){
//        File examplePackage = new File(dir, "src/test/java/example");
//        examplePackage.mkdirs();
//        createTestSource("ExampleTests", examplePackage);
//        createTestSource("MoreTests", examplePackage);
//    }
//
//    private void createBuildScript(File dir) {
//        withPrintWriter(new File(dir, "build.gradle"), (writer) -> {
//            writer.println("plugins {");
//            writer.println("	id 'java'");
//            writer.println("	id 'com.jcohy.sonar.scanner'");
//            writer.println("}");
//            writer.println();
//            writer.println("repositories {");
//            writer.println("	maven { url 'https://maven.aliyun.com/repository/public' } ");
//            writer.println("	mavenCentral()");
//            writer.println("}");
//            writer.println();
//            writer.println("dependencies {");
//            writer.println("	testImplementation 'org.junit.jupiter:junit-jupiter:5.6.0'");
//            writer.println("	testImplementation 'org.assertj:assertj-core:3.11.1'");
//            writer.println("}");
//            writer.println();
//            writer.println("test {");
//            writer.println("	useJUnitPlatform()");
//            writer.println("}");
//        });
//    }
//
//    private void createBuildScriptFailWithHostUrl(File dir) {
//        withPrintWriter(new File(dir, "build.gradle"), (writer) -> {
//            writer.println("plugins {");
//            writer.println("	id 'java'");
//            writer.println("	id 'com.jcohy.sonar.scanner'");
//            writer.println("}");
//            writer.println();
//            writer.println("ext {");
//            writer.println("    set('sonar.host.url', 'http://www.baidu.com')");
//            writer.println("	set('sonar.projectVersion', '1.0.0')");
//            writer.println("	set('sonar.sonar.projectDescription', 'hahaha')");
//            writer.println("}");
//            writer.println();
//            writer.println("repositories {");
//            writer.println("	maven { url 'https://maven.aliyun.com/repository/public' } ");
//            writer.println("	mavenCentral()");
//            writer.println("}");
//            writer.println();
//            writer.println("dependencies {");
//            writer.println("	testImplementation 'org.junit.jupiter:junit-jupiter:5.6.0'");
//            writer.println("	testImplementation 'org.assertj:assertj-core:3.11.1'");
//            writer.println("}");
//            writer.println();
//            writer.println("test {");
//            writer.println("	useJUnitPlatform()");
//            writer.println("}");
//        });
//    }
//
//    private void createTestSource(String name, File dir) {
//        withPrintWriter(new File(dir, name + ".java"), (writer) -> {
//            writer.println("package example;");
//            writer.println();
//            writer.println("import org.junit.jupiter.api.Test;");
//            writer.println();
//            writer.println("import static org.assertj.core.api.Assertions.assertThat;");
//            writer.println();
//            writer.println("class " + name + "{");
//            writer.println();
//            writer.println("	@Test");
//            writer.println("	void fail() {");
//            writer.println("		assertThat(true).isFalse();");
//            writer.println("	}");
//            writer.println();
//            writer.println("	@Test");
//            writer.println("	void bad() {");
//            writer.println("		assertThat(5).isLessThan(4);");
//            writer.println("	}");
//            writer.println();
//            writer.println("	@Test");
//            writer.println("	void ok() {");
//            writer.println("	}");
//            writer.println();
//            writer.println("}");
//        });
//    }
//
//    private void withPrintWriter(File file, Consumer<PrintWriter> consumer) {
//        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
//            consumer.accept(writer);
//        }
//        catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    private List<String> readLines(String output) {
//        try (BufferedReader reader = new BufferedReader(new StringReader(output))) {
//            return reader.lines().collect(Collectors.toList());
//        }
//        catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//}