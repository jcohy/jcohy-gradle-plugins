package com.jcohy.checkstyle.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 支持 {@code .springjavaformatconfig} 文件，在每个项目的根目录上设置。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:16:36
 * @since 0.0.5.1
 */
public interface JavaFormatConfig {
    
    /**
     * {@link JavaFormatConfig} 使用 {@link IndentationStyle#TABS}.
     */
    JavaFormatConfig TABS = new DefaultJavaFormatConfig(IndentationStyle.TABS);
    
    /**
     * {@link JavaFormatConfig} 使用 {@link IndentationStyle#SPACES}.
     */
    JavaFormatConfig SPACES = new DefaultJavaFormatConfig(IndentationStyle.SPACES);
    
    /**
     *  默认使用 TABS 进行缩进 {@link JavaFormatConfig}.
     */
    JavaFormatConfig DEFAULT = TABS;
    
    /**
     * 通过从给定文件中搜索来查找并加载 {@code .springjavaformatconfig}.
     * @param path 给定的文件
     * @return 加载 {@link JavaFormatConfig}, 当 {@code .springjavaformatconfig} 文件不存在时加载 {@link #DEFAULT}.
     */
    static JavaFormatConfig findFrom(Path path) {
        return findFrom((path != null) ? path.toFile() : (File) null);
    }
    
    /**
     * 通过从给定文件中搜索来查找并加载 {@code .springjavaformatconfig}.
     * @param file 从指定文件或目录查找
     * @return 加载 {@link JavaFormatConfig}, 当 {@code .springjavaformatconfig} 文件不存在时加载 {@link #DEFAULT}.
     */
    static JavaFormatConfig findFrom(File file) {
        if (file != null && file.isFile()) {
            return findFrom(file.getParentFile());
        }
        try {
            while (file != null) {
                File candidate = new File(file, ".springjavaformatconfig");
                if (candidate.exists() && candidate.isFile()) {
                    return load(candidate);
                }
                file = file.getParentFile();
            }
        }
        catch (Exception ex) {
        }
        return DEFAULT;
    }
    
    /**
     * 从指定文件加载 {@link JavaFormatConfig}.
     * @param file 加载文件
     * @return the loaded config
     */
    static JavaFormatConfig load(File file) {
        try {
            return PropertiesJavaFormatConfig.load(file);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    /**
     * 从指定的流中加载 {@link JavaFormatConfig}.
     * @param inputStream the input stream to load
     * @return the loaded config
     */
    static JavaFormatConfig load(InputStream inputStream) {
        try {
            return PropertiesJavaFormatConfig.load(inputStream);
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
    /**
     * 返回应该与项目一起使用的缩进样式.
     * @return the indentation style
     */
    IndentationStyle getIndentationStyle();
}
