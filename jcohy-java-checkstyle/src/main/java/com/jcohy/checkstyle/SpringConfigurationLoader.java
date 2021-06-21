package com.jcohy.checkstyle;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader.IgnoredModulesOptions;
import com.puppycrawl.tools.checkstyle.PropertyResolver;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.Context;
import com.puppycrawl.tools.checkstyle.api.FileSetCheck;
import org.xml.sax.InputSource;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 从  {@code spring-checkstyle.xml} 加载 {@link FileSetCheck FileSetChecks} 配置
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:15:08
 * @since 1.0.0
 */
public class SpringConfigurationLoader {

	private final Context context;

	private final FilteredModuleFactory moduleFactory;

	public SpringConfigurationLoader(Context context, FilteredModuleFactory moduleFactory) {
		this.context = context;
		this.moduleFactory = moduleFactory;
	}

	public Collection<FileSetCheck> load(PropertyResolver propertyResolver) {
		Class<? extends SpringConfigurationLoader> aClass = getClass();
		InputStream resourceAsStream = getClass().getResourceAsStream("jcohy-checkstyle.xml");
		Configuration config = loadConfiguration(getClass().getResourceAsStream("jcohy-checkstyle.xml"),
				propertyResolver);
		return Arrays.stream(config.getChildren()).filter(this.moduleFactory::nonFiltered).map(this::load)
				.collect(Collectors.toList());
	}

	private Configuration loadConfiguration(InputStream inputStream, PropertyResolver propertyResolver) {
		try {
			InputSource inputSource = new InputSource(inputStream);
			return ConfigurationLoader.loadConfiguration(inputSource, propertyResolver, IgnoredModulesOptions.EXECUTE);
		}
		catch (CheckstyleException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private FileSetCheck load(Configuration configuration) {
		Object module = createModule(configuration);
		if (!(module instanceof FileSetCheck)) {
			throw new IllegalStateException(configuration.getName() + " is not allowed");
		}
		return (FileSetCheck) module;
	}

	private Object createModule(Configuration configuration) {
		String name = configuration.getName();
		try {
			Object module = this.moduleFactory.createModule(name);
			if (module instanceof AutomaticBean) {
				initialize(configuration, (AutomaticBean) module);
			}
			return module;
		}
		catch (CheckstyleException ex) {
			throw new IllegalStateException("cannot initialize module " + name + " - " + ex.getMessage(), ex);
		}
	}

	private void initialize(Configuration configuration, AutomaticBean bean) throws CheckstyleException {
		bean.contextualize(this.context);
		bean.configure(configuration);
	}
}
