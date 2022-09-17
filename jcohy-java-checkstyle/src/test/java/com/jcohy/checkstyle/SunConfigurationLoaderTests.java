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
 * <p>
 * Description:  {@link JcohyConfigurationLoader}.
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:17:36
 * @since 0.0.5.1
 */
public class SunConfigurationLoaderTests {

    private String checkStyleFile = ChecksStyles.getFilePath("sun");

    @Test
    public void loadSunShouldLoadChecks() {
        checkStyleFile = ChecksStyles.getFilePath("sun");
        Collection<FileSetCheck> checks = load(null);
        assertThat(checks).hasSize(8);
        TreeWalker treeWalker = (TreeWalker) checks.toArray()[7];
        Set<?> ordinaryChecks = (Set<?>) Extractors.byName("ordinaryChecks").extract(treeWalker);
        assertThat(ordinaryChecks).hasSize(54);
    }

    @Test
    public void loadWithExcludeShouldExcludeChecks() {
        Set<String> excludes = Collections
                .singleton("com.puppycrawl.tools.checkstyle.checks.blocks.EmptyBlockCheck");
        Collection<FileSetCheck> checks = load(excludes);
        assertThat(checks).hasSize(8);
        TreeWalker treeWalker = (TreeWalker) checks.toArray()[7];
        Set<?> ordinaryChecks = (Set<?>) Extractors.byName("ordinaryChecks").extract(treeWalker);
        assertThat(ordinaryChecks).hasSize(53);
    }

    private Collection<FileSetCheck> load(Set<String> excludes) {
        DefaultContext context = new DefaultContext();
        FilteredModuleFactory filteredModuleFactory = new FilteredModuleFactory(
                new PackageObjectFactory(getClass().getPackage().getName(), getClass().getClassLoader()), excludes);
        context.add("moduleFactory", filteredModuleFactory);

        Collection<FileSetCheck> checks = new JcohyConfigurationLoader(context, filteredModuleFactory)
                .load(getPropertyResolver(), checkStyleFile);
        return checks;
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
