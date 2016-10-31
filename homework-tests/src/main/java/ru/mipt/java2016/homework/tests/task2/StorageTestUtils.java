package ru.mipt.java2016.homework.tests.task2;

import org.apache.commons.io.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Fedor S. Lavrentyev
 * @since 14.10.16
 */
public class StorageTestUtils {
    public static final ThreadLocal<Calendar> CALENDAR = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return Calendar.getInstance();
        }
    };

    private StorageTestUtils() {
        // Cannot instantiate
    }

    public static void doInTempDirectory(Callback<String> callback) {
        try {
            Path path = null;
            try {
                path = Files.createTempDirectory("test_task_2");
                callback.callback(path.toString());
            } finally {
                if (path != null) {
                    FileUtils.deleteDirectory(path.toFile());
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected IOException", e);
        }
    }

    @FunctionalInterface
    public interface Callback<T> {
        void callback(T t) throws Exception;
    }

    public static Date date(int year, int month, int day) {
        Calendar calendar = CALENDAR.get();
        calendar.set(year, month, day);
        return calendar.getTime();
    }
}
