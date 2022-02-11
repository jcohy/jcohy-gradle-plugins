package com.jcohy.oss;

import com.jcohy.oss.dsl.AliOssExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:11:04
 * @since 0.0.5.1
 */
public class OssUploadPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        AliOssExtension extension = project.getExtensions().create(AliOssExtension.OSS_EXTENSION_NAME,
                AliOssExtension.class, project);
        project.getTasks().create("uploadOssFiles", OssUploadTask.class,extension);
    }
}
