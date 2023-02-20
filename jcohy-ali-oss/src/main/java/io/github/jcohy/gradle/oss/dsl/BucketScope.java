package io.github.jcohy.gradle.oss.dsl;


import com.aliyun.oss.model.CannedAccessControlList;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:11:01
 * @since 0.0.5.1
 */
public enum BucketScope {

    /**
     * 私有.
     */
    PRIVATE("private"),

    /**
     * 公共读.
     */
    READ("read"),

    /**
     * 公共读.
     */
    WRITE("write"),

    /**
     * 公共读写.
     */
    READ_WRITE("public-read-write");

    private final String scope;

    BucketScope(String scope) {
        this.scope = scope;
    }

    public static CannedAccessControlList getAccessControl(BucketScope scope) {
        switch (scope) {
            case READ:
                return CannedAccessControlList.PublicRead;
            case READ_WRITE:
                return CannedAccessControlList.PublicReadWrite;
            default:
                return CannedAccessControlList.Private;
        }
    }

    public String getScope() {
        return this.scope;
    }
}

