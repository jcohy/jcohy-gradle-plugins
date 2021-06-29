package com.jcohy.checkstyle.check;

import com.puppycrawl.tools.checkstyle.checks.imports.ImportOrderCheck;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查导入顺序是否遵循 Spring 约定.
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:12:54
 * @since 1.0.0
 */
public class SpringImportOrderCheck extends ImportOrderCheck {

	/**
	 * The default root package.
	 */
	public static final String DEFAULT_PROJECT_ROOT_PACKAGE = "com.jcohy";

	private boolean ordered = true;

	public SpringImportOrderCheck() {
		setProjectRootPackage(DEFAULT_PROJECT_ROOT_PACKAGE);
		setOrdered(ordered);
		setSeparated(true);
		setOption("bottom");
		setSortStaticImportsAlphabetically(true);
	}

	public void setProjectRootPackage(String projectRootPackage) {
		setGroups("java", "/^javax?\\./", "*", projectRootPackage);
	}

	@Override
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}
}
