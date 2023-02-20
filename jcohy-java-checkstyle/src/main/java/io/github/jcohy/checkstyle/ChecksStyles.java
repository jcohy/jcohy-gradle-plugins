package io.github.jcohy.checkstyle;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/30:12:54
 * @since 0.0.5.1
 */
public enum ChecksStyles {
    DEFAULT("jcohy", "jcohy-checkstyle.xml"),
    GOOGLE("google", "google-checks.xml"),
    ALIBABA("alibaba", "alibaba-checks.xml"),
    SPRING("spring", "spring-checks.xml"),
    SUN("sun", "sun-checks.xml");

    private String name;

    private String filePath;

    ChecksStyles(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public static String getFilePath(String name) {
        for (ChecksStyles checksStyles : ChecksStyles.values()) {
            if (checksStyles.name.equals(name)) {
                return checksStyles.getFilePath();
            }
        }
        return DEFAULT.getFilePath();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
