/*
 * Copyright 2012-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jcohy.gradle.antora;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.gradle.api.Project;


/**
 * Generates Asciidoctor attributes for use with Antora.
 *
 * @author Phillip Webb
 */
public class AntoraAsciidocAttributes {

	private final String version;

	private final Project project;

	public AntoraAsciidocAttributes(Project project) {
		this.version = String.valueOf(project.getVersion());
		this.project = project;
	}

	public Map<String, String> get() {
		Map<String, String> attributes = new LinkedHashMap<>();
		addProjectAttributes(attributes,this.project);
		addPropertyAttributes(attributes);
		return attributes;
	}

	private void addProjectAttributes(Map<String, String> attributes,Project project) {
		attributes.put("version",(String) project.getVersion());
		attributes.put("image-resource", project.getBuildDir() + "/docs/src/antora/images");
		attributes.put("docs-java", project.getProjectDir() + "/src/main/java");
		attributes.put("docs-kotlin", project.getProjectDir() + "/src/main/kotlin");
		attributes.put("docs-groovy", project.getProjectDir() + "/src/main/groovy");
		attributes.put("docs-url", "https://docs.jcohy.com");
		attributes.put("resource-url", "https://resources.jcohy.com");
		attributes.put("software-url", "https://software.jcohy.com");
		attributes.put("study-url", "https://study.jcohy.com");
		attributes.put("project-url", "https://project.jcohy.com");
	}

	private void addPropertyAttributes(Map<String, String> attributes) {
		Properties properties = new Properties() {

			@Override
			public synchronized Object put(Object key, Object value) {
				// Put directly because order is important for us
				return attributes.put(key.toString(), value.toString());
			}

		};
		try (InputStream in = getClass().getResourceAsStream("antora-asciidoc-attributes.properties")) {
			properties.load(in);
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

}
