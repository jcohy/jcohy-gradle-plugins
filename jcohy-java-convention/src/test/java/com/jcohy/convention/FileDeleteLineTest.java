package com.jcohy.convention;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.gradle.internal.impldep.com.google.api.client.json.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright (c) 2021 <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p> Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/8:21:22
 * @since 0.0.5.1
 */

public class FileDeleteLineTest {

    private final List<String> suffixes = new ArrayList<>(Arrays.asList("jpg","JPG","GIF","gif","png","PNG","JPEG","jpeg"));

    @Test
    public void toTxt(){
        File projectDir = new File("src/test/json/source");
        File buildFile = new File("src/test/json/build");

        for(File file : Objects.requireNonNull(projectDir.listFiles())){
            String content = readFiles(file);
            System.out.println("================= 文件处理开始 =="+file.getName()+"====================");
            JSONObject objJson = JSONObject.parseObject(content);
            List<String> lines = new JsonUtils().analysisJson(objJson);
            System.out.println("处理前文件："+lines.size());
            List<String> remain = lines
                    .stream()
                    .map(this::addprefix)
                    .filter(line -> !contains(line))
                    .collect(Collectors.toList());
            System.out.println("处理后文件：" + remain.size());

            String fileName = file.getName().replace(".json",".txt");
            withPrintWriter(new File(buildFile, fileName), (writer) -> {
                remain.forEach(writer::println);
            });
        }
        System.out.println("================= 文件处理结束 ======================");
    }

    @Test
    public void testDelete() throws IOException {
        File projectDir = new File("src/test/txt/source");
        File buildFile = new File("src/test/txt/build");
        System.out.println("================= 文件处理开始 ======================");
        for(File file : Objects.requireNonNull(projectDir.listFiles())){
            List<String> lines = readLines(file);

            System.out.println("开始去除 " + file.getName() + " 文件的图片格式");
            System.out.println("原始文件总量: " + lines.size());

            List<String> remain = lines.stream()
                    .map(this::addprefix)
                    .filter(line -> !contains(line))
                    .collect(Collectors.toList());

            withPrintWriter(new File(buildFile, file.getName()), (writer) -> {
                remain.forEach(writer::println);
            });
            System.out.println("去除后文件总量: "+ remain.size());
        }
        System.out.println("================= 文件处理结束 ======================");
    }

    private String addprefix(String str) {
        if(!str.startsWith("115://") || !str.startsWith("aliyun")) {
            str = "115://" + str;
        }
        return str;
    }

    private String readFiles(File file) {
        StringBuffer buffer = new StringBuffer();
        try(BufferedReader bf = new BufferedReader(new FileReader(file))){
            String s;
            while((s = bf.readLine())!=null){//使用readLine方法，一次读一行
                buffer.append(s.trim());
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
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

    private class Text115 {

        private String dir_ame;

        private List<String> files;

        public String getDir_ame() {
            return dir_ame;
        }

        public void setDir_ame(String dir_ame) {
            this.dir_ame = dir_ame;
        }

        public List<String> getFiles() {
            return files;
        }

        public void setFiles(List<String> files) {
            this.files = files;
        }

        @Override
        public String toString() {
            return "Text115{" +
                    "dir_ame='" + dir_ame + '\'' +
                    ", files=" + files +
                    '}';
        }
    }
}
