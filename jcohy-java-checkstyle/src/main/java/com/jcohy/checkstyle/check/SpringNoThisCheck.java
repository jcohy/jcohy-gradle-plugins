package com.jcohy.checkstyle.check;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 使用 {@code 'this.'} 检查某些字段是否从未被引用。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:17:01
 * @since 0.0.5.1
 */
public class SpringNoThisCheck extends AbstractSpringCheck {
    
    private Set<String> names = Collections.emptySet();
    
    private boolean allowAssignment = true;
    
    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.IDENT };
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.IDENT) {
            visitIdent(ast);
        }
    }
    
    private void visitIdent(DetailAST ast) {
        String name = ast.getText();
        if (this.names.contains(name)) {
            DetailAST sibling = ast.getPreviousSibling();
            if (sibling != null && sibling.getType() == TokenTypes.LITERAL_THIS) {
                DetailAST parent = getFirstNonDotParent(ast);
                if (!(this.allowAssignment && parent != null && parent.getType() == TokenTypes.ASSIGN)) {
                    log(ast.getLineNo(), ast.getColumnNo(), "nothis.unexpected", name);
                }
            }
        }
    }
    
    private DetailAST getFirstNonDotParent(DetailAST ast) {
        DetailAST result = (ast != null ? ast.getParent() : null);
        while (result != null && result.getType() == TokenTypes.DOT) {
            result = result.getParent();
        }
        return result;
    }
    
    public void setNames(String... names) {
        this.names = new HashSet<>(Arrays.asList(names));
    }
    
    public void setAllowAssignment(boolean allowAssignment) {
        this.allowAssignment = allowAssignment;
    }
    
}
