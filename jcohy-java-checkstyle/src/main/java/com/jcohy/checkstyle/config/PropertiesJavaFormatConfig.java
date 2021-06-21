package com.jcohy.checkstyle.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: {@link JavaFormatConfig} 由属性文件支持.
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:16:37
 * @since 1.0.0
 */
public class PropertiesJavaFormatConfig implements JavaFormatConfig {

	private final Properties properties;

	PropertiesJavaFormatConfig(Properties properties) {
		this.properties = properties;
	}

	@Override
	public IndentationStyle getIndentationStyle() {
		Object value = this.properties.get("indentation-style");
		return (value != null) ? IndentationStyle.valueOf(value.toString().toUpperCase().trim())
				: DEFAULT.getIndentationStyle();
	}

	static JavaFormatConfig load(File file) throws IOException {
		try (InputStream inputStream = new FileInputStream(file)) {
			return load(inputStream);
		}
	}

	static JavaFormatConfig load(InputStream inputStream) throws IOException {
		Properties properties = new Properties();
		properties.load(inputStream);
		return new PropertiesJavaFormatConfig(properties);
	}
}
