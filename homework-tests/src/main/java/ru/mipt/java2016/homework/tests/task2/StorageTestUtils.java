package ru.mipt.java2016.homework.tests.task2;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Fedor S. Lavrentyev
 * @since 14.10.16
 */
public class StorageTestUtils {
    private StorageTestUtils() {
        // Cannot instantiate
    }

    public static void doInTempDirectory(PathCallback callback) {
        try {
            Path path = null;
            try {
                path = Files.createTempDirectory("test_task_2");
                callback.doInPath(path.toString());
            } finally {
                if (path != null) {
                    FileUtils.deleteDirectory(path.toFile());
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }

    @FunctionalInterface
    public interface PathCallback {
        void doInPath(String path) throws IOException;
    }
}
