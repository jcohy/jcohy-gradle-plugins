package io.github.jcohy.gradle.maven.constant;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:15:43
 * @since 2022.04.0
 */
public enum RepositoryType {
    SONATYPE("sonatype"),
    SONATYPES01("sonatypeS01"),
    ALIYUN("aliyun");

    private String name;

    RepositoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public RepositoryType setName(String name) {
        this.name = name;
        return this;
    }
}
