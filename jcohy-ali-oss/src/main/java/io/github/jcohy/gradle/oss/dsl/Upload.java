package io.github.jcohy.gradle.oss.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

/**
 * <p> 描述:
 * 上传到 oss 指定的 prefix 目录下。文件路径为 {oss-bucket}/{prefix}/{source}/{other}.
 * 其中默认忽略 source 目录，如果需要添加 source 目录，请将 ignoreSourceDir 属性设置为 false。
 * 例如:
 * 假设 Test.txt 文件在如下路径中
 * <pre>
 * -- src
 * --- main
 * ---- com
 * ----- jcohy
 * ------ oss
 * ------- Test.txt
 *    alioss {
 * 	    upload {
 * 	        source = "src/main"
 * 	        prefix = "jcohy"
 *        }
 *    }
 * </pre>
 * 此设置会将 src/main 目录下的文件递归上传至 {oss-bucket}/jcohy/com/jcohy/oss/Test.txt
 * <pre>
 * -- src
 * --- main
 * ---- com
 * ----- jcohy
 * ------ oss
 * ------- Test.txt
 *    alioss {
 * 	    upload {
 * 	        source = "src/main"
 * 	        prefix = "jcohy"
 * 	        ignoreSourceDir = false
 *        }
 *    }
 * </pre>
 * 此设置会将 src/main 目录下的文件递归上传至 {oss-bucket}/jcohy/src/main/com/jcohy/oss/Test.txt
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
     * 忽略那些要上传的文件。如果指定了 ignoreSourceDir 属性为 {@code true}. 请注意文件名不能重复。
     */
    @Input
    private List<String> exclusions = new ArrayList<>();

    /**
     * 指定文件前缀
     */
    @Input
    private String prefix;

    /**
     * 是否忽略 source 下的目录递归上传。默认为 {@code true}。如果设置为  {@code false}。则会将所有文件上传至 prefix 指定的目录中.
     */
    @Input
    private boolean ignoreSourceDir = true;

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
