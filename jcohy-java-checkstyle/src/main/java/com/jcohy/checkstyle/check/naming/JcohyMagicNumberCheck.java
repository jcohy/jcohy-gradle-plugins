package com.jcohy.checkstyle.check.naming;

import com.puppycrawl.tools.checkstyle.checks.coding.MagicNumberCheck;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 1.0.0 2021/7/4:0:10
 * @since 1.0.0
 */
public class JcohyMagicNumberCheck extends MagicNumberCheck {

    /**
     * The default root package.
     */
//    private double[] ignoreNumbers = {};

    public JcohyMagicNumberCheck() {
        setIgnoreNumbers(-1, 0, 1, 2, 4, 8, 10, 16, 32, 64, 1024, 2048);
        setIgnoreAnnotationElementDefaults(true);
        setIgnoreHashCodeMethod(true);
        setIgnoreAnnotation(true);
    }
}
