package com.jcohy.checkstyle.check;

import java.util.Locale;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查三元操作是否遵循 Spring 约定。 所有三元测试都应该有括号。 应使用不等于而不是等于作为对空值的测试。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:17:04
 * @since 0.0.5.1
 */
public class SpringTernaryCheck extends AbstractSpringCheck {
    
    private EqualsTest equalsTest = EqualsTest.NEVER_FOR_NULLS;
    
    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.QUESTION };
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.QUESTION) {
            visitQuestion(ast);
        }
    }
    
    private void visitQuestion(DetailAST ast) {
        DetailAST expression = ast.getFirstChild();
        if (!hasType(expression, TokenTypes.LPAREN)) {
            if (expression != null && requiresParens(expression)) {
                log(ast.getLineNo(), ast.getColumnNo(), "ternary.missingParen");
            }
        }
        while (hasType(expression, TokenTypes.LPAREN)) {
            expression = expression.getNextSibling();
        }
        if (isSimpleEqualsExpression(expression) && !isEqualsTestAllowed(ast)) {
            log(ast.getLineNo(), ast.getColumnNo(), "ternary.equalOperator");
        }
    }
    
    private boolean requiresParens(DetailAST expression) {
        if (expression != null && expression.getChildCount() > 1) {
            switch (expression.getType()) {
                case TokenTypes.METHOD_CALL:
                case TokenTypes.DOT:
                    return false;
            }
            return true;
        }
        return false;
    }
    
    private boolean isSimpleEqualsExpression(DetailAST expression) {
        if (expression == null || expression.getType() != TokenTypes.EQUAL) {
            return false;
        }
        DetailAST child = expression.getFirstChild();
        while (child != null) {
            if (child.getChildCount() > 0) {
                return false;
            }
            child = child.getNextSibling();
        }
        return true;
    }
    
    private boolean isEqualsTestAllowed(DetailAST ast) {
        switch (this.equalsTest) {
            case ANY:
                return true;
            case NEVER:
                return false;
            case NEVER_FOR_NULLS:
                DetailAST equal = ast.findFirstToken(TokenTypes.EQUAL);
                return equal.findFirstToken(TokenTypes.LITERAL_NULL) == null;
        }
        throw new IllegalStateException("Unsupported equals test " + this.equalsTest);
    }
    
    private boolean hasType(DetailAST ast, int type) {
        return (ast != null && ast.getType() == type);
    }
    
    public void setEqualsTest(String equalsTest) {
        try {
            this.equalsTest = Enum.valueOf(EqualsTest.class, equalsTest.trim().toUpperCase(Locale.ENGLISH));
        }
        catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("unable to parse " + equalsTest, ex);
        }
    }
    
    /**
     * Type of equals operators allowed in the test condition.
     */
    public enum EqualsTest {
        
        /**
         * Equals checks can be used for any test.
         */
        ANY,
        
        /**
         * Equals tests can never be used.
         */
        NEVER,
        
        /**
         * Equals tests can never be used for {@code null} checks.
         */
        NEVER_FOR_NULLS
        
    }
    
}
