package ru.mipt.java2016.homework.g595.romanenko.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.*;
import ru.mipt.java2016.homework.g595.romanenko.task3.comapators.StudentKeyComparator;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task3
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class StorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        Storage<String, String> result = new Storage<>(
                path,
                StringSerializer.getInstance(),
                StringSerializer.getInstance(),
                new MergerSST<>(String::compareTo)
        );

        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        Storage<Integer, Double> result = new Storage<>(
                path,
                IntegerSerializer.getInstance(),
                DoubleSerializer.getInstance(),
                new MergerSST<>(Integer::compareTo)
        );

        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        Storage<StudentKey, Student> result = new Storage<>(
                path,
                StudentKeySerializer.getInstance(),
                StudentSerializer.getInstance(),
                new MergerSST<>(new StudentKeyComparator())
        );
        return result;
    }
}
