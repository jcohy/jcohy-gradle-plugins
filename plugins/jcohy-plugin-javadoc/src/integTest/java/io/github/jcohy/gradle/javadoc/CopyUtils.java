package io.github.jcohy.gradle.javadoc;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/9/23:16:14
 * @since 2022.0.1
 */
final class CopyUtils {

    private CopyUtils() {
    }

    static void fromResourceNameToDir(String projectResourceName, File toDir) throws IOException, URISyntaxException {
        toDir.mkdirs();
        ClassLoader classLoader = CopyUtils.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(projectResourceName);
        if (resourceUrl == null) {
            throw new IOException("Cannot find resource '" + projectResourceName + "' with " + classLoader);
        }
        Path source = Paths.get(resourceUrl.toURI());
        copyRecursively(source, toDir.toPath());
    }

    private static void copyRecursively(Path source, Path destination) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs)
                    throws IOException {
                Files.createDirectories(resolve(dir));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                Files.copy(file, resolve(file));
                return FileVisitResult.CONTINUE;
            }

            private Path resolve(Path file) {
                return destination.resolve(source.relativize(file));
            }
        });
    }
}
