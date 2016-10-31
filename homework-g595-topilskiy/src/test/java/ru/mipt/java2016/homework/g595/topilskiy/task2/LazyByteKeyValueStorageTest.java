package ru.mipt.java2016.homework.g595.topilskiy.task2;

import org.junit.Test;
import static org.junit.Assert.*;

import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.DoubleSerializer;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.StudentSerializer;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * @author Artem K. Topilskiy
 * @since 28.10.16
 */
public class LazyByteKeyValueStorageTest extends AbstractSingleFileStorageTest {
    @Test
    public void testSerializers() {
        final DoubleSerializer doubleSerializer  = new DoubleSerializer();
        final double DOUBLE_CONST = 981237518.234123;
        final double EPSILON = 0.001;
        double deserializeSerializedDoubleConst = doubleSerializer.deserialize(
                                                  doubleSerializer.serialize(DOUBLE_CONST));
        assertEquals(DOUBLE_CONST, deserializeSerializedDoubleConst, EPSILON);


        final StudentSerializer studentSerializer = new StudentSerializer();
        Student deserializeSerializedStudentValue1 = studentSerializer.deserialize(
                                                     studentSerializer.serialize(VALUE_1));
        assertEquals(VALUE_1, deserializeSerializedStudentValue1);
    }


    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        LazyByteKeyValueStorageInfo storageInfoInit =
                new LazyByteKeyValueStorageInfo(path, "String", "String");
        try {
            return new LazyByteKeyValueStorage<>(storageInfoInit);
        } catch (IOException discardException) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        LazyByteKeyValueStorageInfo storageInfoInit =
                new LazyByteKeyValueStorageInfo(path, "Integer", "Double");

        try {
            return new LazyByteKeyValueStorage<>(storageInfoInit);
        } catch (IOException discardException) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        LazyByteKeyValueStorageInfo storageInfoInit =
                new LazyByteKeyValueStorageInfo(path, "StudentKey", "Student");

        try {
            return new LazyByteKeyValueStorage<>(storageInfoInit);
        } catch (IOException discardException) {
            return null;
        }
    }
}
