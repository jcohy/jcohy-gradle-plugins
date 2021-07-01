package com.jcohy.convention.constant;

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
public interface BomCoordinates {
    String SpringBomCoordinates = SpringBootPlugin.BOM_COORDINATES;
    
    String AliYunBomCoordinates = "com.alibaba.cloud:aliyun-spring-boot-dependencies:1.0.0";
    
    String AliCloudBomCoordinates = "com.alibaba.cloud:spring-cloud-alibaba-dependencies:2.2.2.RELEASE";
}
