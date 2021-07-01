package com.jcohy.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查  protected, package-private 和 private  的类没有公共方法，除非它们也用 {@link Override @Override} 注解。
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:16:58
 * @since 1.0.0
 */
public class SpringMethodVisibilityCheck extends AbstractSpringCheck {
    
    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.METHOD_DEF };
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null) {
            visitPublicMethod(modifiers, ast);
        }
    }
    
    private void visitPublicMethod(DetailAST modifiers, DetailAST method) {
        if (hasOverrideAnnotation(modifiers)) {
            return;
        }
        DetailAST classDef = getClassDef(method.getParent());
        if (classDef == null || isPublicOrProtected(classDef)) {
            return;
        }
        DetailAST interfaceDef = getInterfaceDef(classDef.getParent());
        if (interfaceDef != null && isPublicOrProtected(interfaceDef)) {
            return;
        }
        DetailAST ident = method.findFirstToken(TokenTypes.IDENT);
        log(ident.getLineNo(), ident.getColumnNo(), "methodvisibility.publicMethod", ident.getText());
    }
    
    private boolean hasOverrideAnnotation(DetailAST modifiers) {
        DetailAST candidate = modifiers.getFirstChild();
        while (candidate != null) {
            if (candidate.getType() == TokenTypes.ANNOTATION) {
                DetailAST dot = candidate.findFirstToken(TokenTypes.DOT);
                String name = (dot != null ? dot : candidate).findFirstToken(TokenTypes.IDENT).getText();
                if ("Override".equals(name)) {
                    return true;
                }
            }
            candidate = candidate.getNextSibling();
        }
        return false;
    }
    
    private DetailAST getClassDef(DetailAST ast) {
        return findParent(ast, TokenTypes.CLASS_DEF);
    }
    
    private DetailAST getInterfaceDef(DetailAST ast) {
        return findParent(ast, TokenTypes.INTERFACE_DEF);
    }
    
    private DetailAST findParent(DetailAST ast, int classDef) {
        while (ast != null) {
            if (ast.getType() == classDef) {
                return ast;
            }
            ast = ast.getParent();
        }
        return null;
    }
    
    private boolean isPublicOrProtected(DetailAST ast) {
        DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers == null) {
            return false;
        }
        return modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null
                || modifiers.findFirstToken(TokenTypes.LITERAL_PROTECTED) != null;
    }
}
