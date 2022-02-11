package com.jcohy.convention.dsl;

import org.gradle.api.Project;
import org.gradle.api.tasks.Console;

/**
 * 描述: .
 *
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/2/11:16:17
 * @since 2022.0.1
 */
public class PomExtension {

    public static final String OSS_EXTENSION_NAME = "pomExtension";

    public String username;

    public String password;

    public String gitUrl;

    public String organizationName;

    public String organizationUrl;

    public String scmConnection;

    public String scmDeveloperConnection;

    public String licenseName;

    public String licenseUrl;

    public String developerName;

    public String developerEmail;

    public String issueSystem;

    public String issueUrl;

    private final Project project;

    @Console
    boolean logDocuments = false;

    public PomExtension(Project project) {
        this.project = project;
    }

    public String getUsername() {
        return this.username;
    }

    public PomExtension setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public PomExtension setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getGitUrl() {
        return this.gitUrl;
    }

    public PomExtension setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
        return this;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public PomExtension setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
        return this;
    }

    public String getOrganizationUrl() {
        return this.organizationUrl;
    }

    public PomExtension setOrganizationUrl(String organizationUrl) {
        this.organizationUrl = organizationUrl;
        return this;
    }

    public String getScmConnection() {
        return this.scmConnection;
    }

    public PomExtension setScmConnection(String scmConnection) {
        this.scmConnection = scmConnection;
        return this;
    }

    public String getScmDeveloperConnection() {
        return this.scmDeveloperConnection;
    }

    public PomExtension setScmDeveloperConnection(String scmDeveloperConnection) {
        this.scmDeveloperConnection = scmDeveloperConnection;
        return this;
    }

    public String getLicenseName() {
        return this.licenseName;
    }

    public PomExtension setLicenseName(String licenseName) {
        this.licenseName = licenseName;
        return this;
    }

    public String getLicenseUrl() {
        return this.licenseUrl;
    }

    public PomExtension setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
        return this;
    }

    public String getDeveloperName() {
        return this.developerName;
    }

    public PomExtension setDeveloperName(String developerName) {
        this.developerName = developerName;
        return this;
    }

    public String getDeveloperEmail() {
        return this.developerEmail;
    }

    public PomExtension setDeveloperEmail(String developerEmail) {
        this.developerEmail = developerEmail;
        return this;
    }

    public String getIssueSystem() {
        return this.issueSystem;
    }

    public PomExtension setIssueSystem(String issueSystem) {
        this.issueSystem = issueSystem;
        return this;
    }

    public String getIssueUrl() {
        return this.issueUrl;
    }

    public PomExtension setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public boolean isLogDocuments() {
        return this.logDocuments;
    }

    public PomExtension setLogDocuments(boolean logDocuments) {
        this.logDocuments = logDocuments;
        return this;
    }
}
