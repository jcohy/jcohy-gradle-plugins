package io.github.jcohy.checkstyle;

import java.util.Set;

import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.TreeWalkerFilter;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:15:03
 * @since 0.0.5.1
 */
public class FilteredModuleFactory implements ModuleFactory {

    static final TreeWalkerFilter FILTERED = treeWalkerAuditEvent -> true;

    private final ModuleFactory moduleFactory;

    private final Set<String> excludes;

    public FilteredModuleFactory(ModuleFactory moduleFactory, Set<String> excludes) {
        this.moduleFactory = moduleFactory;
        this.excludes = excludes;
    }

    @Override
    public Object createModule(String name) throws CheckstyleException {

        Object module = this.moduleFactory.createModule(name);
        if (isFiltered(module)) {
            if (module instanceof AbstractCheck) {
                return FILTERED;
            }
            throw new IllegalStateException("Unable to filter module " + module.getClass().getName());
        }
        return module;
    }

    boolean nonFiltered(Configuration configuration) {
        return !isFiltered(configuration.getName());
    }

    private boolean isFiltered(Object module) {
        return isFiltered(module.getClass().getName());
    }

    private boolean isFiltered(String name) {
        return this.excludes != null && this.excludes.contains(name);
    }
}
