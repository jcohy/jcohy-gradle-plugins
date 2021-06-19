package com.jcohy.gradle.plugins.version;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/6/19:17:52
 * @since 1.0.0
 */
public class VersionTests {

	@Test
	void whenProjectVersionIsMilestone(){
		Project project = ProjectBuilder.builder().build();
		project.setVersion("1.2.3-M1");
		ReleaseStatus releaseStatus = ReleaseStatus.ofProject(project);
		assertThat(releaseStatus).isEqualTo(ReleaseStatus.PRERELEASE);
		Repository repository = Repository.of(releaseStatus);
		assertThat(repository).isEqualTo(Repository.MILESTONE);
	}

	@Test
	void whenProjectVersionIsReleaseCandidate(){
		Project project = ProjectBuilder.builder().build();
		project.setVersion("1.2.3-RC1");
		ReleaseStatus releaseStatus = ReleaseStatus.ofProject(project);
		assertThat(releaseStatus).isEqualTo(ReleaseStatus.PRERELEASE);
		Repository repository = Repository.of(releaseStatus);
		assertThat(repository).isEqualTo(Repository.MILESTONE);
	}

	@Test
	void whenProjectVersionIsRelease(){
		Project project = ProjectBuilder.builder().build();
		project.setVersion("1.2.3");
		ReleaseStatus releaseStatus = ReleaseStatus.ofProject(project);
		assertThat(releaseStatus).isEqualTo(ReleaseStatus.GENERAL_AVAILABILITY);
		Repository repository = Repository.of(releaseStatus);
		assertThat(repository).isEqualTo(Repository.RELEASE);
	}

	@Test
	void whenProjectVersionIsSnapshot(){
		Project project = ProjectBuilder.builder().build();
		project.setVersion("1.2.3-SNAPSHOT");
		ReleaseStatus releaseStatus = ReleaseStatus.ofProject(project);
		assertThat(releaseStatus).isEqualTo(ReleaseStatus.SNAPSHOT);
		Repository repository = Repository.of(releaseStatus);
		assertThat(repository).isEqualTo(Repository.SNAPSHOT);
	}
}
