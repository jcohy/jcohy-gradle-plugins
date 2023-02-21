package io.github.jcohy.gradle.maven.dsl;

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
public class ArchivesRepository {

    public String name;

    private String username;

    private String release;

    private String snapshot;

    private String password;

    private String publishKey;

    private String publishSecret;

    public ArchivesRepository(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(String snapshot) {
        this.snapshot = snapshot;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublishKey() {
        return publishKey;
    }

    public void setPublishKey(String publishKey) {
        this.publishKey = publishKey;
    }

    public String getPublishSecret() {
        return publishSecret;
    }

    public void setPublishSecret(String publishSecret) {
        this.publishSecret = publishSecret;
    }
}
