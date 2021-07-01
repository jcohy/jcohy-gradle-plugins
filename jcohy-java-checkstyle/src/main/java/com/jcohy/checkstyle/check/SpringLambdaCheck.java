package com.jcohy.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 一个参数的 lambda 应该有括号。 单语句实现不应使用花括号。
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:16:28
 * @since 1.0.0
 */
public class SpringLambdaCheck extends AbstractSpringCheck {
    
    private boolean singleArgumentParentheses = true;
    
    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.LAMBDA };
        
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.LAMBDA) {
            visitLambda(ast);
        }
    }
    
    private void visitLambda(DetailAST lambda) {
        if (hasSingleParameter(lambda)) {
            boolean hasParentheses = hasToken(lambda, TokenTypes.LPAREN);
            if (this.singleArgumentParentheses && !hasParentheses) {
                log(lambda.getLineNo(), lambda.getColumnNo(), "lambda.missingParen");
            }
            else if (!this.singleArgumentParentheses && hasParentheses) {
                if (!isUsingParametersToDefineType(lambda)) {
                    log(lambda.getLineNo(), lambda.getColumnNo(), "lambda.unnecessaryParen");
                }
            }
        }
        DetailAST block = lambda.getLastChild();
        int statements = countDescendantsOfType(block, TokenTypes.SEMI);
        int requireBlock = countDescendantsOfType(block, TokenTypes.LCURLY, TokenTypes.LITERAL_THROW, TokenTypes.SLIST);
        if (statements == 1 && requireBlock == 0) {
            log(block.getLineNo(), block.getColumnNo(), "lambda.unnecessaryBlock");
        }
    }
    
    private int countDescendantsOfType(DetailAST ast, int... types) {
        int count = 0;
        for (int type : types) {
            count += ast.getChildCount(type);
        }
        DetailAST child = ast.getFirstChild();
        while (child != null) {
            count += countDescendantsOfType(child, types);
            child = child.getNextSibling();
        }
        return count;
    }
    
    private boolean hasSingleParameter(DetailAST lambda) {
        DetailAST parameters = lambda.findFirstToken(TokenTypes.PARAMETERS);
        return (parameters == null) || (parameters.getChildCount(TokenTypes.PARAMETER_DEF) == 1);
    }
    
    private boolean isUsingParametersToDefineType(DetailAST lambda) {
        DetailAST ast = lambda.findFirstToken(TokenTypes.PARAMETERS);
        ast = (ast != null ? ast.findFirstToken(TokenTypes.PARAMETER_DEF) : null);
        ast = (ast != null ? ast.findFirstToken(TokenTypes.TYPE) : null);
        ast = (ast != null ? ast.findFirstToken(TokenTypes.IDENT) : null);
        return ast != null;
    }
    
    private boolean hasToken(DetailAST ast, int type) {
        return ast.findFirstToken(type) != null;
    }
    
    public void setSingleArgumentParentheses(boolean singleArgumentParentheses) {
        this.singleArgumentParentheses = singleArgumentParentheses;
    }
    
}
