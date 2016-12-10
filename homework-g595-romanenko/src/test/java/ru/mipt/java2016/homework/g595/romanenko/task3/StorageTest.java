package ru.mipt.java2016.homework.g595.romanenko.task3;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.romanenko.task2.StudentKeySerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.StudentSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.DoubleSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.StringSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task3.comapators.StudentKeyComparator;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignatureAdler32;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;
import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomValue;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task3
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class StorageTest extends KeyValueStoragePerformanceTest {
    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        Storage<String, String> result = new Storage<>(
                path,
                StringSerializer.getInstance(),
                StringSerializer.getInstance(),
                FileDigitalSignatureAdler32.getInstance(),
                String::compareTo
        );

        return result;
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        Storage<Integer, Double> result = new Storage<>(
                path,
                IntegerSerializer.getInstance(),
                DoubleSerializer.getInstance(),
                FileDigitalSignatureAdler32.getInstance(),
                Integer::compareTo
        );

        return result;
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        Storage<StudentKey, Student> result = new Storage<>(
                path,
                StudentKeySerializer.getInstance(),
                StudentSerializer.getInstance(),
                FileDigitalSignatureAdler32.getInstance(),
                new StudentKeyComparator()
        );
        return result;
    }


    @Test
    public void testOne() {
        doInTempDirectory(path -> doWithStrings(path, storage -> {
            Random random = new Random(42);
            for (int i = 0; i < 5000; i++) {
                String key = Integer.toString(i);
                String value = randomValue(random);
                storage.write(key, value);
            }
            assertEquals(storage.size(), 5000);

            random.setSeed(42);
            for (int i = 0; i < 5000; i++) {
                assertEquals(storage.read(Integer.toString(i)), randomValue(random));
            }
        }));
    }
}
