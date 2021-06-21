package com.jcohy.checkstyle.check;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.design.HideUtilityClassConstructorCheck;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 扩展  {@link HideUtilityClassConstructorCheck}
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:15:40
 * @since 1.0.0
 */
public class SpringHideUtilityClassConstructor extends HideUtilityClassConstructorCheck {

	private static final Set<String> BYPASS_ANNOTATIONS;
	static {
		Set<String> annotations = new LinkedHashSet<>();
		annotations.add("org.springframework.context.annotation.Configuration");
		annotations.add("org.springframework.boot.autoconfigure.SpringBootApplication");
		annotations.add("org.springframework.boot.autoconfigure.EnableAutoConfiguration");
		Set<String> shortNames = annotations.stream().map((name) -> name.substring(name.lastIndexOf(".") + 1))
				.collect(Collectors.toSet());
		annotations.addAll(shortNames);
		BYPASS_ANNOTATIONS = Collections.unmodifiableSet(annotations);
	}

	@Override
	public void visitToken(DetailAST ast) {
		if (!isBypassed(ast)) {
			super.visitToken(ast);
		}
	}

	private boolean isBypassed(DetailAST ast) {
		for (String bypassAnnotation : BYPASS_ANNOTATIONS) {
			if (AnnotationUtil.containsAnnotation(ast, bypassAnnotation)) {
				return true;
			}
		}
		return false;
	}

}