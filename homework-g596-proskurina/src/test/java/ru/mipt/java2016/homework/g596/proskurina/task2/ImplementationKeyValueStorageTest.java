package ru.mipt.java2016.homework.g596.proskurina.task2;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class ImplementationKeyValueStorageTest extends AbstractSingleFileStorageTest {

        @Override
        protected KeyValueStorage<String, String> buildStringsStorage(String path) {
            return new ImplementationKeyValueStorage<>("String", "String",
                                    new SerialiseString(), new SerialiseString(),
                                    path) ;
        }

        @Override
        protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
            return new ImplementationKeyValueStorage<>("Integer", "Double",
                    new SerialiseInteger(), new SerialiseDouble(),
                    path) ;
        }

        @Override
        protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
            return new ImplementationKeyValueStorage<>("StudentKey", "Student",
                    new SerialiseStudentKey(), new SerialiseStudent(),
                    path) ;
        }

    }


