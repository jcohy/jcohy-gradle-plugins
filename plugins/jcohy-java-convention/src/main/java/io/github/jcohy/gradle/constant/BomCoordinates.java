package io.github.jcohy.gradle.constant;

import io.github.jcohy.gradle.JcohyVersion;

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

	/**
	 * Spring Boot Bom
	 */
	public static final String SPRING_BOM_COORDINATES = SpringBootPlugin.BOM_COORDINATES;

	/**
	 * Spring Cloud Bom
	 */
    public static final String SPRING_CLOUD_BOM_COORDINATES = "org.springframework.cloud:spring-cloud-dependencies:2023.0.0";

	/**
	 * <a href="https://github.com/alibaba/spring-cloud-alibaba">...</a>
	 * todo 更新
	 */
	public static final String ALI_CLOUD_BOM_COORDINATES = "com.alibaba.cloud:spring-cloud-alibaba-dependencies:2022.0.0.0";

	/**
	 * <a href="https://github.com/Tencent/spring-cloud-tencent/wiki/Spring-Cloud-Tencent-">版本管理#2022-系列 </a>
	 * todo 更新
	 */
	public static final String TENCENT_CLOUD_BOM_COORDINATES = "com.tencent.cloud:spring-cloud-tencent-dependencies:1.12.4-2022.0.4";

	/**
	 * <a href="https://github.com/pivotal-cf/spring-cloud-services-starters/releases">Spring Cloud Services in a Pivotal Cloud Foundry environment.</a>。
	 */
	public static final String PIVOTAL_SPRING_CLOUD = "io.pivotal.spring.cloud:spring-cloud-services-dependencies:4.1.0";

	/**
	 * <a href="https://github.com/codecentric/spring-boot-admin">...</a>
	 */
    public static final String SPRING_BOOT_ADMIN = "de.codecentric:spring-boot-admin-dependencies:3.2.0";

	/**
	 * <a href=" https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html#appendix.dependency-versions">...</a>
	 */
	public static final String SPRING_SECURITY_BOM = "org.springframework.security:spring-security-bom:6.2.1";

	/**
	 * <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/dependency-versions.html#appendix.dependency-versions">...</a>
	 */
	public static final String TESTCONTAINERS_BOM ="org.testcontainers:testcontainers-bom:1.19.3";
    public static final String JCOHY_CHECKSTYLE = "io.github.jcohy.gradle:jcohy-java-checkstyle:" + JcohyVersion.getVersion();

}
