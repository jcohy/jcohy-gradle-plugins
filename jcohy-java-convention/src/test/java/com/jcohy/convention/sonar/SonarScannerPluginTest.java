package com.jcohy.convention.sonar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/7/1:18:48
 * @since 1.0.0
 */
class SonarScannerPluginTest {

    private File projectDir;

    private File buildFile;

    @BeforeEach
    void setup(@TempDir File projectDir) throws IOException {
        this.projectDir = projectDir;
        this.buildFile = new File(this.projectDir, "build.gradle");
        File settingsFile = new File(this.projectDir, "settings.gradle");
        // 创建根目录 settings.gradle 并填充内容
        try (PrintWriter out = new PrintWriter(new FileWriter(settingsFile))) {
            out.println("include ':jcohy-project'");
        }

        File jcohyParentProject = new File(this.projectDir, "jcohy-project/build.gradle");
        jcohyParentProject.getParentFile().mkdirs();
        try (PrintWriter out = new PrintWriter(new FileWriter(jcohyParentProject))) {
            out.println("plugins {");
            out.println("    id 'java-platform'");
            out.println("}");
        }
    }


}