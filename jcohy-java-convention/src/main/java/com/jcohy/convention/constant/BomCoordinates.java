package com.jcohy.convention.constant;

import com.jcohy.convention.JcohyVersion;

import org.springframework.boot.gradle.plugin.SpringBootPlugin;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/17:12:24
 * @since 0.0.5.1
 */
public final class BomCoordinates {

    public static final String SPRING_CLOUD_BOM_COORDINATES = "org.springframework.cloud:spring-cloud-dependencies:2021.0.4";

    public static final String SPRING_BOM_COORDINATES = SpringBootPlugin.BOM_COORDINATES;

    // https://github.com/pivotal-cf/spring-cloud-services-starters
    public static final String PIVOTAL_SPRING_CLOUD = "io.pivotal.spring.cloud:spring-cloud-services-dependencies:3.5.0";

    public static final String SPRING_BOOT_ADMIN = "de.codecentric:spring-boot-admin-dependencies:2.7.5";

    public static final String ALI_YUN_BOM_COORDINATES = "com.alibaba.cloud:aliyun-spring-boot-dependencies:1.0.0";

    public static final String ALI_CLOUD_BOM_COORDINATES = "com.alibaba.cloud:spring-cloud-alibaba-dependencies:2021.0.1.0";

    public static final String JCOHY_CHECKSTYLE = "com.jcohy.gradle:jcohy-java-checkstyle:" + JcohyVersion.getVersion();
}
