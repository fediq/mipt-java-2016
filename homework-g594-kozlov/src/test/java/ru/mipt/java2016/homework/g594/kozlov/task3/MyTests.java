package ru.mipt.java2016.homework.g594.kozlov.task3;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.FileWorker;
import ru.mipt.java2016.homework.g594.kozlov.task2.KVStorageFactory;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.File;
import java.io.IOException;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest.*;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.assertFullyMatch;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

/**
 * Created by Anatoly on 14.11.2016.
 */
public class MyTests {
    @Test
    public void storageWriteReadTest() {
        KeyValueStorage<String, String> storage = KVStorageFactory.buildStringsStorage("temp");
        storage.write("no pain", "no gain");
        assertEquals("no gain", storage.read("no pain"));
        try {
            storage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void storageWriteCloseReadTest() {
        KeyValueStorage<String, String> storage = KVStorageFactory.buildStringsStorage("temp");
        storage.write("no pain", "no gain");
        storage.write("no pain2", "no gain2");
        try {
            storage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        storage = KVStorageFactory.buildStringsStorage("temp");
        assertEquals("no gain", storage.read("no pain"));
        assertEquals("no gain2", storage.read("no pain2"));
        try {
            storage.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
