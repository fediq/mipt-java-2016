package ru.mipt.java2016.homework.g594.borodin.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies.DoubleSerialization;
import ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies.IntSerialization;
import ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies.StringSerialization;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Maxim on 10/31/2016.
 */
public class KVStorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new KVStorage<String, String> (path, new StringSerialization(), new StringSerialization());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new KVStorage<Integer, Double>(path, new IntSerialization(), new DoubleSerialization());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new KVStorage<StudentKey, Student> (path, new StudentKeySerialization(),
                                                    new StudentSerialization());
    }
}
