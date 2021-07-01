package com.jcohy.checkstyle;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/6/30:12:54
 * @since 1.0.0
 */
public enum ChecksStyle {
    DEFAULT("jcohy", "jcohy_checkstyle.xml"),
    GOOGLE("google", "google_checks.xml"),
    ALIBABA("alibaba", "alibaba_checks.xml"),
    SPRING("spring", "spring_checks.xml"),
    SUN("sun", "sun_checks.xml");
    
    private String label;
    
    private String path;
    
    ChecksStyle(String label, String path) {
        this.label = label;
        this.path = path;
    }
    
    public static String getPath(String label) {
        for (ChecksStyle checksStyle : ChecksStyle.values()) {
            if (checksStyle.label.equals(label)) {
                return checksStyle.getPath();
            }
        }
        return DEFAULT.getPath();
    }
    
    public String getLabel() {
        return label;
    }
    
    public ChecksStyle setLabel(String label) {
        this.label = label;
        return this;
    }
    
    public String getPath() {
        return path;
    }
    
    public ChecksStyle setPath(String path) {
        this.path = path;
        return this;
    }
}
