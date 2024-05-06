package io.github.jcohy.gradle.asciidoctor;

import org.gradle.api.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright: Copyright (c) 2023 <a href="https://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 2023.0.1 2023/9/14:09:44
 * @since 2023.0.1
 */
public class AsciidoctorAggregateExtension {

    public static final String AGGREGATED_ASCIIDOCTOR_EXTENSION_NAME = "aggregatedAsciidoctorExtension";
    private final Project project;

    private List<String> excludePdf = new ArrayList<>();

    private List<String> excludeProject = new ArrayList<>();

    private List<String> excludeSingle = new ArrayList<>();

    private List<String> excludeMulti = new ArrayList<>();

    public AsciidoctorAggregateExtension(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public List<String> getExcludePdf() {
        return excludePdf;
    }

    public AsciidoctorAggregateExtension setExcludePdf(List<String> excludePdf) {
        this.excludePdf = excludePdf;
        return this;
    }

    public List<String> getExcludeProject() {
        return excludeProject;
    }

    public AsciidoctorAggregateExtension setExcludeProject(List<String> excludeProject) {
        this.excludeProject = excludeProject;
        return this;
    }

    public List<String> getExcludeSingle() {
        return excludeSingle;
    }

    public AsciidoctorAggregateExtension setExcludeSingle(List<String> excludeSingle) {
        this.excludeSingle = excludeSingle;
        return this;
    }

    public List<String> getExcludeMulti() {
        return excludeMulti;
    }

    public AsciidoctorAggregateExtension setExcludeMulti(List<String> excludeMulti) {
        this.excludeMulti = excludeMulti;
        return this;
    }
}
