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

	// Spring Boot Bom
	public static final String SPRING_BOM_COORDINATES = SpringBootPlugin.BOM_COORDINATES;
	// Spring Cloud Bom
    public static final String SPRING_CLOUD_BOM_COORDINATES = "org.springframework.cloud:spring-cloud-dependencies:2022.0.0";

	// https://github.com/pivotal-cf/spring-cloud-services-starters/releases。Spring Cloud Services in a Pivotal Cloud Foundry environment.
	public static final String PIVOTAL_SPRING_CLOUD = "io.pivotal.spring.cloud:spring-cloud-services-dependencies:4.0.0";

	// https://github.com/codecentric/spring-boot-admin
    public static final String SPRING_BOOT_ADMIN = "de.codecentric:spring-boot-admin-dependencies:3.0.0";

	// 删除
//  public static final String ALI_YUN_BOM_COORDINATES = "com.alibaba.cloud:aliyun-spring-boot-dependencies:1.0.0";

	// https://github.com/alibaba/spring-cloud-alibaba
    public static final String ALI_CLOUD_BOM_COORDINATES = "com.alibaba.cloud:spring-cloud-alibaba-dependencies:2022.0.0.0-RC1";

    public static final String JCOHY_CHECKSTYLE = "com.jcohy.gradle:jcohy-java-checkstyle:" + JcohyVersion.getVersion();
}
