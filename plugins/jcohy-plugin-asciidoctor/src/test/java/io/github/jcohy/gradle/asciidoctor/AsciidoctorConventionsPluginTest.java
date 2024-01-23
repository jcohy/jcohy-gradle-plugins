package io.github.jcohy.gradle.asciidoctor;

import java.util.Map;

import org.asciidoctor.gradle.jvm.AsciidoctorJPlugin;
import org.asciidoctor.gradle.jvm.AsciidoctorTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/10/13:16:38
 * @since 2022.0.1
 */
@SuppressWarnings("unchecked")
public class AsciidoctorConventionsPluginTest {

    @Test
    void asciidoctorWithAsciidoctorExtensionsConfigurationSuccess() {
        Project project = projectWithPlugins("jcohy-asciidoctor");
        Configuration configuration = project.getConfigurations().getByName(JcohyAsciidoctorPlugin.EXTENSIONS_CONFIGURATION_NAME);
        DependencySet dependencies = configuration.getDependencies();
        assertThat(dependencies.size()).isEqualTo(2);
        assertThat(dependencies).extracting(Dependency::getName).contains("spring-asciidoctor-backends");
        assertThat(dependencies).extracting(Dependency::getName).contains("asciidoctorj-pdf");
    }

    @Test
    void asciidoctorWithAttributesSuccess() {
        Project project = projectWithPlugins("jcohy-asciidoctor");
        AsciidoctorTask asciidoctorTask = (AsciidoctorTask) project.getTasks().findByPath(":asciidoctor");
        assertThat(asciidoctorTask.getOptions().get("doctype")).isEqualTo("book");
        assertThat(asciidoctorTask.getLogDocuments()).isTrue();
        Map<String,Object> attributes = asciidoctorTask.getAttributes();
        assertThat(attributes.get("idprefix")).isEqualTo("");
        assertThat(attributes.get("idseparator")).isEqualTo("-");
        assertThat(attributes.get("toc")).isEqualTo("left");
        assertThat(attributes.get("toclevels")).isEqualTo("4");
        assertThat(attributes.get("tabsize")).isEqualTo("4");
        assertThat(attributes.get("numbered")).isEqualTo("");
        assertThat(attributes.get("source-indent")).isEqualTo("0");
        assertThat(attributes.get("sectanchors")).isEqualTo("");
        assertThat(attributes.get("icons")).isEqualTo("font");
        assertThat(attributes.get("hide-uri-scheme")).isEqualTo("font");
        assertThat((boolean)attributes.get("allow-uri-read")).isTrue();
        assertThat(attributes.get("version")).isEqualTo("1.0");
        assertThat(attributes.get("revnumber")).isEqualTo("1.0");
        assertThat(attributes.get("docinfo")).isEqualTo("shared,private");
        assertThat(attributes.get("attribute-missing")).isNull();
        assertThat(attributes.get("docs-url")).isEqualTo("https://docs.jcohy.com");
        assertThat(attributes.get("resource-url")).isEqualTo("https://resources.jcohy.com");
        assertThat(attributes.get("software-url")).isEqualTo("https://software.jcohy.com");
        assertThat(attributes.get("study-url")).isEqualTo("https://study.jcohy.com");
        assertThat(attributes.get("project-url")).isEqualTo("https://project.jcohy.com");
    }

    private Project projectWithPlugins(String name) {
        Project project = ProjectBuilder.builder()
                .withName(name)
                .build();
        project.setVersion("1.0");
        project.getPlugins().apply(AsciidoctorJPlugin.class);
        project.getPlugins().apply(JcohyAsciidoctorPlugin.class);
        return project;
    }
}
