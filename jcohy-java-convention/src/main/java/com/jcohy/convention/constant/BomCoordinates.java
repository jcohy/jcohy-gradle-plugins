package com.jcohy.convention.constant;

import com.jcohy.convention.JcohyVersion;

import org.springframework.boot.gradle.plugin.SpringBootPlugin;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/6/17:12:24
 * @since 1.0.0
 */
public final class BomCoordinates {

    public static final String SpringBomCoordinates = SpringBootPlugin.BOM_COORDINATES;

    public static final String AliYunBomCoordinates = "com.alibaba.cloud:aliyun-spring-boot-dependencies:1.0.0";

    public static final String FlightBomCoordinates = "com.jcohy.gradle:jcohy-framework-bom:" + JcohyVersion.getVersion();

    public static final String AliCloudBomCoordinates = "com.alibaba.cloud:spring-cloud-alibaba-dependencies:2.2.2.RELEASE";
}
