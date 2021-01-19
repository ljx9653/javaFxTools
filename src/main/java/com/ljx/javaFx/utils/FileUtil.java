package com.ljx.javaFx.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * @author lijx
 * @date 2021/1/19 - 10:13
 */
public class FileUtil {

    /**
     * 删除目标文件夹，即使文件夹不为空，
     * 当文件夹不为空时，直接删除会报错，因此采用这种方式去删除
     *
     * @param target 目标文件或者目标
     * @throws IOException IOException
     */
    public static void delete(Path target) throws IOException {
        if (Files.isDirectory(target)) {
            Files.walkFileTree(target, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new DeleteFileVisitor());
        } else {
            Files.deleteIfExists(target);
        }
    }

    private static class DeleteFileVisitor extends SimpleFileVisitor<Path> {

        /**
         * 表示访问一个文件时要进行的操作
         */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * 表示访问一个目录后要进行的操作
         */
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc == null) {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            } else {
                // directory iteration failed
                throw exc;
            }
        }
    }
}
