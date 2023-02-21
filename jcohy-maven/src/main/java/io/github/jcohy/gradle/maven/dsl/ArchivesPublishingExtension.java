package io.github.jcohy.gradle.maven.dsl;

import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/10/17:17:07
 * @since 2022.0.1
 */
public class ArchivesPublishingExtension {

    public static final String JCOHY_MAVEN_PUBLISHING_EXTENSION_NAME = "archivesPublishing";

    private NamedDomainObjectContainer<ArchivesRepository> archivesRepositories;

    public ArchivesPublishingExtension(Project project) {
        archivesRepositories = project.container(ArchivesRepository.class);
    }

    public void sonatype() {
        archivesRepositories.create("sonatype", repository -> {
            repository.setRelease("https://oss.sonatype.org/service/local/staging/deploy/maven2");
            repository.setSnapshot("https://oss.sonatype.org/content/repositories/snapshots");
        });
    }

    public void sonatype(Action<NamedDomainObjectContainer<ArchivesRepository>> action) {
        archivesRepositories.create("sonatype", repository -> {
            repository.setRelease("https://oss.sonatype.org/service/local/staging/deploy/maven2");
            repository.setSnapshot("https://oss.sonatype.org/content/repositories/snapshots");
        });
        action.execute(archivesRepositories);
    }

    public void sonatype2() {
        archivesRepositories.create("sonatype2", repository -> {
            repository.setRelease("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2");
            repository.setSnapshot("https://s01.oss.sonatype.org/content/repositories/snapshots");
        });
    }
}
