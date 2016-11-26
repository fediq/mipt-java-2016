package ru.mipt.java2016.homework.g597.shirokova.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

public class StorageTest extends AbstractSingleFileStorageTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        MyStorage<String, String> currentStorage = null;
        try {
            currentStorage = new MyStorage<>(path,
                    new ConcreteSerializationStrategy.StringConcreteStrategy(),
                    new ConcreteSerializationStrategy.StringConcreteStrategy()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentStorage;
    }


    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        MyStorage<Integer, Double> currentStorage = null;
        try {
            currentStorage = new MyStorage<>(path,
                    new ConcreteSerializationStrategy.IntegerConcreteStrategy(),
                    new ConcreteSerializationStrategy.DoubleConcreteStrategy()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentStorage;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        MyStorage<StudentKey, Student> currentStorage = null;
        try {
            currentStorage = new MyStorage<>(path,
                    new ConcreteSerializationStrategy.StudentKeyConcreteStrategy(),
                    new ConcreteSerializationStrategy.StudentConcreteStrategy()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return currentStorage;
    }
}
