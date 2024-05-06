package io.github.jcohy.gradle.oss.dsl;

import groovy.lang.Closure;
import org.gradle.api.Project;
import org.gradle.api.tasks.Console;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Nested;
import org.gradle.util.ConfigureUtil;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:10:41
 * @since 0.0.5.1
 */
@SuppressWarnings("deprecation")
public class AliOssExtension {

    public static final String OSS_EXTENSION_NAME = "alioss";

    private final Project project;

    @Nested
    private final Upload upload = new Upload();

    @Console
    boolean logDocuments = false;

    @Input
    private String endpoint = "http://oss-cn-beijing.aliyuncs.com";

    @Input
    private String accessKey = "accessKey";

    @Input
    private String secretKey = "secretKey";

    @Input
    private String bucket = "jcohy-test";

    @Input
    private String payload;

    @Input
    private BucketScope scope = BucketScope.READ_WRITE;

    public AliOssExtension(Project project) {
        this.project = project;
    }

    public void upload(Closure<?> closure) {
        ConfigureUtil.configure(closure, this.upload);
    }

    public Upload getUpload() {
        return this.upload;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return this.bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Project getProject() {
        return this.project;
    }

    public boolean isLogDocuments() {
        return this.logDocuments;
    }

    public void setLogDocuments(boolean logDocuments) {
        this.logDocuments = logDocuments;
    }

    public BucketScope getScope() {
        return this.scope;
    }

    public void setScope(BucketScope scope) {
        this.scope = scope;
    }
}
