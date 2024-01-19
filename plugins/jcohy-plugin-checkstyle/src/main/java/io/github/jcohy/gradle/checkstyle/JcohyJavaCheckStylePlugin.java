package io.github.jcohy.gradle.checkstyle;

import java.io.File;
import java.util.Objects;

import io.spring.javaformat.gradle.tasks.CheckFormat;
import javax.annotation.Nullable;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.quality.CheckstyleExtension;
import org.gradle.api.plugins.quality.CheckstylePlugin;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 2024.0.1 2024/1/19 15:10
 * @since 2024.0.1
 */
public class JcohyJavaCheckStylePlugin implements Plugin<Project> {

	private static final String CHECKSTYLE_DIR = "etc/checkstyle";
	private static final String SPRING_JAVAFORMAT_VERSION_PROPERTY = "javaformatVersion";
	private static final String DEFAULT_SPRING_JAVAFORMAT_VERSION = "0.0.31";
	private static final String NOHTTP_CHECKSTYLE_VERSION_PROPERTY = "nohttpCheckstyleVersion";
	private static final String DEFAULT_NOHTTP_CHECKSTYLE_VERSION = "0.0.10";
	private static final String CHECKSTYLE_TOOL_VERSION_PROPERTY = "checkstyleToolVersion";
	private static final String DEFAULT_CHECKSTYLE_TOOL_VERSION = "8.34";
	private static final String SPRING_JAVAFORMAT_EXCLUDE_PACKAGES_PROPERTY = "javaformatExcludePackages";

	@Override
	public void apply(Project project) {
		project.getPlugins().withType(JavaPlugin.class, (javaPlugin) -> {
			File checkstyleDir = project.getRootProject().file(CHECKSTYLE_DIR);
			if (checkstyleDir.exists() && checkstyleDir.isDirectory()) {
				project.getPluginManager().apply(CheckstylePlugin.class);

				// NOTE: See gradle.properties#springJavaformatVersion for actual version number
				project.getDependencies().add("checkstyle", "io.spring.javaformat:spring-javaformat-checkstyle:" + getSpringJavaFormatVersion(project));
				// NOTE: See gradle.properties#nohttpCheckstyleVersion for actual version number
				project.getDependencies().add("checkstyle", "io.spring.nohttp:nohttp-checkstyle:" + getNoHttpCheckstyleVersion(project));

				CheckstyleExtension checkstyle = project.getExtensions().getByType(CheckstyleExtension.class);
				checkstyle.getConfigDirectory().set(checkstyleDir);
				// NOTE: See gradle.properties#checkstyleToolVersion for actual version number
				checkstyle.setToolVersion(getCheckstyleToolVersion(project));
			}

			// Configure checkFormat task
			project.getTasks().withType(CheckFormat.class, (checkFormat) -> {
				// NOTE: See gradle.properties#springJavaformatExcludePackages for excluded packages
				String[] springJavaformatExcludePackages = getSpringJavaFormatExcludePackages(project);
				if (springJavaformatExcludePackages != null) {
					checkFormat.exclude(springJavaformatExcludePackages);
				}
			});
		});
	}

	/**
	 * 获取 SpringJavaFormat 默认为 0.0.31.
	 * 可以通过 javaformatVersion 属性设置
	 * @param project project
	 * @return /
	 */
	private static String getSpringJavaFormatVersion(Project project) {
		String springJavaformatVersion = DEFAULT_SPRING_JAVAFORMAT_VERSION;
		if (project.hasProperty(SPRING_JAVAFORMAT_VERSION_PROPERTY)) {
			springJavaformatVersion = Objects.requireNonNull(project.findProperty(SPRING_JAVAFORMAT_VERSION_PROPERTY)).toString();
		}
		return springJavaformatVersion;
	}

	/**
	 * 获取 NoHttpCheckstyle 默认为 0.0.10.
	 * 可以通过 nohttpCheckstyleVersion 属性设置
	 * @param project project
	 * @return /
	 */
	private static String getNoHttpCheckstyleVersion(Project project) {
		String nohttpCheckstyleVersion = DEFAULT_NOHTTP_CHECKSTYLE_VERSION;
		if (project.hasProperty(NOHTTP_CHECKSTYLE_VERSION_PROPERTY)) {
			nohttpCheckstyleVersion = Objects.requireNonNull(project.findProperty(NOHTTP_CHECKSTYLE_VERSION_PROPERTY)).toString();
		}
		return nohttpCheckstyleVersion;
	}

	/**
	 * 获取 Checkstyle 默认为 8.34.
	 * 可以通过 checkstyleToolVersion 属性设置
	 * @param project project
	 * @return /
	 */
	private static String getCheckstyleToolVersion(Project project) {
		String checkstyleToolVersion = DEFAULT_CHECKSTYLE_TOOL_VERSION;
		if (project.hasProperty(CHECKSTYLE_TOOL_VERSION_PROPERTY)) {
			checkstyleToolVersion = Objects.requireNonNull(project.findProperty(CHECKSTYLE_TOOL_VERSION_PROPERTY)).toString();
		}
		return checkstyleToolVersion;
	}

	@Nullable
	private String[] getSpringJavaFormatExcludePackages(Project project) {
		String springJavaformatExcludePackages = (String) project.findProperty(SPRING_JAVAFORMAT_EXCLUDE_PACKAGES_PROPERTY);
		return (springJavaformatExcludePackages != null) ? springJavaformatExcludePackages.split(" ") : null;
	}
}
