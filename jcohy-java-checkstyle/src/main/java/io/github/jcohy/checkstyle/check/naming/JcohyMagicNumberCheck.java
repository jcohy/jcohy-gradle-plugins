package io.github.jcohy.checkstyle.check.naming;

import com.puppycrawl.tools.checkstyle.checks.coding.MagicNumberCheck;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p> Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/4:0:10
 * @since 0.0.5.1
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
