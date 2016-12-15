package ru.mipt.java2016.homework.g594.kozlov.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.kozlov.task2.KVStorageFactory;
import ru.mipt.java2016.homework.g594.kozlov.task2.serializer.StringSerializer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


/**
 * Created by Anatoly on 14.11.2016.
 */
public class MyTest {

    @Ignore
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

    @Ignore
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

    @Test
    public void storageStorageDoubleClose() {
        KeyValueStorage<String, String> storage =
                new FastStorage<>(new StringSerializer(), new StringSerializer(), "");
        storage.write("no pain", "no gain");
        try {
            storage.close();
            storage.close();
            storage.close();
            storage.close();
        } catch (IOException e) {
            System.out.println("smth went wrong");
        }
        storage = new FastStorage<>(new StringSerializer(), new StringSerializer(), "");
        assertEquals("no gain", storage.read("no pain"));
        try {
            storage.close();
        } catch (IOException e) {
            System.out.println("smth went wrong");
        }
    }
}
