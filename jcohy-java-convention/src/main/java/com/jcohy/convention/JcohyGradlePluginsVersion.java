package com.jcohy.convention;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.springframework.boot.gradle.plugin.SpringBootPlugin;
import org.springframework.boot.gradle.util.VersionExtractor;

/**
 * <p>
 * Description: 获取此 jar 包版本号.
 *
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *

 *
 * @author jiac
 * @version 0.0.5.1 2021/7/2:18:15
 * @since 0.0.5.1
 */
public class JcohyGradlePluginsVersion {
    private JcohyGradlePluginsVersion() {
    }

    public static String getVersion() {
        return determineVersion();
    }

    private static String determineVersion() {
        String implementationVersion = JcohyGradlePluginsVersion.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        }
        CodeSource codeSource = JcohyGradlePluginsVersion.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return null;
        }
        URL codeSourceLocation = codeSource.getLocation();
        try {
            URLConnection connection = codeSourceLocation.openConnection();
            if (connection instanceof JarURLConnection) {
                return getImplementationVersion(((JarURLConnection) connection).getJarFile());
            }
            try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
                return getImplementationVersion(jarFile);
            }
        }
        catch (Exception ex) {
            return "";
        }
    }

    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }

    public static String getSpringBootVersion() {
        return VersionExtractor.forClass(SpringBootPlugin.class);
    }
}
