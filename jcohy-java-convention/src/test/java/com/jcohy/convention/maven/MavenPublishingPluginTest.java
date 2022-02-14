package com.jcohy.convention.maven;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.SystemUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/2/11:18:38
 * @since 2022.0.1
 */
public class MavenPublishingPluginTest {

    @Test
    public void mavenDeployTest() throws Exception {
        File projectDir = new File("src/test/gradle");

        assertThat(runGradle(projectDir,"clean","publishToMavenLocal").getOutput()).contains("SUCCESS");

        String mavenHome = System.getProperty( "maven.home" );
        String pomPath = "";
        if(StringUtils.isBlank(mavenHome)) {
            pomPath = SystemUtils.getUserHome().getPath() + "\\.m2\\repository\\com\\example\\asciidoc\\1.1.1-SNAPSHOT\\asciidoc-1.1.1-SNAPSHOT.pom";
        } else {
            pomPath = mavenHome + "\\.m2\\repository\\com\\example\\asciidoc\\1.1.1-SNAPSHOT\\asciidoc-1.1.1-SNAPSHOT.pom";
        }
        File file = new File(pomPath);
        assertThat(file).exists();
        try(FileInputStream fis = new FileInputStream(file)) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model read = reader.read(fis);
            assertThat(read.getGroupId()).isEqualTo("com.example");
            assertThat(read.getArtifactId()).isEqualTo("asciidoc");
            assertThat(read.getVersion()).isEqualTo("1.1.1-SNAPSHOT");
            assertThat(read.getDescription()).isEqualTo("Jcohy Boot Docs");
            assertThat(read.getOrganization().getName()).isEqualTo("jcohy-example");
            assertThat(read.getOrganization().getUrl()).isEqualTo("https://github.com/jcohy-example");
            assertThat(read.getLicenses().get(0).getName()).isEqualTo("PSD");
            assertThat(read.getLicenses().get(0).getUrl()).isEqualTo("PSD");
            assertThat(read.getDevelopers().get(0).getName()).isEqualTo("aaa");
            assertThat(read.getDevelopers().get(0).getEmail()).isEqualTo("aaa@email.com");
            assertThat(read.getScm().getConnection()).isEqualTo("scm:git:git://github.com/jcohy/jcohy-example");
            assertThat(read.getScm().getDeveloperConnection()).isEqualTo("scm:git:git://github.com/jcohy");
            assertThat(read.getIssueManagement().getSystem()).isEqualTo("GITHUB");
            assertThat(read.getIssueManagement().getUrl()).isEqualTo("https://github.com");

            for(Dependency dependency : read.getDependencyManagement().getDependencies()) {
                String artifactId = dependency.getArtifactId();
                if( artifactId.equals("spring-cloud-alibaba-dependencies")){
                    assertThat(dependency.getGroupId()).isEqualTo("com.alibaba.cloud");
                    assertThat(dependency.getVersion()).isEqualTo("2021.1");
                }
                if( artifactId.equals("aliyun-spring-boot-dependencies")){
                    assertThat(dependency.getGroupId()).isEqualTo("com.alibaba.cloud");
                    assertThat(dependency.getVersion()).isEqualTo("1.0.0");
                }
                if( artifactId.equals("spring-cloud-services-dependencies")){
                    assertThat(dependency.getGroupId()).isEqualTo("io.pivotal.spring.cloud");
                    assertThat(dependency.getVersion()).isEqualTo("2.4.1");
                }
                if( artifactId.equals("spring-boot-dependencies")){
                    assertThat(dependency.getGroupId()).isEqualTo("org.springframework.boot");
                    assertThat(dependency.getVersion()).isEqualTo("2.4.6");
                }
                if( artifactId.equals("spring-boot-admin-dependencies")){
                    assertThat(dependency.getGroupId()).isEqualTo("de.codecentric");
                    assertThat(dependency.getVersion()).isEqualTo("2.4.3");
                }

                if( artifactId.equals("spring-cloud-dependencies")){
                    assertThat(dependency.getGroupId()).isEqualTo("org.springframework.cloud");
                    assertThat(dependency.getVersion()).isEqualTo("2020.0.3");
                }
            }
        }
    }

    private BuildResult runGradle(File projectDir,String... args) {
        return runGradle(projectDir,Collections.emptyMap(), args);
    }

    private BuildResult runGradle(File projectDir,Map<String, String> environment, String... args) {
        return GradleRunner.create()
                .withProjectDir(projectDir)
                .withGradleVersion("6.8.3")
//                .withEnvironment(environment)
                .withArguments(args)
                .withPluginClasspath()
                .withDebug(true)
                .build();
    }
}
