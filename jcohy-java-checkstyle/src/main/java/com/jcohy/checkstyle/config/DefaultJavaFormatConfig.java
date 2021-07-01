package com.jcohy.checkstyle.config;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 找不到配置文件时使用的默认 {@link JavaFormatConfig} 实现。
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:16:36
 * @since 1.0.0
 */
public class DefaultJavaFormatConfig implements JavaFormatConfig {
    
    private final IndentationStyle indentationStyle;
    
    DefaultJavaFormatConfig(IndentationStyle indentationStyle) {
        this.indentationStyle = indentationStyle;
    }
    
    @Override
    public IndentationStyle getIndentationStyle() {
        return this.indentationStyle;
    }
}
