package com.jcohy.oss.dsl;

import org.gradle.api.tasks.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/23:12:39
 * @since 0.0.5.1
 */
public class Upload {

    /**
     * 源文件目录
     */
    @Input
    private String source;

    /**
     * 忽略那些要上传的文件。如果指定了 ignoreSourceDir 属性为 {@code false}. 请注意文件名不能重复。
     */
    private List<String> exclusions = new ArrayList<>();

    /**
     * 上传到 oss 指定的 prefix 目录下。
     * 例如:
     * 假设 Test.txt 文件在如下路径中
     * <pre>
     * -- src
     * --- main
     * ---- com
     * ----- jcohy
     * ------ oss
     * ------- Test.txt
     *
     *    alioss {
     * 	    upload {
     * 	        source = "src/main"
     * 	        prefix = "jcohy"
     * 	    }
     *    }
     * </pre>
     * 此设置会将 src/main 目录中的文件上传至指定的 bucket，下 jcohy 目录下。其路径不包含 source 指定的目录：
     * http://jcohy.oss-cn-beijing.aliyuncs.com/jcohy/com/jcohy/oss/Test.txt
     */
    @Input
    private String prefix;

    /**
     * 是否忽略 source 下的目录递归上传。默认为 {@code false}。如果设置为  {@code true}。则会忽略 {@code source} 属性，将所有文件上传至 prefix 指定的目录中
     * 例如:
     * 假设 Test.txt 文件在如下路径中
     * <pre>
     * -- src
     * --- main
     * ---- com
     * ----- jcohy
     * ------ oss
     * ------- Test.txt
     *
     *    alioss {
     * 	    upload {
     * 	        source = "src/main"
     * 	        prefix = "jcohy"
     * 	        ignoreSourceDir = "true"
     * 	    }
     *    }
     * </pre>
     * 此设置会将 src/main 目录中的文件上传至指定的 bucket，下 jcohy 目录下。其路径为
     * http://jcohy.oss-cn-beijing.aliyuncs.com/jcohy/Test.txt
     * 如果 ignoreSourceDir 为 {@code false} ,则上传的路径为:
     * http://jcohy.oss-cn-beijing.aliyuncs.com/jcohy/src/main/Test.txt
     */
    @Input
    private boolean ignoreSourceDir = false;

    /**
     * 是否递归目录上传
     */
    @Input
    private boolean recursion = true;

    public boolean isRecursion() {
        return recursion;
    }

    public void setRecursion(boolean recursion) {
        this.recursion = recursion;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getExclusions() {
        return this.exclusions;
    }

    public void setExclusions(List<String> exclusions) {
        this.exclusions = exclusions;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isIgnoreSourceDir() {
        return this.ignoreSourceDir;
    }

    public void setIgnoreSourceDir(boolean ignoreSourceDir) {
        this.ignoreSourceDir = ignoreSourceDir;
    }
}
