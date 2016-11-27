package ru.mipt.java2016.homework.tests.task2;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils.*;
import ru.mipt.java2016.homework.tests.task3.KeyValueStorageFactories;

import java.io.File;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.function.Function;


import static org.junit.Assert.*;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.*;

/**
 * Оснастка для функционального тестирования {@link ru.mipt.java2016.homework.base.task2.KeyValueStorage}.
 * Для запуска нужно завести конкретный класс-потомок и определить соответствующие фабричные методы.
 *
 * @author Fedor S. Lavrentyev
 * @since 13.10.16
 */
public abstract class AbstractSingleFileStorageTest extends KeyValueStorageFactories {

    public static final StudentKey KEY_1 = new StudentKey(591, "Vasya Pukin");
    public static final Student VALUE_1 = new Student(591, "Vasya Pukin", "Vasyuki", date(1996, 4, 14), true, 7.8);

    public static final StudentKey KEY_2 = new StudentKey(591, "Ahmad Ben Hafiz");
    public static final Student VALUE_2 = new Student(591, "Ahmad Ben Hafiz", "Cairo", date(1432, 9, 2), false, 3.3);

    public static final StudentKey KEY_3 = new StudentKey(599, "John Smith");
    public static final Student VALUE_3 = new Student(599, "John Smith", "Glasgow", date(1874, 3, 8), true, 9.1);

    @Test
    public void testReadWrite() {
        doInTempDirectory(path -> doWithStrings(path, storage -> {
            storage.write("foo", "bar");
            assertEquals("bar", storage.read("foo"));
            assertEquals(1, storage.size());
            assertFullyMatch(storage.readKeys(), "foo");
        }));
    }

    @Test
    public void testPersistence() {
        doInTempDirectory(path -> {
            doWithPojo(path, storage -> storage.write(KEY_1, VALUE_1));
            doWithPojo(path, storage -> {
                assertEquals(VALUE_1, storage.read(KEY_1));
                assertEquals(1, storage.size());
                assertFullyMatch(storage.readKeys(), KEY_1);
            });
        });

        doInTempDirectory(path -> doWithPojo(path, storage -> assertEquals(null, storage.read(KEY_1))));
    }

    @Test
    public void testMissingKey() {
        doInTempDirectory(path -> doWithNumbers(path, storage -> {
            storage.write(4, 3.0);
            assertEquals((Object) storage.read(4), 3.0);
            assertEquals(storage.read(5), null);
            assertEquals(1, storage.size());
            assertFullyMatch(storage.readKeys(), 4);
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
                assertTrue(storage.exists("foo"));
                assertEquals(3, storage.size());
                assertFullyMatch(storage.readKeys(), "bar", "foo", "yammy");
            });
            doWithStrings(path, storage -> {
                assertEquals("bar", storage.read("foo"));
                assertEquals("foo", storage.read("bar"));
                assertEquals("nooo", storage.read("yammy"));
                assertTrue(storage.exists("bar"));
                assertFalse(storage.exists("yep"));
                assertEquals(3, storage.size());
                assertFullyMatch(storage.readKeys(), "bar", "foo", "yammy");
            });
            doWithStrings(path, storage -> {
                storage.delete("bar");
                storage.write("yammy", "yeahs");
                assertFalse(storage.exists("bar"));
                assertFalse(storage.exists("yep"));
                assertEquals(2, storage.size());
                assertFullyMatch(storage.readKeys(), "foo", "yammy");
            });
            doWithStrings(path, storage -> {
                assertEquals("bar", storage.read("foo"));
                assertNull(storage.read("bar"));
                assertEquals("yeahs", storage.read("yammy"));
                assertEquals(2, storage.size());
                assertFullyMatch(storage.readKeys(), "foo", "yammy");
            });
        });
    }

    @Test
    public void testPersistAndCopy() {
        doInTempDirectory(path1 -> {
            doWithPojo(path1, storage -> {
                storage.write(KEY_1, VALUE_1);
                storage.write(KEY_2, VALUE_2);
            });
            doInTempDirectory(path2 -> {
                File from = new File(path1);
                String path2ext = path2 + File.separator + "trololo/";
                File to = new File(path2ext);
                FileUtils.copyDirectory(from, to);
                doWithPojo(path2ext, storage -> {
                    assertEquals(VALUE_1, storage.read(KEY_1));
                    assertEquals(VALUE_2, storage.read(KEY_2));
                    assertEquals(2, storage.size());
                    assertFullyMatch(storage.readKeys(), KEY_1, KEY_2);
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

    @Test(expected = ConcurrentModificationException.class)
    public void testIteratorWithConcurrentKeysModification() {
        doInTempDirectory(path -> doWithPojo(path, storage -> {
            storage.write(KEY_1, VALUE_1);
            storage.write(KEY_2, VALUE_2);
            storage.write(KEY_3, VALUE_3);
            Iterator<StudentKey> iterator = storage.readKeys();
            assertTrue(iterator.hasNext());
            assertTrue(Arrays.asList(KEY_1, KEY_2, KEY_3).contains(iterator.next()));
            assertTrue(iterator.hasNext());
            assertTrue(Arrays.asList(KEY_1, KEY_2, KEY_3).contains(iterator.next()));
            storage.delete(KEY_2);
            iterator.hasNext();
            iterator.next();
        }));
    }

    @Test
    public void testIteratorWithConcurrentValuesModification() {
        doInTempDirectory(path -> doWithPojo(path, storage -> {
            storage.write(KEY_1, VALUE_1);
            storage.write(KEY_2, VALUE_2);
            storage.write(KEY_3, VALUE_3);
            Iterator<StudentKey> iterator = storage.readKeys();
            assertTrue(iterator.hasNext());
            assertTrue(Arrays.asList(KEY_1, KEY_2, KEY_3).contains(iterator.next()));
            assertTrue(iterator.hasNext());
            assertTrue(Arrays.asList(KEY_1, KEY_2, KEY_3).contains(iterator.next()));
            storage.write(KEY_3, VALUE_2);
            assertTrue(iterator.hasNext());
            assertTrue(Arrays.asList(KEY_1, KEY_2, KEY_3).contains(iterator.next()));
        }));
    }

    @Test(expected = Exception.class)
    public void testDoNotWriteInClosedState() {
        doInTempDirectory(path -> doWithNumbers(path, storage -> {
            assertNull(storage.read(4));
            storage.write(4, 5.0);
            assertEquals((Object) 5.0, storage.read(4));
            storage.close();
            storage.write(3, 5.0);
            throw new AssertionError("Storage should not accept writes in closed state");
        }));
    }

    @Test(expected = Exception.class)
    public void testDoNotReadInClosedState() {
        doInTempDirectory(path -> doWithStrings(path, storage -> {
            assertNull(storage.read("trololo"));
            storage.write("trololo", "yarr");
            assertEquals("yarr", storage.read("trololo"));
            storage.close();
            storage.readKeys();
            throw new AssertionError("Storage should not allow read anything in closed state");
        }));
    }

    protected final <K extends Comparable<K>, V> KeyValueStorage<K, V> storageCallback(
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

    protected final KeyValueStorage<String, String> doWithStrings(
            String path, Callback<KeyValueStorage<String, String>> callback) throws Exception {
        return storageCallback(path, callback, this::buildStringsStorage);
    }

    protected final KeyValueStorage<Integer, Double> doWithNumbers(
            String path, Callback<KeyValueStorage<Integer, Double>> callback) throws Exception {
        return storageCallback(path, callback, this::buildNumbersStorage);
    }

    protected final KeyValueStorage<StudentKey, Student> doWithPojo(
            String path, Callback<KeyValueStorage<StudentKey, Student>> callback) throws Exception {
        return storageCallback(path, callback, this::buildPojoStorage);
    }
}