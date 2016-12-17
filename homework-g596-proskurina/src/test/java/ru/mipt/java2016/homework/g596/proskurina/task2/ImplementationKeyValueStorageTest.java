package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;

public class ImplementationKeyValueStorageTest extends AbstractSingleFileStorageTest {

        @Override
        protected ru.mipt.java2016.homework.base.task2.KeyValueStorage buildStringsStorage(String path) {
            return new ImplementationKeyValueStorage<>("String", "String",
                                    new SerialiseString(), new SerialiseString(),
                                    path) ;
        }

        @Override
        protected ru.mipt.java2016.homework.base.task2.KeyValueStorage buildNumbersStorage(String path) {
            return new ImplementationKeyValueStorage<>("Integer", "Double",
                    new SerialiseInteger(), new SerialiseDouble(),
                    path) ;
        }

        @Override
        protected ru.mipt.java2016.homework.base.task2.KeyValueStorage buildPojoStorage(String path) {
            return new ImplementationKeyValueStorage<>("StudentKey", "Student",
                    new SerialiseStudentKey(), new SerialiseStudent(),
                    path) ;
        }

    }


