package io.github.jcohy.gradle;

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
import java.util.Map;

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

    public static File getResourceAsFile(Class<?> clazz, String Path) {
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
}
