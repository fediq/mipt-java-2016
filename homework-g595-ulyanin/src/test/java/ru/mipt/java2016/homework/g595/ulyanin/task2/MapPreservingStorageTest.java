package ru.mipt.java2016.homework.g595.ulyanin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author ulyanin
 * @since 31.10.16
 */
public class MapPreservingStorageTest extends AbstractSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MapPreservingStorage<String, String> result = null;
        try {
            result = new MapPreservingStorage<>(
                    path,
                    StringSerializer.getInstance(),
                    StringSerializer.getInstance()
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MapPreservingStorage<Integer, Double> result = null;
        try {
            result = new MapPreservingStorage<>(
                    path,
                    IntegerSerializer.getInstance(),
                    DoubleSerializer.getInstance()
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MapPreservingStorage<StudentKey, Student> result = null;
        try {
            result = new MapPreservingStorage<>(
                    path,
                    StudentKeySerializer.getInstance(),
                    StudentSerializer.getInstance()
            );
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

}
