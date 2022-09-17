package com.jcohy.checkstyle.check.naming;

import com.jcohy.checkstyle.check.AbstractSpringCheck;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/30:17:21
 * @since 0.0.5.1
 */
public class JcohyEnumCheck extends AbstractSpringCheck {

    @Override
    public int[] getAcceptableTokens() {
        return new int[] { TokenTypes.ENUM_DEF };
    }
}
