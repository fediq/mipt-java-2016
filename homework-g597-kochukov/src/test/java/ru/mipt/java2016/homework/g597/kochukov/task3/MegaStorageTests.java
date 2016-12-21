package ru.mipt.java2016.homework.g597.kochukov.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g597.kochukov.task3.MegaStudentSerializer;
import ru.mipt.java2016.homework.g597.kochukov.task3.MegaSerializerImpl;
import ru.mipt.java2016.homework.g597.kochukov.task3.MegaStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public class MegaStorageTests extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            return new MegaStorage<String, String>(
                    path,
                    new MegaSerializerImpl.StringSerializer(),
                    new MegaSerializerImpl.StringSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            return new MegaStorage<Integer, Double>(
                    path,
                    new MegaSerializerImpl.IntegerSerializer(),
                    new MegaSerializerImpl.DoubleSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            return new MegaStorage<StudentKey, Student>(
                    path,
                    new MegaStudentSerializer.StudentKeySerializer(),
                    new MegaStudentSerializer.StudentSerializer()
            );
        } catch (IOException error) {
            System.out.println("can't build Storage");
        }

        return null;
    }

    /*@Test
    @Ignore
    @Override
    public void measure100kWDump100kR() {
        super.measure100kWDump100kR();
    }*/
}