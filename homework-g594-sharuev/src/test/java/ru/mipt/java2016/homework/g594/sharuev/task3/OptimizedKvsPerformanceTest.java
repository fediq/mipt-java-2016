package ru.mipt.java2016.homework.g594.sharuev.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

public class OptimizedKvsPerformanceTest extends KeyValueStoragePerformanceTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(
            String path) throws MalformedDataException {
        return new FSOptimizedKVS<String, String>(path,
                new StringSerializer(),
                new StringSerializer(), 0);
        /*new OptimizedKvs<String, String>(path,
                new PojoChannelSerializer<String>(String.class), new PojoChannelSerializer<String>(String.class),
                new POJOComparator<>(String.class))*/
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(
            String path) throws MalformedDataException {
        return new FSOptimizedKVS<Integer, Double>(path,
                new PojoSerializer<Integer>(Integer.class),
                new PojoSerializer<Double>(Double.class), 0);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(
            String path) throws MalformedDataException {
        return new FSOptimizedKVS<StudentKey, Student>(path,
                new PojoSerializer<StudentKey>(StudentKey.class),
                new PojoSerializer<Student>(Student.class), 0);
    }

    /*@Test
    @Override
    @Ignore
    public void measure100kWDump100kR() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            System.out.print(t+"\n");
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 100000; ++i) {
                            if (i % 10000 == 0)
                                System.out.print(i+"\n");
                            String key = randomKey(random);
                            String value = randomValue(random);
                            storage.write(key, value);
                        }
                    });
                    summaryWriteTime.addAndGet(writeTime);
                });

                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long readTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 100000; ++i) {
                            String key = randomKey(random);
                            String value = randomValue(random);
                            Assert.assertEquals(value, storage.read(key));
                        }
                    });
                    summaryReadTime.addAndGet(readTime);
                });
            });
        }
        long endTime = System.currentTimeMillis();
        long iterationTimeMillis = (endTime - beginTime) / 10;
        long readsPerSecond = 10 * 100000 * 1000 / summaryReadTime.get();
        long writesPerSecond = 10 * 100000 * 1000 / summaryWriteTime.get();

        print("%5d Writes per second up to 100k", writesPerSecond);
        print("%5d Reads per second from 100k", readsPerSecond);
        print("%5d millis for single 100kW 100kR iteration", iterationTimeMillis);
    }*/
}
