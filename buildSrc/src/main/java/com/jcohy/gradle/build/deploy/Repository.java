package com.jcohy.gradle.build.deploy;

import org.gradle.api.Project;
import org.springframework.util.Assert;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description: 项目构件发布的仓库地址
 *
 * @author jiac
 * @version 0.0.5.1 2021/3/5:12:47
 * @since 0.0.5.1
 */
public enum Repository {

    /**
     * 快照版本对应的仓库.
     */
    NEXUS_RELEASE_REPOSITORY("nexus-release", "nexus-release","https://oss.sonatype.org/service/local/staging/deploy/maven2"),

    NEXUS_SNAPSHOTS_REPOSITORY("nexus-snapshots", "nexus-snapshots","https://oss.sonatype.org/content/repositories/snapshots");

    private static final String SNAPSHOT_SUFFIX = "SNAPSHOT";

    private final String id;

    private final String name;

    private final String url;

    Repository(String id, String name,String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    /**
     * 根据版本名 获取 {@link Repository repository}
     *
     * @param project 项目版本
     * @return 获取此项目的 release status
     */
    public static Repository ofProject(Project project) {
        String version = project.getVersion().toString();
        Assert.notNull(version, "Version must not be null");
        String deploy = (String)project.getProperties().get("deployRepository");
        Repository repository = Repository.NEXUS_RELEASE_REPOSITORY;
        switch (deploy) {
            case "nexus-release" -> repository = Repository.NEXUS_RELEASE_REPOSITORY;
            case "nexus-snapshots" -> repository = Repository.NEXUS_SNAPSHOTS_REPOSITORY;
        }
        return repository;
    }
}
