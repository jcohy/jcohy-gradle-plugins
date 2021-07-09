package com.jcohy.convention;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 1.0.0 2021/7/8:21:22
 * @since 1.0.0
 */

public class FileDeleteLineTest {

    private final File projectDir;

    private final File buildFile;

    private final List<String> suffixes = new ArrayList<>(Arrays.asList("jpg","JPG","GIF","gif","png","PNG","JPEG","jpeg"));

    public FileDeleteLineTest() {
        this.projectDir = new File("src/test/txt/source");
        this.buildFile = new File("src/test/txt/build");
    }

    @Test
    public void testDelete() throws IOException {
        System.out.println("================= 文件处理开始 ======================");
//        for(File file : Objects.requireNonNull(projectDir.listFiles())){
//            List<String> lines = readLines(file);
//
//            System.out.println("开始去除 " + file.getName() + " 文件的图片格式");
//            System.out.println("原始文件总量: " + lines.size());
//
//            List<String> remain = lines.stream()
//                    .filter(line -> !contains(line))
//                    .collect(Collectors.toList());
//
//            withPrintWriter(new File(this.buildFile, file.getName()), (writer) -> {
//                remain.forEach(writer::println);
//            });
//            System.out.println("去除后文件总量: "+ remain.size());
//        }
        System.out.println("================= 文件处理结束 ======================");
    }

    private boolean contains(String line){
        for (String suffix : suffixes) {
            if(line.contains(suffix)){
                return true;
            }
        }
        return false;
    }

    private void withPrintWriter(File file, Consumer<PrintWriter> consumer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            consumer.accept(writer);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<String> readLines(File file) {
        try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.toList());
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
