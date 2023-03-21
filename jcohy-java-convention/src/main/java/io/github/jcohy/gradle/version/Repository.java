package io.github.jcohy.gradle.version;

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
    SNAPSHOT("jcohy-snapshots", "Jcohy-Snapshots",
            "http://192.168.11.230:8081/repository/snapshot", true),

    /**
     * 预发布版本对应的仓库.
     */
    MILESTONE("jcohy-milestones", "Jcohy-Milestones",
            "http://192.168.11.230:8081/repository/milestone", false),

    /**
     * 发布版本对应的仓库.
     */
    RELEASE("jochy-releases", "Jcohy-Releases",
            "http://192.168.11.230:8081/repository/releases", false);

    private final String id;

    private final String name;

    private final String url;

    private final boolean snapshotsEnabled;

    Repository(String id, String name, String url, Boolean snapshotsEnabled) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.snapshotsEnabled = snapshotsEnabled;
    }

    /**
     * 根据  {@link ReleaseStatus} 获取仓库地址
     *
     * @param status the release status
     * @return the artifact repository
     */
    public static Repository of(ReleaseStatus status) {
        switch (status) {
            case GENERAL_AVAILABILITY:
                return RELEASE;
            case PRERELEASE:
                return MILESTONE;
            case SNAPSHOT:
                return SNAPSHOT;
            default:
                return RELEASE;
        }
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
