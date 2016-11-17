package ru.mipt.java2016.homework.g595.belyh.task2;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g595.belyh.task2.Serializer;
import ru.mipt.java2016.homework.g595.belyh.task2.MySerializer;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by white2302 on 29.10.2016.
 */
public class KeyValueStorageTest extends AbstractSingleFileStorageTest {
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MyKeyValueStorage<String, String>(
                    path,
                    new MySerializer.StringSerializer(),
                    new MySerializer.StringSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MyKeyValueStorage<Integer, Double>(
                    path,
                    new MySerializer.IntegerSerializer(),
                    new MySerializer.DoubleSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MyKeyValueStorage<StudentKey, Student>(
                path,
                new MySerializer.StudentKeySerializer(),
                new MySerializer.StudentSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }
}
