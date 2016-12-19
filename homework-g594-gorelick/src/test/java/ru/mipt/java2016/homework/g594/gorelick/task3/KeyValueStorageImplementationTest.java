package ru.mipt.java2016.homework.g594.gorelick.task3;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

public class KeyValueStorageImplementationTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        try {
            KeyValueStorageImplementation test = new KeyValueStorageImplementation<>(path,
                    new StringFileWorker(), new StringFileWorker());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        try {
            KeyValueStorageImplementation test = new KeyValueStorageImplementation<>(path,
                    new IntegerFileWorker(), new DoubleFileWorker());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        try {
            KeyValueStorageImplementation test = new KeyValueStorageImplementation<>(path,
                    new StudentKeyFileWorker(), new StudentFileWorker());
            return test;
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }

    @Test(expected = Exception.class)
    public void testLockFile() throws IOException {
        int step = 0;
        try {
            StringFileWorker stringFileWorker = new StringFileWorker();
            KeyValueStorageImplementation<String, String> firstInstance =
                    new KeyValueStorageImplementation<String, String>("/home/alex", stringFileWorker, stringFileWorker);
            System.out.println("First instance created...");
            step = 1;
            KeyValueStorageImplementation<String, String> secondInstance = new KeyValueStorageImplementation<String, String>("/home/alex", stringFileWorker, stringFileWorker);
            System.out.println("Second instance created...");
            step = 2;
        } catch (Exception e) {
            if (step == 1) {
                System.out.println("Fail on creating second instance.");
            }
            if (step == 0) {
                System.out.println("Fail on creating first instance");
            }
            System.out.println(e.getMessage());
            throw e;
        }
        throw new AssertionError("You can have two database instances on one file");
    }
}
