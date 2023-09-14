package io.github.jcohy.gradle.asciidoctor;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Sync;

import java.io.File;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/9/14:09:41
 * @since 2023.0.1
 */
public class AsciidoctorAggregatePlugins implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(AsciidoctorConventionsPlugin.class);

        AsciidoctorAggregateExtension extension = project.getExtensions().create(AsciidoctorAggregateExtension.AGGREGATED_ASCIIDOCTOR_EXTENSION_NAME,
                AsciidoctorAggregateExtension.class, project);
        Sync aggregatedAsciidoctor = project.getTasks().create("aggregatedAsciidoctor", Sync.class, extension);
        configurationAggregatedAsciidoctorTask(project, aggregatedAsciidoctor);
    }


    private void configurationAggregatedAsciidoctorTask(Project p, Sync aggregatedProject) {
        p.afterEvaluate(project -> {
            aggregatedProject.setGroup("documentation");
            AsciidoctorAggregateExtension extension = project.getExtensions().getByType(AsciidoctorAggregateExtension.class);
            String name = project.getName();
            if (!extension.getExcludeProject().contains(name)) {
                File syncSource = new File(project.getRootProject().getBuildDir(), "reference/");

                aggregatedProject.setDestinationDir(syncSource);

                if (project.getTasks().getNames().contains("asciidoctor")) {
                    if (!extension.getExcludeSingle().contains(name)) {
                        Task asciidoctor = project.getTasks().getByName("asciidoctor");
                        aggregatedProject.dependsOn(asciidoctor);
                        aggregatedProject.from(asciidoctor.getOutputs(), spec -> {
                            spec.into(project.getName() + "/" + project.getVersion() + "/htmlsingle");
                        });
                    }
                }

                if (project.getTasks().getNames().contains("asciidoctorPdf")) {
                    if (!extension.getExcludePdf().contains(name)) {
                        Task asciidoctorPdf = project.getTasks().getByName("asciidoctorPdf");
                        aggregatedProject.dependsOn(asciidoctorPdf);
                        aggregatedProject.from(asciidoctorPdf.getOutputs(), spec -> {
                            spec.into(project.getName() + "/" + project.getVersion() + "/pdf");
                        });
                    }
                }

                if (project.getTasks().getNames().contains("asciidoctorMultiPage")) {
                    if (!extension.getExcludeMulti().contains(name)) {
                        Task multiPage = project.getTasks().getByName("asciidoctorMultiPage");
                        aggregatedProject.dependsOn(multiPage);
                        aggregatedProject.from(multiPage.getOutputs(), spec -> {
                            spec.into(project.getName() + "/" + project.getVersion() + "/html5");
                        });
                    }
                }
            }
        });
    }
}
