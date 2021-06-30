package com.jcohy.checkstyle.check.naming;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AbbreviationAsWordInNameCheck;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 类名使用 UpperCamelCase 风格，必须遵从驼峰形式，但以下情形例外：DO / BO / DTO / VO / AO
 *
 * @author jiac
 * @version 1.0.0 2021/6/30:11:54
 * @since 1.0.0
 */
public class JcohyAbbreviationAsWordInNameCheck extends AbbreviationAsWordInNameCheck {

	private final Set<String> suffixes = new HashSet<>(Arrays.asList("DO","BO","DTO","VO","AO"));

	private static final Set<Integer> TOP_LEVEL_TYPES;

	private static final Set<Integer> OTHER_LEVEL_TYPES;

	static {
		Set<Integer> topLevelTypes = new HashSet<Integer>();
		topLevelTypes.add(TokenTypes.METHOD_DEF);
		topLevelTypes.add(TokenTypes.PARAMETER_DEF);
		topLevelTypes.add(TokenTypes.VARIABLE_DEF);
		TOP_LEVEL_TYPES = Collections.unmodifiableSet(topLevelTypes);
	}

	static {
		Set<Integer> otherLevelTypes = new HashSet<Integer>();
		otherLevelTypes.add(TokenTypes.INTERFACE_DEF);
		otherLevelTypes.add(TokenTypes.CLASS_DEF);
		otherLevelTypes.add(TokenTypes.ENUM_DEF);
		otherLevelTypes.add(TokenTypes.ANNOTATION_DEF);
		OTHER_LEVEL_TYPES = Collections.unmodifiableSet(otherLevelTypes);
	}


	@Override
	public void visitToken(DetailAST ast) {
		if (TOP_LEVEL_TYPES.contains(ast.getType())) {
			checkSuffix(ast);
		}

	}

	private void checkSuffix(DetailAST ast) {
		DetailAST detailAST = ast.findFirstToken(TokenTypes.IDENT);
		if(!this.contain(detailAST.getText())){
			super.visitToken(ast);
		}
	}

	public boolean contain(String str){
		for(String suffix: suffixes){
			if(str.endsWith(suffix)){
				return true;
			}
		}
		return false;
	}

	public void setSuffix(String... suffix) {
		this.suffixes.addAll(Arrays.asList(suffix));
	}
}
