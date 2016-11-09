package ru.mipt.java2016.homework.g594.petrov.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g594.petrov.task2.SerializeString;

/**
 * Created by philipp on 30.10.16.
 */
public class MegaKeyValueStorageTester extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new MegaKeyValueStorage<>(path, "{String:String}", new SerializeString(), new SerializeString());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new MegaKeyValueStorage<>(path, "{Integer:Double}", new SerializeInteger(), new SerializeDouble());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new MegaKeyValueStorage<>(path, "{StudentKey:Student}",
                new SerializeStudentKey(), new SerializeStudent());
    }
}
