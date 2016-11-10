package ru.mipt.java2016.homework.tests.task3;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public abstract class KeyValueStoragePerformanceTest extends AbstractSingleFileStorageTest {
    private static final int KEY_SIZE = 64;
    private static final int VALUE_SIZE = 8192;
    private static final int ITERATIONS = 10;

    public static String randomKey(Random random) {
        byte[] bytes = new byte[KEY_SIZE];
        random.nextBytes(bytes);
        return new String(bytes);
    }

    public static String randomValue(Random random) {
        byte[] bytes = new byte[VALUE_SIZE];
        byte[] seed = new byte[KEY_SIZE];
        random.nextBytes(seed);
        System.arraycopy(seed, 0, bytes, 0, seed.length);
        int allocated = seed.length;
        while (allocated < VALUE_SIZE) {
            System.arraycopy(bytes, 0, bytes, allocated, allocated);
            allocated *= 2;
        }
        return new String(bytes);
    }

    @Test
    public void measure100RWNoFlush() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        for (int t = 0; t < ITERATIONS; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 100; ++i) {
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
                        for (int i = 0; i < 100; ++i) {
                            String key = randomKey(random);
                            String value = randomValue(random);
                            Assert.assertEquals(value, storage.read(key));
                        }
                    });
                    summaryReadTime.addAndGet(readTime);
                });
            });
        }
        long writeTime = summaryWriteTime.get() / ITERATIONS;
        long readTime = summaryReadTime.get() / ITERATIONS;
        System.out.println("Average write time: " + writeTime);
        System.out.println("Average read time:  " + readTime);
    }
}
