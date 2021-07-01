package com.jcohy.convention.conventions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import org.springframework.util.FileCopyUtils;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: {@link Task} 从 classpath 中获取资源并写入磁盘中
 *
 * @author jiac
 * @version 1.0.0 2021/6/11:16:25
 * @since 1.0.0
 */
public class ExtractResources extends DefaultTask {
    
    private final PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper("${", "}");
    
    private final Map<String, String> properties = new HashMap<>();
    
    private final DirectoryProperty destinationDirectory;
    
    private List<String> resourceNames = new ArrayList<>();
    
    public ExtractResources() {
        this.destinationDirectory = getProject().getObjects().directoryProperty();
    }
    
    @Input
    public List<String> getResourceNames() {
        return this.resourceNames;
    }
    
    public void setResourcesNames(List<String> resourceNames) {
        this.resourceNames = resourceNames;
    }
    
    @OutputDirectory
    public DirectoryProperty getDestinationDirectory() {
        return this.destinationDirectory;
    }
    
    public void property(String name, String value) {
        this.properties.put(name, value);
    }
    
    @Input
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    @TaskAction
    void extractResources() throws IOException {
        for (String resourceName : this.resourceNames) {
            InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (resourceStream == null) {
                throw new GradleException("Resource '" + resourceName + "' does not exist");
            }
            String resource = FileCopyUtils.copyToString(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
            resource = this.propertyPlaceholderHelper.replacePlaceholders(resource,
                    (placeholder) -> this.properties.get(placeholder));
            FileCopyUtils.copy(resource,
                    new FileWriter(this.destinationDirectory.file(resourceName).get().getAsFile()));
        }
    }
}
