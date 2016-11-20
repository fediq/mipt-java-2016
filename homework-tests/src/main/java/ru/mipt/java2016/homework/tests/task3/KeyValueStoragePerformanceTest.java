package ru.mipt.java2016.homework.tests.task3;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomKey;
import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomValue;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public abstract class KeyValueStoragePerformanceTest extends AbstractSingleFileStorageTest {

    protected final void print(String s, Object... args) {
        System.out.println(String.format("PERFORMANCE: " + s + " (for [" + getClass().getName() + "])", args));
    }

    @Test
    public void measure100WDump100R() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
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
        long endTime = System.currentTimeMillis();
        long iterationTimeMillis = (endTime - beginTime) / 10;
        long readsPerSecond = 10 * 100 * 1000 / summaryReadTime.get();
        long writesPerSecond = 10 * 100 * 1000 / summaryWriteTime.get();

        print("%5d Writes per second up to 100", writesPerSecond);
        print("%5d Reads per second from 100", readsPerSecond);
        print("%5d millis for 100W 100R iteration", iterationTimeMillis);
    }

    @Test
    public void measure1kWDump1kR() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 1000; ++i) {
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
                        for (int i = 0; i < 1000; ++i) {
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
        long readsPerSecond = 10 * 1000 * 1000 / summaryReadTime.get();
        long writesPerSecond = 10 * 1000 * 1000 / summaryWriteTime.get();

        print("%5d Writes per second up to 1k", writesPerSecond);
        print("%5d Reads per second from 1k", readsPerSecond);
        print("%5d millis for single 1kW 1kR iteration", iterationTimeMillis);
    }

    @Test
    public void measure10kWDump10kR() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 10000; ++i) {
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
                        for (int i = 0; i < 10000; ++i) {
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
        long readsPerSecond = 10 * 10000 * 1000 / summaryReadTime.get();
        long writesPerSecond = 10 * 10000 * 1000 / summaryWriteTime.get();

        print("%5d Writes per second up to 10k", writesPerSecond);
        print("%5d Reads per second from 10k", readsPerSecond);
        print("%5d millis for single 10kW 10kR iteration", iterationTimeMillis);
    }

    // Вы можете пометить этот тест как @Ignored во время отладки, если он занимает у вас слишком много времени
    @Test
    public void measure100kWDump100kR() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < 100000; ++i) {
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
    }


    @Test
    public void measure10W20Rx1k() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random writeRandom = new Random(42);
                    Random readRandom = new Random(42);
                    for (int generation = 0; generation < 1000; ++generation) {
                        long writeTime = StorageTestUtils.measureTime(() -> {
                            for (int i = 0; i < 10; ++i) {
                                String key = randomKey(writeRandom);
                                String value = randomValue(writeRandom);
                                storage.write(key, value);
                            }
                        });
                        summaryWriteTime.addAndGet(writeTime);

                        if (generation > 0) {
                            long readTime = StorageTestUtils.measureTime(() -> {
                                for (int i = 0; i < 20; ++i) {
                                    String key = randomKey(readRandom);
                                    String value = randomValue(readRandom); // Not used, just to fit load balance
                                    Assert.assertNotNull(storage.read(key));
                                }
                            });
                            summaryReadTime.addAndGet(readTime);
                            if (Arrays.asList(1, 3, 7, 15, 31, 63, 127, 255, 511).contains(generation)) {
                                readRandom.setSeed(42);
                            }
                        }
                    }
                });
            });
        }
        long endTime = System.currentTimeMillis();
        long iterationTimeMillis = (endTime - beginTime) / 10;
        long readsPerSecond = 10 * (1000 - 1) * 20 * 1000 / summaryReadTime.get();
        long writesPerSecond = 10 * 1000 * 10 * 1000 / summaryWriteTime.get();

        print("%5d Writes per second in mix 10W 20R", writesPerSecond);
        print("%5d Reads per second in mix 10W 20R", readsPerSecond);
        print("%5d millis for single 10W 20R x1k iteration", iterationTimeMillis);
    }

    @Test
    public void measure10kWAnd100Rx100() {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
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

                    for (int cycle = 0; cycle < 100; ++cycle) {
                        Random readRandom = new Random(42);
                        long readTime = StorageTestUtils.measureTime(() -> {
                            for (int i = 0; i < 100; ++i) {
                                String key = randomKey(readRandom);
                                String value = randomValue(readRandom); // Not used, just to fit load balance
                                Assert.assertNotNull(storage.read(key));
                            }
                        });
                        summaryReadTime.addAndGet(readTime);
                    }
                });
            });
        }
        long endTime = System.currentTimeMillis();
        long iterationTimeMillis = (endTime - beginTime) / 10;
        long readsPerSecond = 10 * 100 * 100 * 1000 / summaryReadTime.get();
        long writesPerSecond = 10 * 10000 * 1000 / summaryWriteTime.get();

        print("%5d Writes per second up to 10k in test", writesPerSecond);
        print("%5d Reads per second in 100-positions cycle", readsPerSecond);
        print("%5d millis for single 10kW 100Rx100", iterationTimeMillis);
    }
}
