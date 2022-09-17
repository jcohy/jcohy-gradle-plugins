package com.jcohy.checkstyle.check;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:15:13
 * @since 0.0.5.1
 */
public abstract class AbstractSpringCheck extends AbstractCheck {

    public static final int[] NO_REQUIRED_TOKENS = {};

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return NO_REQUIRED_TOKENS;
    }

}