package ru.mipt.java2016.homework.g594.kozlov.task3;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.FileWorker;
import ru.mipt.java2016.homework.g594.kozlov.task2.KVStorageFactory;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anatoly on 14.11.2016.
 */
public class MyTests {
    @Test
    public void simpleWriteReadTest() {
        FileWorker file = new FileWorker("temp/mytemp.txt");
        file.createFile();
        String testString = "testString";
        file.bufferedWrite(testString);
        file.bufferedWriteSubmit();
        file.refresh();
        String result = file.readNextToken();
        assertEquals(testString, result);
    }


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
        try {
            storage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        storage = KVStorageFactory.buildStringsStorage("temp");
        assertEquals("no gain", storage.read("no pain"));
        try {
            storage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
