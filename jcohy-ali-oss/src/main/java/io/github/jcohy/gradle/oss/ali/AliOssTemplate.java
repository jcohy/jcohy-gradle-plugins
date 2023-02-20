package io.github.jcohy.gradle.oss.ali;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.utils.StringUtils;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectResult;
import io.github.jcohy.gradle.oss.OssFile;
import io.github.jcohy.gradle.oss.dsl.BucketScope;
import io.github.jcohy.gradle.oss.dsl.AliOssExtension;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:16:54
 * @since 0.0.5.1
 */
public class AliOssTemplate {

    private final OSSClient client;

    private final AliOssExtension extension;

    private final String bucket;

    public AliOssTemplate(AliOssExtension extension) {
        this.extension = extension;
        this.client = createClient(extension);
        this.bucket = extension.getBucket();
    }

    private OSSClient createClient(AliOssExtension extension) {
        // 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数 。
        ClientConfiguration conf = new ClientConfiguration();
        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
        conf.setMaxConnections(1024);
        // 设置Socket 层传输数据的超时时间，默认为 50000 毫秒。
        // conf.setSocketTimeout(50000);
        // 设置建立连接的超时时间，默认为50000毫秒。
        // conf.setConnectionTimeout(50000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
        conf.setConnectionRequestTimeout(1000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
        // conf.setIdleConnectionTime(60000);
        // 设置失败请求重试次数，默认为3次。
        // conf.setMaxErrorRetry(5);
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(extension.getAccessKey(),
                extension.getSecretKey());
        OSSClient ossClient = new OSSClient(extension.getEndpoint(), credentialsProvider, conf);
        ossClient.setBucketAcl(extension.getBucket(),
                BucketScope.getAccessControl(extension.getScope()));
        return ossClient;
    }

    public OssFile putFile(String key, File file) {
        return putFile(this.bucket, key, file);
    }

    public OssFile putFile(String bucket, String key, File file) {
        return put(bucket, file, key, true);
    }

    public List<OssFile> putUploadFile(Map<String, File> uploadFiles) {
        List<OssFile> list = new ArrayList<>();
        uploadFiles.forEach((key, value) -> list.add(this.putFile(key, value)));
        return list;
    }

    public OssFile put(String bucketName, File file, String key, boolean cover) {
        key = formatKey(key);
        createBucket(bucketName);
        // 覆盖上传
        if (cover) {
            this.client.putObject(bucketName, key, file);
        }
        else {
            PutObjectResult response = this.client.putObject(bucketName, key, file);
            int retry = 0;
            int retryCount = 4;
            while (StringUtils.isNullOrEmpty(response.getETag()) && retry < retryCount) {
                response = this.client.putObject(bucketName, key, file);
                retry++;
            }
        }
        OssFile ossFile = new OssFile();
        ossFile.setOriginalName(key);
        ossFile.setName(key);
        ossFile.setDomain(getOssHost(bucketName));
        ossFile.setLink(fileLink(bucketName, key));
        return ossFile;
    }

    public void createBucket(String bucket) {
        if (!bucketExists(bucket)) {
            CreateBucketRequest request = new CreateBucketRequest(this.bucket);
            request.setCannedACL(BucketScope.getAccessControl(extension.getScope()));
            this.client.createBucket(request);
        }
    }

    public boolean bucketExists(String bucket) {
        return this.client.doesBucketExist(extension.getBucket());
    }

    public String fileLink(String key) {
        return getOssHost().concat("/").concat(key);
    }

    public String fileLink(String bucket, String key) {
        return getOssHost(bucket).concat("/").concat(key);
    }

    public String getOssHost() {
        return getOssHost(extension.getBucket());
    }

    public String getOssHost(String bucketName) {
        String prefix = this.extension.getEndpoint().contains("https://") ? "https://" : "http://";
        return prefix + bucketName + "."
                + this.extension.getEndpoint().replaceFirst(prefix, "");
    }

    public String formatKey(String key) {
        // / 用于分割路径，可快速创建子目录，但不要以 / 或 \ 开头，不要出现连续的 /；
        if (StringUtils.isNullOrEmpty(key)) {
            return "";
        }

        if (key.startsWith("\\") || key.startsWith("/")) {
            key = key.substring(1);
        }

        Pattern pattern = Pattern.compile("[\\\\|/]{2,}");
        return pattern.matcher(key).replaceAll("/");
    }
}
