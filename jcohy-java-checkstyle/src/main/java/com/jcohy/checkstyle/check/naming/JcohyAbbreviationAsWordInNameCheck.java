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
 * <p>
 * Description: 类名使用 UpperCamelCase 风格，必须遵从驼峰形式，但以下情形例外：DO / BO / DTO / VO / AO
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/30:11:54
 * @since 0.0.5.1
 */
public class JcohyAbbreviationAsWordInNameCheck extends AbbreviationAsWordInNameCheck {

    private static final Set<Integer> TOP_LEVEL_TYPES;

//    private static final Set<Integer> OTHER_LEVEL_TYPES;

//    static {
//        Set<Integer> otherLevelTypes = new HashSet<>();
//        otherLevelTypes.add(TokenTypes.METHOD_DEF);
//        otherLevelTypes.add(TokenTypes.PARAMETER_DEF);
//        otherLevelTypes.add(TokenTypes.VARIABLE_DEF);
//        OTHER_LEVEL_TYPES = Collections.unmodifiableSet(otherLevelTypes);
//    }

    static {
        Set<Integer> topLevelTypes = new HashSet<>();
        topLevelTypes.add(TokenTypes.INTERFACE_DEF);
        topLevelTypes.add(TokenTypes.CLASS_DEF);
        topLevelTypes.add(TokenTypes.ENUM_DEF);
        topLevelTypes.add(TokenTypes.ANNOTATION_DEF);
        TOP_LEVEL_TYPES = Collections.unmodifiableSet(topLevelTypes);
    }

    private final Set<String> suffixes = new HashSet<>(Arrays.asList("DO", "BO", "DTO", "VO", "AO", "OSS"));

    private final Set<String> prefixes = new HashSet<>(Arrays.asList("OSS"));

    @Override
    public void visitToken(DetailAST ast) {
        if (TOP_LEVEL_TYPES.contains(ast.getType())) {
            checkSuffix(ast);
        }

    }

    private void checkSuffix(DetailAST ast) {
        DetailAST detailAST = ast.findFirstToken(TokenTypes.IDENT);
        String name = detailAST.getText();
        if (!this.containSuffix(name) && !this.containPrefix(name)) {
            super.visitToken(ast);
        }
    }

    public boolean containPrefix(String str) {
        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public boolean containSuffix(String str) {
        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public void setPrefix(String... prefix) {
        this.prefixes.addAll(Arrays.asList(prefix));
    }

    public void setSuffix(String... suffix) {
        this.suffixes.addAll(Arrays.asList(suffix));
    }
}
