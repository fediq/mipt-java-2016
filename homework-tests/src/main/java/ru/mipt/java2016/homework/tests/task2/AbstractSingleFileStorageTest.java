package ru.mipt.java2016.homework.tests.task2;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils.Callback;

import java.io.File;
import java.util.function.Function;

import static org.junit.Assert.*;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.date;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

/**
 * Оснастка для функционального тестирования {@link ru.mipt.java2016.homework.base.task2.KeyValueStorage}.
 * Для запуска нужно завести конкретный класс-потомок и определить соответствующие фабричные методы.
 *
 * @author Fedor S. Lavrentyev
 * @since 13.10.16
 */
public abstract class AbstractSingleFileStorageTest {

    protected abstract KeyValueStorage<String, String> buildStringsStorage(String path);

    protected abstract KeyValueStorage<Integer, Double> buildNumbersStorage(String path);

    protected abstract KeyValueStorage<StudentKey, Student> buildPojoStorage(String path);


    @Test
    public void testReadWrite() {
        doInTempDirectory(path -> doWithStrings(path, storage -> {
            storage.write("foo", "bar");
            assertEquals("bar", storage.read("foo"));
        }));
    }

    @Test
    public void testPersistence() {
        StudentKey key = new StudentKey(591, "Vasya Pukin");
        Student value = new Student(591, "Vasya Pukin", "Vasyuki", date(1996, 4, 14), true, 7.8);

        doInTempDirectory(path -> {
            doWithPojo(path, storage -> storage.write(key, value));
            doWithPojo(path, storage -> assertEquals(value, storage.read(key)));
        });

        doInTempDirectory(path -> doWithPojo(path, storage -> assertEquals(null, storage.read(key))));
    }

    @Test
    public void testMissingKey() {
        doInTempDirectory(path -> doWithNumbers(path, storage -> {
            storage.write(4, 3.0);
            assertEquals((Object) storage.read(4), 3.0);
            assertEquals(storage.read(5), null);
        }));
    }

    @Test
    public void testMultipleModifications() {
        doInTempDirectory(path -> {
            doWithStrings(path, storage -> {
                storage.write("foo", "bar");
                storage.write("bar", "foo");
                storage.write("yammy", "nooo");
                assertEquals("bar", storage.read("foo"));
                assertEquals("foo", storage.read("bar"));
                assertEquals("nooo", storage.read("yammy"));
            });
            doWithStrings(path, storage -> {
                assertEquals("bar", storage.read("foo"));
                assertEquals("foo", storage.read("bar"));
                assertEquals("nooo", storage.read("yammy"));
            });
            doWithStrings(path, storage -> {
                storage.delete("bar");
                storage.write("yammy", "yeahs");
            });
            doWithStrings(path, storage -> {
                assertEquals("bar", storage.read("foo"));
                assertNull(storage.read("bar"));
                assertEquals("yeahs", storage.read("yammy"));
            });
        });
    }

    @Test
    public void testPersistAndCopy() {
        StudentKey key1 = new StudentKey(591, "Vasya Pukin");
        Student value1 = new Student(591, "Vasya Pukin", "Vasyuki", date(1996, 4, 14), true, 7.8);

        StudentKey key2 = new StudentKey(591, "Abu-Ali Ben Hafiz");
        Student value2 = new Student(591, "Abu-Ali Ben Hafiz", "Baghdad", date(1432, 9, 2), false, 3.3);

        doInTempDirectory(path1 -> {
            doWithPojo(path1, storage -> {
                storage.write(key1, value1);
                storage.write(key2, value2);
            });
            doInTempDirectory(path2 -> {
                File from = new File(path1);
                String path2ext = path2 + File.pathSeparator + "trololo/";
                File to = new File(path2ext);
                FileUtils.copyDirectory(from, to);
                doWithPojo(path2ext, storage -> {
                    assertEquals(value1, storage.read(key1));
                    assertEquals(value2, storage.read(key2));
                });
            });
        });
    }

    @Test
    public void testNonEquality() {
        doInTempDirectory(path -> assertNotSame(doWithPojo(path, null), doWithPojo(path, null)));
        doInTempDirectory(path -> assertNotSame(doWithStrings(path, null), doWithStrings(path, null)));
        doInTempDirectory(path -> assertNotSame(doWithNumbers(path, null), doWithNumbers(path, null)));
    }

    private <K, V> KeyValueStorage<K, V> storageCallback(
            String path, Callback<KeyValueStorage<K, V>> callback, Function<String, KeyValueStorage<K, V>> builder)
            throws Exception {
        KeyValueStorage<K, V> storage = builder.apply(path);
        try {
            if (callback != null) {
                callback.callback(storage);
            }
        } finally {
            storage.close();
        }
        return storage;
    }

    private KeyValueStorage<String, String> doWithStrings(
            String path, Callback<KeyValueStorage<String, String>> callback) throws Exception {
        return storageCallback(path, callback, this::buildStringsStorage);
    }

    private KeyValueStorage<Integer, Double> doWithNumbers(
            String path, Callback<KeyValueStorage<Integer, Double>> callback) throws Exception {
        return storageCallback(path, callback, this::buildNumbersStorage);
    }

    private KeyValueStorage<StudentKey, Student> doWithPojo(
            String path, Callback<KeyValueStorage<StudentKey, Student>> callback) throws Exception {
        return storageCallback(path, callback, this::buildPojoStorage);
    }
}
