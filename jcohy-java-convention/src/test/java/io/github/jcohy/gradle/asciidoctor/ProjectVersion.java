package io.github.jcohy.gradle.asciidoctor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/8:12:22
 * @since 0.0.5.1
 */
public class ProjectVersion {

    private ProjectVersion() {
    }

    static String get() throws IOException {
        try (FileInputStream inputStream = new FileInputStream("gradle.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("VERSION");
        }
    }
}
