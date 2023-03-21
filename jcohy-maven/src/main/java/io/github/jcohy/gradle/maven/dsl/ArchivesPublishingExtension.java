package io.github.jcohy.gradle.maven.dsl;

import io.github.jcohy.gradle.maven.constant.RepositoryType;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2023 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2023/2/21 12:33
 * @since 1.0.0
 */
public class ArchivesPublishingExtension {

    public static final String JCOHY_MAVEN_PUBLISHING_EXTENSION_NAME = "archivesPublishing";

    private final Project project;

    @Input
    public RepositoryType name = RepositoryType.SONATYPE;

    @Input
    private String username;

    @Input
    private String release;

    @Input
    private String snapshot;

    @Input
    private String password;

    @Input
    private String publishKey;

    @Input
    private String publishSecret;

    public ArchivesPublishingExtension(Project project) {
        this.project = project;
    }

    public RepositoryType getName() {
        return name;
    }

    public ArchivesPublishingExtension setName(RepositoryType name) {
        this.name = name;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ArchivesPublishingExtension setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getRelease() {
        return release;
    }

    public ArchivesPublishingExtension setRelease(String release) {
        this.release = release;
        return this;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public ArchivesPublishingExtension setSnapshot(String snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ArchivesPublishingExtension setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPublishKey() {
        return publishKey;
    }

    public ArchivesPublishingExtension setPublishKey(String publishKey) {
        this.publishKey = publishKey;
        return this;
    }

    public String getPublishSecret() {
        return publishSecret;
    }

    public ArchivesPublishingExtension setPublishSecret(String publishSecret) {
        this.publishSecret = publishSecret;
        return this;
    }
}
