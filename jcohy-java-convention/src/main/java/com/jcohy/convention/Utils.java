package com.jcohy.convention;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jcohy.convention.conventions.AsciidoctorConventions;

/**
 * <p> 描述: .
 * Copyright: Copyright (c) 2021.
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * @author jiac
 * @version 0.0.5.1 2021/7/15:16:38
 * @since 0.0.5.1
 */
public class Utils {

    public static File getResourceAsFile(Class clazz,String Path){
		List
        File file = null;
        URL resource = clazz.getResource(Path);
        try {
            file = new File(resource.toURI());
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File extractResources(Class clazz ,String dir) {
        File file = null;
        try {
            URI resource = AsciidoctorConventions.class.getResource(dir).toURI();
            String[] array = resource.toString().split("!");
            Map<String, String> env = new HashMap<>();
            try(FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), env)) {
                Path path = fs.getPath(array[1]);
                if(Files.isDirectory(path)){
                    Files.list(path).forEach((p) -> System.out.println(p.toString()));
                } else {
                    System.out.println(path.toString());
                }
                Path path1 = Files.copy(path, Paths.get("D:/logo.svg"));
                file = new File(path.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
