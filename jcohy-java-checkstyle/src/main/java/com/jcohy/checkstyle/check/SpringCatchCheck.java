package com.jcohy.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查 catch 块
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:15:18
 * @since 0.0.5.1
 */
public class SpringCatchCheck extends AbstractSpringCheck {
    
    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.LITERAL_CATCH };
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.LITERAL_CATCH) {
            visitCatch(ast);
        }
    }
    
    private void visitCatch(DetailAST ast) {
        DetailAST child = ast.getFirstChild();
        while (child != null && child.getType() != TokenTypes.PARAMETER_DEF) {
            child = child.getNextSibling();
        }
        if (child != null) {
            visitParameterDef(child);
        }
    }
    
    private void visitParameterDef(DetailAST ast) {
        DetailAST lastChild = ast.getLastChild();
        if (lastChild != null && lastChild.getType() == TokenTypes.IDENT) {
            checkIdent(lastChild);
        }
    }
    
    
    private void checkIdent(DetailAST ast) {
        String text = ast.getText();
        if (text.length() == 1) {
            log(ast.getLineNo(), ast.getColumnNo(), "catch.singleLetter");
        }
        if (text.equalsIgnoreCase("o_o")) {
            log(ast.getLineNo(), ast.getColumnNo(), "catch.wideEye");
        }
    }
}
