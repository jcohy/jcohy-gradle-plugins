package io.github.jcohy.gradle.build;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 * </p>
 *
 * @author jiac
 * @version 2022.04.0 2023/3/21:16:54
 * @since 2022.04.0
 */
public class Utils {


    public static String getProperties(Project project, String key) {
        return getProperties(project, key, "");
    }

    public static String getProperties(Project project, String key, String defaultValue) {
        var ref = new Object() {
            String value = defaultValue;
        };

        if (StringUtils.isNotEmpty(System.getenv(key))) {
            ref.value = System.getenv(key);
        }

        project.afterEvaluate(project1 -> {
            Object o = project1.getExtensions().getByType(ExtraPropertiesExtension.class).getProperties()
                    .get(key);
            ref.value = (String) o;
        });

        if (project.hasProperty(key)) {
            ref.value = (String) project.findProperty(key);
        }
        return ref.value;
    }
}
