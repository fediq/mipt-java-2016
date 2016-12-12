package ru.mipt.java2016.homework.g597.zakharkin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.*;

import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

/**
 * Test class for KeyValueDB storage
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class KeyValueDBTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new KeyValueDB<String, String>(path, "storage.db", StringSerializer.getInstance(),
                    StringSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new KeyValueDB<Integer, Double>(path, "storage.db", IntegerSerializer.getInstance(), DoubleSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new KeyValueDB<StudentKey, Student>(path, "storage.db", StudentKeySerializer.getInstance(), StudentSerializer.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
