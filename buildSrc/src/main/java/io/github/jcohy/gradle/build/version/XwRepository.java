package io.github.jcohy.gradle.build.version;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description: 项目构件发布的仓库地址
 *
 * @author jiac
 * @version 0.0.5.1 2021/3/5:12:47
 * @since 0.0.5.1
 */
public enum XwRepository {

    /**
     * 快照版本对应的仓库.
     */
    SNAPSHOT("jcohy-xw-snapshots", "xw",
            "http://3b7t671894.zicp.vip:53740/repository/snapshot", true),

    /**
     * 发布版本对应的仓库.
     */
    RELEASE("jcohy-xw-releases", "xw",
            "http://3b7t671894.zicp.vip:53740/repository/releases", false);

    private final String id;

    private final String name;

    private final String url;

    private final boolean snapshotsEnabled;

    XwRepository(String id, String name, String url, Boolean snapshotsEnabled) {
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
    public static XwRepository of(ReleaseStatus status) {
        return switch (status) {
            case PRERELEASE, SNAPSHOT -> SNAPSHOT;
            case GENERAL_AVAILABILITY -> RELEASE;
        };
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
