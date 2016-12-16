package ru.mipt.java2016.homework.g594.shevkunov.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.g594.shevkunov.task2.BinarySerializator;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomKey;
import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomValue;

/**
 * Tester for task3
 * Created by shevkunov on 14.11.16.
 */
public class NobodyReadNamesKeyValueStorageTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        try {
            return new LazyMergedKeyValueStorage<>(new BinarySerializator<String>("String"),
                    new BinarySerializator<String>("String"), path, 0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        try {
            return new LazyMergedKeyValueStorage<>(new BinarySerializator<Integer>("Integer"),
                    new BinarySerializator<Double>("Double"), path, 0);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        try {
            return new LazyMergedKeyValueStorage<>(new BinarySerializator<StudentKey>("StudentKey"),
                    new BinarySerializator<Student>("Student"), path, 0);
        } catch (Exception e) {
            return null;
        }
    }

    ///measure10kWDump10kR works 9.966 ms and measure10kWAnd10kD works 15.558 ms
    @Ignore
    @Test
    public void measure10kWAnd10kD() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryDeleteTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random writeRandom = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 10000; ++i) {
                            String key = randomKey(writeRandom);
                            String value = randomValue(writeRandom);
                            storage.write(key, value);
                        }
                    });
                    summaryWriteTime.addAndGet(writeTime);

                    Random deleteRandom = new Random(42);
                    long deleteTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 10000; ++i) {
                            String key = randomKey(deleteRandom);
                            String value = randomValue(deleteRandom); // for balanced randoms
                            storage.delete(key);
                        }
                    });
                    summaryDeleteTime.addAndGet(deleteTime);



                });
            });
        }
        long endTime = System.currentTimeMillis();
        long iterationTimeMillis = (endTime - beginTime) / 10;
        long writesPerSecond = 10 * 10000 * 1000 / summaryWriteTime.get();
        long deletesPerSecond = 10 * 10000 * 1000 / summaryDeleteTime.get();

        print("%5d Writes per second up to 10k in test", writesPerSecond);
        print("%5d Deletes per second up to 10k in test", deletesPerSecond);
        print("%5d millis for single 10kW 10kD", iterationTimeMillis);
    }

}
