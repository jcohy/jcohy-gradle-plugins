package com.jcohy.checkstyle.check.naming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.ConstantNameCheck;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 1.0.0 2021/6/30:11:22
 * @since 1.0.0
 */
public class JcohyConstantNameCheck extends ConstantNameCheck {
    
    private Set<String> excludes;
    
    public JcohyConstantNameCheck() {
        setApplyToPublic(true);
        setFormat(Pattern.compile("^log(ger)?$|^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"));
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        if (!excludes.contains(ast.getText())) {
            super.visitToken(ast);
        }
    }
    
    public void setExcludes(String... excludes) {
        this.excludes = new HashSet<>(Arrays.asList(excludes));
    }
}
