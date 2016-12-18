package ru.mipt.java2016.homework.g596.gerasimov.task3;

import java.io.IOException;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.DoubleSerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.IntegerSerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.StringSerializerV2;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.StudentKeySerializer;
import ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer.StudentSerializer;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by geras-artem on 17.11.16.
 */
public class SSTableKeyValueStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path)
            throws MalformedDataException {
        try {
            return new SSTableKeyValueStorage<>(path,
                    new StringSerializerV2(), new StringSerializerV2(), 100);
        }catch (IOException exception){
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path)
            throws MalformedDataException {
        try {
            return new SSTableKeyValueStorage<>(path, new IntegerSerializer(), new DoubleSerializer(), 100);
        } catch (IOException exception) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path)
            throws MalformedDataException {
        try {
            return new SSTableKeyValueStorage<>(path, new StudentKeySerializer(),
                    new StudentSerializer(), 100);
        } catch (IOException exception) {
            return null;
        }
    }
}
