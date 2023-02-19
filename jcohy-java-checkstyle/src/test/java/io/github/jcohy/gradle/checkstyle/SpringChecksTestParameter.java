package io.github.jcohy.gradle.checkstyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.puppycrawl.tools.checkstyle.api.AuditListener;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 * <p>
 * Description: 通过读取一个简单的文本文件来判断检查结果，如果文本必须存在，则使用  {@code +} 开头，如果必须不存在，则以  {@code -} 开头
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:17:34
 * @since 0.0.5.1
 */
public class SpringChecksTestParameter {

    private final List<String> checks;

    public SpringChecksTestParameter(File file) throws IOException {
        this.checks = Collections.unmodifiableList(Files.readAllLines(file.toPath()));
    }

    public void attach(Consumer<AuditListener> consumer) {
        consumer.accept(new AssertionsAuditListener(this.checks));
    }

}
