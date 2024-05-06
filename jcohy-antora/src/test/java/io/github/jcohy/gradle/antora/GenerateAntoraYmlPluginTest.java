package io.github.jcohy.gradle.antora;

import java.nio.file.Files;
import java.nio.file.Path;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/16 11:24
 * @since 2024.0.1
 */
class GenerateAntoraYmlPluginTest {

	public static final String OUTPUT_FILE_PATH = "build/generated-antora-resources/antora.yml";

	@Test
	void versionWhenRelease(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			version = '1.0.0'""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();

		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
			name: spring-security
			version: 1.0.0""");
	}

	@Test
	void versionWhenSnapshot(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			version = '1.0.0-SNAPSHOT'""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();

		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
			name: spring-security
			version: 1.0.0
			prerelease: -SNAPSHOT
			""");
	}
	@Test
	void versionWhenMilestone(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			version = '1.0.0-M1'""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();

		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
			name: spring-security
			version: 1.0.0-M1
			prerelease: true
			""");
	}

	@Test
	void nameDefaultsToProjectName(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("settings.gradle"), """
			rootProject.name = 'spring-security-docs'""");
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();

		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
			name: spring-security-docs""");
	}

	@Test
	void antoraYmlDefaults(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();
		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent(Files.readString(projectDir.resolve("antora.yml")));
	}


	@Test
	void noAntoraYmlTemplate(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();
		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("name: " + projectDir.toFile().getName());
	}

	@Test
	void antoraYmlCustomasciidocAttributesAsDeferredProperty(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			def springVersion = 'wrong';
			tasks.named('generateAntoraYml') {
				asciidocAttributes.put('springVersion', providers.provider { springVersion })
			}
			springVersion = '6.0.0'
			""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();
		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
				name: spring-security
				asciidoc:
				  attributes:
				    springVersion: 6.0.0""");
	}



	@Test
	void antoraYmlCustomasciidocAttributesAsProperty(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			tasks.named('generateAntoraYml') {
				asciidocAttributes['springVersion'] = '6.0.0'
			}
			""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();
		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
				name: spring-security
				asciidoc:
				  attributes:
				    springVersion: 6.0.0""");
	}

	@Test
	void antoraYmlCustomasciidocAttributesAsMap(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			tasks.named('generateAntoraYml') {
				asciidocAttributes = ['springVersion' : '6.0.0']
			}
			""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();
		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
				name: spring-security
				asciidoc:
				  attributes:
				    springVersion: 6.0.0""");
	}

	@Test
	void baseAntoraAndTaskMergeAsciidocAttributesWithTaskFirst(@TempDir Path projectDir) throws Exception {
		Files.writeString(projectDir.resolve("build.gradle"), """
			plugins {
				id 'io.github.jcohy.gradle.generate-antora-yml'
			}
			tasks.named('generateAntoraYml') {
				asciidocAttributes = ['spring-version' : '6.0.0']
			}
			""");
		Files.writeString(projectDir.resolve("antora.yml"), """
			name: spring-security
			asciidoc:
			  attributes:
			    import-java: example$docs-src/main/java/org/springframework/docs
			""");
		GradleRunner runner = GradleRunner.create()
				.withProjectDir(projectDir.toFile())
				.withPluginClasspath()
				.withArguments("generateAntoraYml", "--stacktrace");
		BuildResult build = runner.build();
		assertThat(projectDir.resolve(OUTPUT_FILE_PATH)).hasContent("""
				name: spring-security
				asciidoc:
				  attributes:
				    spring-version: 6.0.0
				    import-java: example$docs-src/main/java/org/springframework/docs""");
	}
}