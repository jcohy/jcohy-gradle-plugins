package com.jcohy.checkstyle.filter;

import java.util.Set;
import java.util.SortedSet;

import com.puppycrawl.tools.checkstyle.DefaultContext;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageNamesLoader;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.Context;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.Violation;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:{@link AbstractCheck checks}  的基类，用作单个子项的过滤器。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:17:06
 * @since 0.0.5.1
 */
public class CheckFilter extends AbstractCheck {

    private Context childContext;

    private AbstractCheck check;

    @Override
    public void finishLocalSetup() {
        DefaultContext context = new DefaultContext();
        context.add("severity", getSeverity());
        context.add("tabWidth", String.valueOf(getTabWidth()));
        this.childContext = context;
    }

    @Override
    public void setupChild(Configuration childConf) throws CheckstyleException {
        ModuleFactory moduleFactory = createModuleFactory();
        String name = childConf.getName();
        Object module = moduleFactory.createModule(name);
        if (!(module instanceof AbstractCheck)) {
            throw new CheckstyleException("OptionalCheck is not allowed as a parent of " + name
                    + " Please review 'Parent Module' section for this Check.");
        }
        if (this.check != null) {
            throw new CheckstyleException("Can only make a single check optional");
        }
        AbstractCheck moduleCheck = (AbstractCheck) module;
        moduleCheck.contextualize(this.childContext);
        moduleCheck.configure(childConf);
        moduleCheck.init();
        this.check = moduleCheck;
    }

    private ModuleFactory createModuleFactory() {
        try {
            ClassLoader classLoader = AbstractCheck.class.getClassLoader();
            Set<String> packageNames = PackageNamesLoader.getPackageNames(classLoader);
            return new PackageObjectFactory(packageNames, classLoader);
        }
        catch (CheckstyleException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return this.check.getDefaultTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.check.getAcceptableTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.check.getRequiredTokens();
    }

    @Override
    public boolean isCommentNodesRequired() {
        return this.check.isCommentNodesRequired();
    }

    @Override
    public SortedSet<Violation> getViolations() {
        return this.check.getViolations();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        this.check.setFileContents(getFileContents());
        this.check.clearViolations();
        this.check.beginTree(rootAST);
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        this.check.finishTree(rootAST);
    }

    @Override
    public void visitToken(DetailAST ast) {
        this.check.visitToken(ast);
    }

    @Override
    public void leaveToken(DetailAST ast) {
        this.check.leaveToken(ast);
    }

}
