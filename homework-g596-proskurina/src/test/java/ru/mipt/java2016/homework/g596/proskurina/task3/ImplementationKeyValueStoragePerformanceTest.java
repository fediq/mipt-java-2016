package ru.mipt.java2016.homework.g596.proskurina.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g596.proskurina.task2.*;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

/**
 * Created by Lenovo on 17.12.2016.
 */
public class ImplementationKeyValueStoragePerformanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage buildStringsStorage(String path) {
        return new ImplementationKeyValueStorage<>(new SerialiseString(), new SerialiseString(),
                path) ;
    }

    @Override
    protected KeyValueStorage buildNumbersStorage(String path) {
        return new ImplementationKeyValueStorage<>(new SerialiseInteger(), new SerialiseDouble(),
                path) ;
    }

    @Override
    protected KeyValueStorage buildPojoStorage(String path) {
        return new ImplementationKeyValueStorage<>(new SerialiseStudentKey(), new SerialiseStudent(),
                path) ;
    }

}
