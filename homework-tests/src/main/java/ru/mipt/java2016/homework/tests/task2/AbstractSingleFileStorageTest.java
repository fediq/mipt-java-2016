package ru.mipt.java2016.homework.tests.task2;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;

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

    protected abstract KeyValueStorage<Integer, Double> buildI2DStorage(String path);

    protected abstract KeyValueStorage<StudentKey, Student> buildStudentsStorage(String path);


    @Test
    public void testReadWrite() {
        doInTempDirectory(path -> {
            KeyValueStorage<String, String> storage = buildStringsStorage(path);
            storage.write("foo", "barr");
            Assert.assertEquals("bar", storage.read("foo"));
            storage.close();
        });
    }

    @Test
    public void testNonEqual() {
        doInTempDirectory(path -> {
            KeyValueStorage<?, ?> storage1 = buildStringsStorage(path);
            storage1.close();
            KeyValueStorage<?, ?> storage2 = buildStringsStorage(path);
            storage2.close();
            Assert.assertNotEquals(storage1, storage2);
        });

        doInTempDirectory(path -> {
            KeyValueStorage<?, ?> storage1 = buildStudentsStorage(path);
            storage1.close();
            KeyValueStorage<?, ?> storage2 = buildStudentsStorage(path);
            storage2.close();
            Assert.assertNotEquals(storage1, storage2);
        });

        doInTempDirectory(path -> {
            KeyValueStorage<?, ?> storage1 = buildI2DStorage(path);
            storage1.close();
            KeyValueStorage<?, ?> storage2 = buildI2DStorage(path);
            storage2.close();
            Assert.assertNotEquals(storage1, storage2);
        });
    }
}