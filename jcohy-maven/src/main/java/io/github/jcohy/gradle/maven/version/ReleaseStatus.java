package io.github.jcohy.gradle.maven.version;

import org.gradle.api.Project;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description: Release 版本状态
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/17:15:51
 * @since 0.0.5.1
 */
public enum ReleaseStatus {

    /**
     * 快照版本
     */
    SNAPSHOT,
    /**
     * 预发布版本
     */
    PRERELEASE,
    /**
     * 可用版本
     */
    GENERAL_AVAILABILITY;

    private static final Pattern PRERELEASE_PATTERN = Pattern.compile("[A-Za-z0-9.\\-]+?(M|RC)\\d+");

    private static final String SNAPSHOT_SUFFIX = "SNAPSHOT";

    /**
     * 根据版本名 获取 {@link ReleaseStatus status}
     *
     * @param project 项目版本
     * @return 获取此项目的 release status
     */
    public static ReleaseStatus ofProject(Project project) {
        String version = project.getVersion().toString();
        Assert.notNull(version, "Version must not be null");
        if (version.endsWith(SNAPSHOT_SUFFIX)) {
            return SNAPSHOT;
        }
        if (PRERELEASE_PATTERN.matcher(version).matches()) {
            return PRERELEASE;
        }
        return GENERAL_AVAILABILITY;
    }
}
