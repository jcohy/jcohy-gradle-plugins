package io.github.jcohy.gradle.javadoc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.JavadocOutputLevel;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述: 默认 javadoc 配置约定.
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:11:53
 * @since 2022.0.1
 */
public class JavadocConventionsPlugin implements Plugin<Project> {

    private static final Logger log = LoggerFactory.getLogger(JavadocConventionsPlugin.class);
    /**
     * Spring 的样式表.
     */
    public static final String STYLESHEET_RESOURCE_NAME = "/io/github/jcohy/gradle/javadoc/internal/stylesheet.css";

    static final String STYLESHEET_FILE_NAME = "build/io.github.jcohy.gradle.javadoc-conventions/stylesheet.css";

    @Override
    public void apply(Project project) {
        // 定义一个名为 syncJavadocStylesheet 的 Sync 任务，主要用来复制样式文件
        TaskProvider<Sync> syncJavadocStylesheet = project.getTasks().register("syncJavadocStylesheet", Sync.class,
                (sync) -> {
                    sync.setGroup("Documentation");
                    sync.setDescription("Syncs the javadoc stylesheet");
                    String relativeToPath = project.relativeProjectPath(new File(STYLESHEET_FILE_NAME).getParent());
                    // 在 Gradle 6.4 之前，使用复制操作复制插件资源可以正常工作，而 Gradle 6.5.1 不再有效。
                    // 主要因为 testkit 将构建资源打成 main.jar 放在缓存中。
                    try {
                        URL resource = getClass().getResource(STYLESHEET_RESOURCE_NAME);
                        URLConnection urlConnection = resource.openConnection();
                        if (urlConnection instanceof JarURLConnection) {
                            resource = copyJarResources(STYLESHEET_RESOURCE_NAME, project.getBuildDir()  + "/tmp/stylesheet.css").toURI().toURL();
                        }
                        sync.from(resource);
                        sync.into(relativeToPath);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });

        // javadoc 通用配置
        project.getTasks().withType(Javadoc.class, (javadoc) -> {
            javadoc.dependsOn(syncJavadocStylesheet);
            StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) javadoc.getOptions();
            String title = title(project);
            options.setAuthor(true);
            options.setDocTitle(title);
            options.setEncoding(StandardCharsets.UTF_8.name());
            options.setMemberLevel(JavadocMemberLevel.PROTECTED);
            options.setOutputLevel(JavadocOutputLevel.QUIET);
            options.splitIndex(true);
            options.setStylesheetFile(project.file(STYLESHEET_FILE_NAME));
            options.setUse(true);
            options.setWindowTitle(title);

            // 跨模块的 @see 和 @link 引用消除警告
            javadoc.getLogging().captureStandardError(LogLevel.INFO);
            // 消除  “## warnings” 消息
            javadoc.getLogging().captureStandardOutput(LogLevel.INFO);
        });

    }

    /**
     * Javadoc 标题，移除 "-build" 后缀，并且将所有的 "-" 替换成 " "，首字母大写。
     *
     * @param project project.
     * @return 整理后的标题.
     */
    private String title(Project project) {
        String BUILD = "-build";
        String title = project.getRootProject().getName();
        if (title.endsWith(BUILD)) {
            title = title.substring(0, title.length() - BUILD.length());
        }
        title = title.replace("-", " ");
        char[] chars = title.toCharArray();
        boolean capitalizeNext = true;
        for (int i = 0; i < chars.length; i++) {
            if (capitalizeNext) {
                chars[i] = Character.toUpperCase(chars[i]);
                capitalizeNext = false;
            }
            if (chars[i] == ' ') {
                capitalizeNext = true;
            }
        }
        return new String(chars) + " API";
    }

    /**
     * 从 jar 包获取指定资源
     * @param resource 资源文件
     * @param destination 目标路径
     * @return /
     * @throws IOException /
     */
    public File copyJarResources(String resource, String destination) throws IOException {
        if (!resource.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }

        if (resource.endsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (not end with '/').");
        }


        File file = new File(resource);

        if(!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists() && !file.createNewFile()) {
            log.error("create file {} failed.",resource);
        }

        // Open and check input stream
        URL url = this.getClass().getResource(resource);
        URLConnection urlConnection = url.openConnection();

        try( InputStream is = urlConnection.getInputStream(); OutputStream os = new FileOutputStream(file)) {
            // Prepare buffer for data copying
            byte[] buffer = new byte[1024];
            int readBytes;
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        }
        return file;
    }
}
