package io.github.jcohy.gradle.oss;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.github.jcohy.gradle.oss.ali.AliOssTemplate;
import io.github.jcohy.gradle.oss.dsl.AliOssExtension;
import io.github.jcohy.gradle.oss.dsl.Upload;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:10:54
 * @since 0.0.5.1
 */
public class OssUploadTask extends DefaultTask {

    private final Logger log = LoggerFactory.getLogger(OssUploadTask.class);

    private final AliOssExtension extension;

    private final Upload upload;

    private final Project project;

    @Inject
    public OssUploadTask(AliOssExtension extension) {
        this.extension = extension;
        this.upload = extension.getUpload();
        this.project = extension.getProject();
    }

    @TaskAction
    public void upload() {
        Map<String, File> uploadFiles = getUploadFiles();
        AliOssTemplate aliOssTemplate = new AliOssTemplate(extension);
        List<OssFile> ossFiles = aliOssTemplate.putUploadFile(uploadFiles);
        ossFiles.forEach((ossFile) -> {
            log.info(ossFile.getName() + "：文件上传成功!!!" + "下载地址：" + ossFile.getLink());
        });
    }

    /**
     * 获取需要上传的文件
     *
     * @return /
     */

    @Input
    public Map<String, File> getUploadFiles() {
        String prefix = this.upload.getPrefix();
        Set<File> localFiles = project.getLayout().getProjectDirectory()
                .dir(this.upload.getSource()).getAsFileTree().getFiles()
                .stream()
                .filter(file -> !contains(file.getPath()))
                .collect(Collectors.toSet());
        Map<String, File> uploadFiles;
        if (!this.upload.isRecursion()) {
            uploadFiles = localFiles.stream()
                    .collect(Collectors.toMap((key) -> getUploadFileName(key.getName()), Function.identity()));
        }
        else {
            uploadFiles = localFiles
                    .stream()
                    .collect(Collectors.toMap((key) -> prefix + "/" + getUploadFilePath(key.getPath()), Function.identity()));
        }
        return uploadFiles;
    }

    private String getUploadFilePath(String localFilePath) {
        localFilePath = localFilePath.replace("\\", "/");
        String source = this.upload.getSource();
        int index = localFilePath.indexOf(source);
        return this.upload.isIgnoreSourceDir() ?
                localFilePath.substring(index + source.length() + 1) :
                localFilePath.substring(index);
    }

    private String getUploadFileName(String localFileName) {
        String source = this.upload.getSource();
        String prefix = this.upload.getPrefix();
        return this.upload.isIgnoreSourceDir() ? prefix + "/" + localFileName
                : prefix + "/" + source + "/" + localFileName;
    }

    public boolean contains(String filePath) {
        filePath = filePath.replace("\\", "/");
        for (String e : extension.getUpload().getExclusions()) {
            if (filePath.contains(e)) {
                return true;
            }
        }
        return false;
    }
}
