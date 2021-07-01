package com.jcohy.checkstyle.check;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractFileSetCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.checks.header.RegexpHeaderCheck;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description: 检查源文件的头部信息
 *
 * @author jiac
 * @version 1.0.0 2021/6/21:10:53
 * @since 1.0.0
 */
public class SpringHeaderCheck extends AbstractFileSetCheck {
    
    /**
     * 默认 header 类型.
     */
    public static final String DEFAULT_HEADER_TYPE = "apache2";
    
    /**
     * 默认 copyright 版权规则.
     */
    public static final String DEFAULT_HEADER_COPYRIGHT_PATTERN = "20\\d\\d(-20\\d\\d)?";
    
    /**
     * 不检查 header.
     */
    private static final String UNCHECKED = "unchecked";
    
    /**
     * 强制不能使用 header.
     */
    private static final String NONE = "none";
    
    /**
     * 默认文件编码.
     */
    private static final String DEFAULT_CHARSET = System.getProperty("file.encoding", StandardCharsets.UTF_8.name());
    
    /**
     * 编码.
     */
    private String charset = DEFAULT_CHARSET;
    
    /**
     * 文件类型.
     */
    private String headerType = DEFAULT_HEADER_TYPE;
    
    /**
     * 指定包含 header 信息的文件名.
     */
    private URI headerFile;
    
    /**
     * header 版权规则.
     */
    private String headerCopyrightPattern = DEFAULT_HEADER_COPYRIGHT_PATTERN;
    
    /**
     * package-info 文件类型.
     */
    private String packageInfoHeaderType;
    
    /**
     * package-info 文件地址.
     */
    private URI packageInfoHeaderFile;
    
    /**
     * 最后一行是否包含空白行，默认为 {@code true}.
     */
    private boolean blankLineAfter = true;
    
    /**
     * Header 检查规则.
     */
    private HeaderCheck check;
    
    /**
     * 默认编码.
     */
    private HeaderCheck packageInfoCheck;
    
    @Override
    protected void finishLocalSetup() {
        try {
            this.check = createCheck(this.headerType, this.headerFile);
            String packageHeaderType = this.packageInfoHeaderType != null ? this.packageInfoHeaderType
                    : this.headerType;
            URI packageHeaderFile = this.packageInfoHeaderFile != null ? this.packageInfoHeaderFile
                    : this.headerFile;
            this.packageInfoCheck = createCheck(packageHeaderType, packageHeaderFile);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 创建 {@link HeaderCheck}
     * @param headerType headerType
     * @param headerFile headerFile
     * @return HeaderCheck
     * @throws IOException ex
     */
    private HeaderCheck createCheck(String headerType, URI headerFile) throws IOException {
        if (UNCHECKED.equals(headerType)) {
            return HeaderCheck.NONE;
        }
        if (NONE.equals(headerType)) {
            return new NoHeaderCheck();
        }
        return new RegexHeaderCheck(headerType, headerFile);
    }
    
    @Override
    protected void processFiltered(File file, FileText fileText)  {
        getCheck(file).run(fileText, this.blankLineAfter);
    }
    
    public void setCharset(String charset) throws UnsupportedEncodingException {
        if (!Charset.isSupported(charset)) {
            throw new UnsupportedEncodingException("unsupported charset: '" + charset + "'");
        }
        this.charset = charset;
    }
    
    public void setHeaderType(String headerType) {
        this.headerType = headerType;
    }
    
    public void setHeaderFile(URI headerFile) {
        this.headerFile = headerFile;
    }
    
    public void setHeaderCopyrightPattern(String headerCopyrightPattern) {
        this.headerCopyrightPattern = headerCopyrightPattern;
    }
    
    public void setPackageInfoHeaderType(String packageInfoHeaderType) {
        this.packageInfoHeaderType = packageInfoHeaderType;
    }
    
    public void setPackageInfoHeaderFile(URI packageInfoHeaderFile) {
        this.packageInfoHeaderFile = packageInfoHeaderFile;
    }
    
    public void setBlankLineAfter(boolean blankLineAfter) {
        this.blankLineAfter = blankLineAfter;
    }
    
    private HeaderCheck getCheck(File file) {
        if (file.getName().startsWith("package-info")) {
            return this.packageInfoCheck;
        }
        return this.check;
    }
    
    private interface HeaderCheck {
        /**
         * 不运行任何检查.
         */
        HeaderCheck NONE = (fileText, blankLineAfter) -> true;
        
        /**
         * 运行检查.
         * @param fileText 检查的文件
         * @param blankLineAfter 如果 header 后面应该有一个空行
         * @return {@code true} header is valid
         */
        boolean run(FileText fileText, boolean blankLineAfter);
    }
    
    
    /**
     * {@link HeaderCheck} 基于表达式.
     */
    private class RegexHeaderCheck implements HeaderCheck {
        
        private final List<Pattern> lines;
        
        RegexHeaderCheck(String type, URI file) throws IOException {
            this.lines = loadLines(openInputStream(type, file));
        }
        
        /**
         * 读取 header 文件.
         * 如果 file 不为空,则读取 file 指定的文件，否则则读取 header-{type}.txt 文件
         * @param type 文件类型 {@code headerType}
         * @param file 文件地址
         * @return /
         * @throws IOException ex
         */
        private InputStream openInputStream(String type, URI file) throws IOException {
            if (file != null) {
                return file.toURL().openStream();
            }
            String name = "header-" + type + ".txt";
            InputStream inputStream = SpringHeaderCheck.class.getResourceAsStream(name);
            if (inputStream == null) {
                throw new IllegalStateException("Unknown header type " + type);
            }
            return inputStream;
        }
        
        /**
         * 为每一行添加正则表达式
         * @param inputStream inputStream
         * @return List<Pattern>
         * @throws IOException ex
         */
        private List<Pattern> loadLines(InputStream inputStream) throws IOException {
            inputStream = new BufferedInputStream(inputStream);
            try (Reader reader = new InputStreamReader(inputStream, SpringHeaderCheck.this.charset)) {
                LineNumberReader lineReader = new LineNumberReader(reader);
                List<Pattern> lines = new ArrayList<>();
                while (true) {
                    String line = lineReader.readLine();
                    if (line == null) {
                        return lines;
                    }
                    lines.add(loadLine(line, SpringHeaderCheck.this.headerCopyrightPattern));
                }
            }
        }
        
        /**
         * 将给定的正则表达式编译并赋予给 {@link Pattern} 类
         * @param line line
         * @param copyrightPattern copyrightPattern
         * @return Pattern
         */
        private Pattern loadLine(String line, String copyrightPattern) {
            line = line.replace("${copyright-pattern}", "\\E" + copyrightPattern + "\\Q");
            line = "^\\Q" + line + "\\E$";
            return Pattern.compile(line);
        }
        
        @Override
        public boolean run(FileText fileText, boolean blankLineAfter) {
            if (this.lines.size() > fileText.size()) {
                log(1, RegexpHeaderCheck.MSG_HEADER_MISSING);
                return false;
            }
            
            for (int i = 0; i < this.lines.size(); i++) {
                String fileLine = fileText.get(i);
                Pattern pattern = this.lines.get(i);
                if (!pattern.matcher(fileLine).matches()) {
                    log(i + 1, RegexpHeaderCheck.MSG_HEADER_MISMATCH, pattern);
                    return false;
                }
            }
            if (blankLineAfter) {
                if (fileText.size() <= this.lines.size() || !"".equals(fileText.get(this.lines.size()))) {
                    log(this.lines.size() + 1, "header.blankLine");
                }
            }
            return true;
        }
        
    }
    
    /**
     * {@link HeaderCheck} 强制不使用 header.
     */
    private class NoHeaderCheck implements HeaderCheck {
        
        @Override
        public boolean run(FileText fileText, boolean blankLineAfter) {
            
            for (int i = 0; i < fileText.size(); i++) {
                String fileLine = fileText.get(i);
                if (fileLine.trim().isEmpty()) {
                    continue;
                }
                if (isHeaderComment(fileLine)) {
                    log(i, "header.unexpected");
                    return false;
                }
                return true;
            }
            return true;
        }
        
        private boolean isHeaderComment(String fileLine) {
            return fileLine.contains("/*") || fileLine.contains("//") && !fileLine.contains("/**");
        }
    }
}
