package io.github.jcohy.gradle.maven.constant;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/11:17:24
 * @since 0.0.5.1
 */
public final class PomConstant {

    public static final String NEXUS_USER_NAME = System.getenv("nexus_user_name");

    public static final String NEXUS_PASSWORD = System.getenv("nexus_password");

    public static final String GIT_URL = "https://github.com/jcohy/jcohy-gradle-plugins.git";

    public static final String POM_ORGANIZATION_NAME = "jcohy";

    public static final String POM_ORGANIZATION_URL = "https://github.com/jcohy";

    public static final String GIT_SCM_CONNECTION = "scm:git:git://github.com/jcohy/jcohy-gradle-plugins";

    public static final String GIT_SCM_DEVELOPER_CONNECTION = "scm:git:git://github.com/jcohy/jiachao23";

    public static final String LICENSE_NAME = "Apache License, Version 2.0";

    public static final String LICENSE_URL = "https://www.apache.org/licenses/LICENSE-2.0";

    public static final String DEVELOPER_NAME = "jiachao23";

    public static final String DEVELOPER_EMAIL = "jia_chao23@126.com";

    public static final String ISSUE_SYSTEM = "GitHub";

    public static final String ISSUE_URL = "https://github.com/jcohy/jcohy-gradle-plugins/issues";
}
