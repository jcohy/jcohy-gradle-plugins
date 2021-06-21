package com.jcohy.checkstyle;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.jcohy.checkstyle.check.SpringHeaderCheck;
import com.jcohy.checkstyle.check.SpringImportOrderCheck;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultContext;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.ExternalResourceHolder;
import com.puppycrawl.tools.checkstyle.api.FileSetCheck;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;
import com.puppycrawl.tools.checkstyle.filters.SuppressFilterElement;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: {@link FileSetCheck} that applies Spring checkstype rules.
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:10:50
 * @since 1.0.0
 */
public class SpringChecks extends AbstractFileSetCheck implements ExternalResourceHolder {

	private ClassLoader classLoader;

	private ModuleFactory moduleFactory;

	private Collection<FileSetCheck> checks;

	private String headerType = SpringHeaderCheck.DEFAULT_HEADER_TYPE;

	private String headerCopyrightPattern = SpringHeaderCheck.DEFAULT_HEADER_COPYRIGHT_PATTERN;

	private String headerFile;

	private Set<String> avoidStaticImportExcludes = Collections.emptySet();

	private String projectRootPackage = SpringImportOrderCheck.DEFAULT_PROJECT_ROOT_PACKAGE;

	private Set<String> excludes;

	/**
	 * Sets classLoader to load class.
	 * @param classLoader class loader to resolve classes with.
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	/**
	 * Sets the module factory for creating child modules (Checks).
	 * @param moduleFactory the factory
	 */
	public void setModuleFactory(ModuleFactory moduleFactory) {
		this.moduleFactory = moduleFactory;
	}

	@Override
	protected void finishLocalSetup() throws CheckstyleException {
		FilteredModuleFactory moduleFactory = new FilteredModuleFactory(this.moduleFactory, this.excludes);
		DefaultContext context = new DefaultContext();
		context.add("classLoader", this.classLoader);
		context.add("severity", getSeverity());
		context.add("tabWidth", String.valueOf(getTabWidth()));
		context.add("moduleFactory", moduleFactory);
		Properties properties = new Properties();
		put(properties, "headerType", this.headerType);
		put(properties, "headerCopyrightPattern", this.headerCopyrightPattern);
		put(properties, "headerFile", this.headerFile);
		put(properties, "projectRootPackage", this.projectRootPackage);
		put(properties, "avoidStaticImportExcludes",
				this.avoidStaticImportExcludes.stream().collect(Collectors.joining(",")));
		this.checks = new SpringConfigurationLoader(context, moduleFactory).load(new PropertiesExpander(properties));
	}

	private void put(Properties properties, String name, Object value) {
		if (value != null) {
			properties.put(name, value);
		}
	}

	@Override
	protected void processFiltered(File file, FileText fileText) throws CheckstyleException {
		SortedSet<LocalizedMessage> messages = new TreeSet<>();
		for (FileSetCheck check : this.checks) {
			messages.addAll(check.process(file, fileText));
		}
		addMessages(messages);
	}

	@Override
	public Set<String> getExternalResourceLocations() {
		Set<String> locations = new LinkedHashSet<>();
		for (FileSetCheck check : this.checks) {
			if (check instanceof ExternalResourceHolder) {
				locations.addAll(((ExternalResourceHolder) check).getExternalResourceLocations());
			}
		}
		return locations;
	}

	@Override
	public void beginProcessing(String charset) {
		super.beginProcessing(charset);
		try {
			SuppressFilterElement filter = new SuppressFilterElement("[\\\\/]src[\\\\/]test[\\\\/]java[\\\\/]",
					"Javadoc*", null, null, null, null);
			((Checker) getMessageDispatcher()).addFilter(filter);
		}
		catch (Exception ex) {
			// Ignore and let users configure their own suppressions
		}
	}

	@Override
	public void setupChild(Configuration configuration) throws CheckstyleException {
		throw new CheckstyleException("SpringChecks is not allowed as a parent of " + configuration.getName());
	}

	public void setHeaderType(String headerType) {
		this.headerType = headerType;
	}

	public void setHeaderCopyrightPattern(String headerCopyrightPattern) {
		this.headerCopyrightPattern = headerCopyrightPattern;
	}

	public void setHeaderFile(String headerFile) {
		this.headerFile = headerFile;
	}

	public void setAvoidStaticImportExcludes(String[] avoidStaticImportExcludes) {
		this.avoidStaticImportExcludes = new LinkedHashSet<>(Arrays.asList(avoidStaticImportExcludes));
	}

	public void setProjectRootPackage(String projectRootPackage) {
		this.projectRootPackage = projectRootPackage;
	}

	public void setExcludes(String... excludes) {
		this.excludes = new HashSet<>(Arrays.asList(excludes));
	}

}
