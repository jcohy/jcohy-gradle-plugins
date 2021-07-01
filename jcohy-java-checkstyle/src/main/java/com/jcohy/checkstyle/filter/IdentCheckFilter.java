package com.jcohy.checkstyle.filter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.RequireThisCheck;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: {@link CheckFilter} 用来跳过 "ident" tokens. 常用作过滤器
 * {@link RequireThisCheck} for logger references.
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:17:07
 * @since 1.0.0
 */
public class IdentCheckFilter extends CheckFilter {
    
    private Set<String> names = Collections.emptySet();
    
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.IDENT && isFiltered(ast)) {
            return;
        }
        super.visitToken(ast);
    }
    
    private boolean isFiltered(DetailAST ast) {
        String name = ast.getText();
        if (this.names.contains(name)) {
            return true;
        }
        return false;
    }
    
    public void setNames(String... names) {
        this.names = new HashSet<>(Arrays.asList(names));
    }
}
