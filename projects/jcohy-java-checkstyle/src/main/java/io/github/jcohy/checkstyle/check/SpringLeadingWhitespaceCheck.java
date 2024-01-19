package io.github.jcohy.checkstyle.check;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.jcohy.checkstyle.config.IndentationStyle;
import io.github.jcohy.checkstyle.config.JavaFormatConfig;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description: 检查每一行前面空格是否与缩进样式匹配。
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:16:32
 * @since 0.0.5.1
 */
public class SpringLeadingWhitespaceCheck extends AbstractSpringCheck {

    private static final Pattern PATTERN = Pattern.compile("^([\\ \\t]+)\\S");

    private static final Map<IndentationStyle, Pattern> INDENTATION_STYLE_PATTERN;

    static {
        Map<IndentationStyle, Pattern> indentationStylePatterns = new HashMap<>();
        indentationStylePatterns.put(IndentationStyle.TABS, Pattern.compile("\\t*"));
        indentationStylePatterns.put(IndentationStyle.SPACES, Pattern.compile("\\ *"));
        INDENTATION_STYLE_PATTERN = Collections.unmodifiableMap(indentationStylePatterns);
    }

    private IndentationStyle indentationStyle;

    @Override
    public int[] getAcceptableTokens() {
        return NO_REQUIRED_TOKENS;
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        super.beginTree(rootAST);
        FileContents fileContents = getFileContents();
        FileText fileText = fileContents.getText();
        File file = fileText.getFile();
        if (file == null) {
            return;
        }
        IndentationStyle indentationStyle = (this.indentationStyle != null) ? this.indentationStyle
                : JavaFormatConfig.findFrom(file.getParentFile()).getIndentationStyle();
        for (int i = 0; i < fileText.size(); i++) {
            String line = fileText.get(i);
            int lineNo = i + 1;
            Matcher matcher = PATTERN.matcher(line);
            boolean found = matcher.find(0);
            while (found
                    && fileContents.hasIntersectionWithComment(lineNo, matcher.start(0), lineNo, matcher.end(0) - 1)) {
                found = matcher.find(matcher.end(0));
            }
            if (found && !INDENTATION_STYLE_PATTERN.get(indentationStyle).matcher(matcher.group(1)).matches()) {
                log(lineNo, "leadingwhitespace.incorrect", indentationStyle.toString().toLowerCase());
            }
        }
    }

    public void setIndentationStyle(String indentationStyle) {
        this.indentationStyle = (indentationStyle != null && !"".equals(indentationStyle))
                ? IndentationStyle.valueOf(indentationStyle.toUpperCase()) : null;
    }
}
