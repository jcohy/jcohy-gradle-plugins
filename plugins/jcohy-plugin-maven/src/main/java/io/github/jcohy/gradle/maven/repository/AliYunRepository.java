package io.github.jcohy.gradle.maven.repository;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description: 项目构件发布的仓库地址
 *
 * @author jiac
 * @version 0.0.5.1 2021/3/5:12:47
 * @since 0.0.5.1
 */
public enum AliYunRepository {

    /**
     * 快照版本对应的仓库.
     */
    SNAPSHOT("jcohy-aliyun-snapshots", "JcohyAliyun",
            "https://packages.aliyun.com/maven/repository/2114765-release-sAPkIv/", true),

    /**
     * 发布版本对应的仓库.
     */
    RELEASE("jcohy-aliyun-releases", "JcohyAliyun",
            "https://packages.aliyun.com/maven/repository/2114765-snapshot-6mT705/", false);

    private final String id;

    private final String name;

    private final String url;

    private final boolean snapshotsEnabled;

    AliYunRepository(String id, String name, String url, Boolean snapshotsEnabled) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.snapshotsEnabled = snapshotsEnabled;
    }

    /**
     * 根据获取仓库地址
     *
     * @param status the release status
     * @return the artifact repository
     */
    public static AliYunRepository of(Boolean status) {
        return status ? AliYunRepository.RELEASE : AliYunRepository.SNAPSHOT;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getUrl() {
        return this.url;
    }

    public boolean isSnapshotsEnabled() {
        return this.snapshotsEnabled;
    }
}
