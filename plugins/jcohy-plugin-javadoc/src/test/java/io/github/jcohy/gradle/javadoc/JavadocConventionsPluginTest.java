package io.github.jcohy.gradle.javadoc;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.JavadocOutputLevel;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:15:20
 * @since 2022.0.1
 */
class JavadocConventionsPluginTest {

    @Test
    void options() {
        Project project = projectWithPlugins("jcohy-javadoc-build");
        Javadoc javadoc = (Javadoc) project.getTasks().findByPath(":javadoc");
        StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) javadoc.getOptions();
        assertThat(options.isAuthor()).isTrue();
        assertThat(options.getDocTitle()).isEqualTo("Jcohy Javadoc API");
        assertThat(options.getEncoding()).isEqualTo(StandardCharsets.UTF_8.name());
        assertThat(options.getMemberLevel()).isEqualTo(JavadocMemberLevel.PROTECTED);
        assertThat(options.getOutputLevel()).isEqualTo(JavadocOutputLevel.QUIET);
        assertThat(options.isSplitIndex()).isTrue();
        File stylesheetFile = project.file(JavadocConventionsPlugin.STYLESHEET_FILE_NAME);
        assertThat(options.getStylesheetFile()).isEqualTo(stylesheetFile);
        assertThat(options.isUse()).isTrue();
        assertThat(options.getWindowTitle()).isEqualTo("Jcohy Javadoc API");
    }

    private Project projectWithPlugins(String name) {
        Project project = ProjectBuilder.builder().withName(name).build();
        PluginContainer plugins = project.getPlugins();
        plugins.apply(JavaPlugin.class);
        plugins.apply(JavadocPlugin.class);
        plugins.apply(JavadocConventionsPlugin.class);
        return project;
    }
}