package com.jcohy.checkstyle.check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查是否遵循 JUnit 5 约定以及是否不小心使用了 JUnit 4。
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:16:20
 * @since 1.0.0
 */
public class SpringJUnit5Check extends AbstractSpringCheck {
    
    private static final String JUNIT4_TEST_ANNOTATION = "org.junit.Test";
    
    private static final List<String> TEST_ANNOTATIONS;
    
    private static final List<String> LIFECYCLE_ANNOTATIONS;
    
    private static final Set<String> BANNED_IMPORTS;
    
    static {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add("RepeatedTest");
        annotations.add("Test");
        annotations.add("TestFactory");
        annotations.add("TestTemplate");
        annotations.add("ParameterizedTest");
        TEST_ANNOTATIONS = Collections.unmodifiableList(new ArrayList<>(annotations));
    }
    
    static {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add("BeforeAll");
        annotations.add("BeforeEach");
        annotations.add("AfterAll");
        annotations.add("AfterEach");
        LIFECYCLE_ANNOTATIONS = Collections.unmodifiableList(new ArrayList<>(annotations));
    }
    
    static {
        Set<String> bannedImports = new LinkedHashSet<>();
        bannedImports.add(JUNIT4_TEST_ANNOTATION);
        bannedImports.add("org.junit.After");
        bannedImports.add("org.junit.AfterClass");
        bannedImports.add("org.junit.Before");
        bannedImports.add("org.junit.BeforeClass");
        bannedImports.add("org.junit.Rule");
        bannedImports.add("org.junit.ClassRule");
        BANNED_IMPORTS = Collections.unmodifiableSet(bannedImports);
    }
    
    private final List<DetailAST> testMethods = new ArrayList<>();
    
    private final Map<String, FullIdent> imports = new LinkedHashMap<>();
    
    private final List<DetailAST> lifecycleMethods = new ArrayList<>();
    
    private List<String> unlessImports = new ArrayList<>();
    
    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.METHOD_DEF, TokenTypes.IMPORT };
    }
    
    @Override
    public void beginTree(DetailAST rootAST) {
        this.imports.clear();
        this.testMethods.clear();
        this.lifecycleMethods.clear();
    }
    
    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                visitMethodDef(ast);
            case TokenTypes.IMPORT:
                visitImport(ast);
                break;
        }
    }
    
    private void visitMethodDef(DetailAST ast) {
        if (AnnotationUtil.containsAnnotation(ast, TEST_ANNOTATIONS)) {
            this.testMethods.add(ast);
        }
        if (AnnotationUtil.containsAnnotation(ast, LIFECYCLE_ANNOTATIONS)) {
            this.lifecycleMethods.add(ast);
        }
    }
    
    private void visitImport(DetailAST ast) {
        FullIdent ident = FullIdent.createFullIdentBelow(ast);
        this.imports.put(ident.getText(), ident);
    }
    
    @Override
    public void finishTree(DetailAST rootAST) {
        if (shouldCheck()) {
            check();
        }
    }
    
    private boolean shouldCheck() {
        if (this.testMethods.isEmpty() && this.lifecycleMethods.isEmpty()) {
            return false;
        }
        for (String unlessImport : this.unlessImports) {
            if (this.imports.containsKey(unlessImport)) {
                return false;
            }
        }
        return true;
    }
    
    private void check() {
        for (String bannedImport : BANNED_IMPORTS) {
            FullIdent ident = this.imports.get(bannedImport);
            if (ident != null) {
                log(ident.getLineNo(), ident.getColumnNo(), "junit5.bannedImport", bannedImport);
            }
        }
        for (DetailAST testMethod : this.testMethods) {
            if (AnnotationUtil.containsAnnotation(testMethod, JUNIT4_TEST_ANNOTATION)) {
                log(testMethod, "junit5.bannedTestAnnotation");
            }
        }
        checkMethodVisibility(this.testMethods, "junit5.testPublicMethod", "junit5.testPrivateMethod");
        checkMethodVisibility(this.lifecycleMethods, "junit5.lifecyclePublicMethod", "junit5.lifecyclePrivateMethod");
    }
    
    private void checkMethodVisibility(List<DetailAST> methods, String publicMessageKey, String privateMessageKey) {
        for (DetailAST method : methods) {
            DetailAST modifiers = method.findFirstToken(TokenTypes.MODIFIERS);
            if (modifiers.findFirstToken(TokenTypes.LITERAL_PUBLIC) != null) {
                log(method, publicMessageKey);
            }
            if (modifiers.findFirstToken(TokenTypes.LITERAL_PRIVATE) != null) {
                log(method, privateMessageKey);
            }
        }
    }
    
    private void log(DetailAST method, String key) {
        String name = method.findFirstToken(TokenTypes.IDENT).getText();
        log(method.getLineNo(), method.getColumnNo(), key, name);
    }
    
    public void setUnlessImports(String unlessImports) {
        this.unlessImports = Collections.unmodifiableList(
                Arrays.stream(unlessImports.split(",")).map(String::trim).collect(Collectors.toList()));
    }
    
}
