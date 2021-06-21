package com.jcohy.checkstyle;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import com.jcohy.checkstyle.check.SpringHeaderCheck;
import com.jcohy.checkstyle.check.SpringImportOrderCheck;
import com.puppycrawl.tools.checkstyle.DefaultContext;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.PropertyResolver;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.FileSetCheck;
import org.assertj.core.extractor.Extractors;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:  {@link SpringConfigurationLoader}.
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:17:36
 * @since 1.0.0
 */
public class SpringConfigurationLoaderTests {

	@Test
	public void loadShouldLoadChecks() {
		Collection<FileSetCheck> checks = load(null);
		assertThat(checks).hasSize(3);
		TreeWalker treeWalker = (TreeWalker) checks.toArray()[2];
		Set<?> ordinaryChecks = (Set<?>) Extractors.byName("ordinaryChecks").extract(treeWalker);
		assertThat(ordinaryChecks).hasSize(60);
	}

	@Test
	public void loadWithExcludeShouldExcludeChecks() {
		Set<String> excludes = Collections
				.singleton("com.puppycrawl.tools.checkstyle.checks.whitespace.MethodParamPadCheck");
		Collection<FileSetCheck> checks = load(excludes);
		assertThat(checks).hasSize(3);
		TreeWalker treeWalker = (TreeWalker) checks.toArray()[2];
		Set<?> ordinaryChecks = (Set<?>) Extractors.byName("ordinaryChecks").extract(treeWalker);
		assertThat(ordinaryChecks).hasSize(59);
	}

	private Collection<FileSetCheck> load(Set<String> excludes) {
		DefaultContext context = new DefaultContext();
		FilteredModuleFactory filteredModuleFactory = new FilteredModuleFactory(
				new PackageObjectFactory(getClass().getPackage().getName(), getClass().getClassLoader()), excludes);
		context.add("moduleFactory", filteredModuleFactory);
		Collection<FileSetCheck> checks = new SpringConfigurationLoader(context, filteredModuleFactory)
				.load(getPropertyResolver());
		return checks;
	}

	@Test
	public void loadWithExcludeHeaderShouldExcludeChecks() {
		Set<String> excludes = Collections.singleton("com.jcohy.checkstyle.check.SpringHeaderCheck");
		Object[] checks = load(excludes).stream().toArray();
		assertThat(checks).hasSize(2);
	}

	private PropertyResolver getPropertyResolver() {
		Properties properties = new Properties();
		properties.put("headerType", SpringHeaderCheck.DEFAULT_HEADER_TYPE);
		properties.put("headerFile", "");
		properties.put("headerCopyrightPattern", SpringHeaderCheck.DEFAULT_HEADER_COPYRIGHT_PATTERN);
		properties.put("projectRootPackage", SpringImportOrderCheck.DEFAULT_PROJECT_ROOT_PACKAGE);
		properties.put("avoidStaticImportExcludes", "");
		return new PropertiesExpander(properties);
	}
}
