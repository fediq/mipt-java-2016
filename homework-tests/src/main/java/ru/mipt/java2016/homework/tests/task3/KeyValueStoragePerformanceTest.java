package ru.mipt.java2016.homework.tests.task3;

import org.junit.Assert;
import org.junit.Test;
import ru.mipt.java2016.homework.tests.task2.AbstractSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomKey;
import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomValue;

/**
 * @author Fedor S. Lavrentyev
 * @since 02.11.16
 */
public abstract class KeyValueStoragePerformanceTest extends AbstractSingleFileStorageTest {

    public static final Set<Integer> TERMINAL_GENERATIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(1, 3, 7, 15, 31, 63, 127, 255, 511, 1023)));

    protected final void print(String s, Object... args) {
        System.out.println(String.format("PERFORMANCE: " + s + " @ " + getClass().getName() + "", args));
    }

    protected void innerMeasureWriteDumpRead(int size, int iterations) {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        AtomicLong summaryCloseTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < iterations; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                final AtomicLong writeMomentTimestampHolder = new AtomicLong(0);
                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < size; ++i) {
                            String key = randomKey(random);
                            String value = randomValue(random);
                            storage.write(key, value);
                        }
                    });
                    summaryWriteTime.addAndGet(writeTime);
                    writeMomentTimestampHolder.set(System.currentTimeMillis());
                });
                summaryCloseTime.addAndGet(System.currentTimeMillis() - writeMomentTimestampHolder.get());

                doWithStrings(path, storage -> {
                    Random random = new Random(42);
                    long readTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < size; ++i) {
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

        long fullTime = endTime - beginTime;
        int readsCount = iterations * size;
        int writesCount = iterations * size;
        long averageOpsPerSecond = (writesCount + readsCount) * 1000 / fullTime;

        String label = String.format("WriteDumpRead-%d", size);
        print("%s: %5d average ops per second", label, averageOpsPerSecond);
    }

    protected void innerMeasureWriteReadChain(int batchSize, int loops, int iterations) {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < iterations; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random writeRandom = new Random(42);
                    Random readRandom = new Random(42);
                    for (int generation = 0; generation < loops; ++generation) {
                        long writeTime = StorageTestUtils.measureTime(() -> {
                            for (int i = 0; i < batchSize; ++i) {
                                String key = randomKey(writeRandom);
                                String value = randomValue(writeRandom);
                                storage.write(key, value);
                            }
                        });
                        summaryWriteTime.addAndGet(writeTime);

                        if (generation > 0) {
                            long readTime = StorageTestUtils.measureTime(() -> {
                                for (int i = 0; i < 2 * batchSize; ++i) {
                                    String key = randomKey(readRandom);
                                    String value = randomValue(readRandom); // Not used, just to fit load balance
                                    Assert.assertNotNull(storage.read(key));
                                }
                            });
                            summaryReadTime.addAndGet(readTime);
                            if (TERMINAL_GENERATIONS.contains(generation)) {
                                readRandom.setSeed(42);
                            }
                        }
                    }
                });
            });
        }
        long endTime = System.currentTimeMillis();

        long fullTime = endTime - beginTime;
        int readsCount = iterations * (loops - 1) * 2 * batchSize;
        int writesCount = iterations * loops * batchSize;
        long averageOpsPerSecond = (writesCount + readsCount) * 1000 / fullTime;

        String label = String.format("WriteReadChain-%dW%dRx%d", batchSize, 2 * batchSize, loops);
        print("%s: %5d average ops per second", label, averageOpsPerSecond);
    }

    public void innerMeasureWriteAndReadBack(int writeSize, int readSize, int readLoops, int iterations) {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < iterations; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                doWithStrings(path, storage -> {
                    Random writeRandom = new Random(42);

                    long writeTime = StorageTestUtils.measureTime(() -> {
                        for (int i = 0; i < writeSize; ++i) {
                            String key = randomKey(writeRandom);
                            String value = randomValue(writeRandom);
                            storage.write(key, value);
                        }
                    });
                    summaryWriteTime.addAndGet(writeTime);

                    for (int cycle = 0; cycle < readLoops; ++cycle) {
                        Random readRandom = new Random(42);
                        long readTime = StorageTestUtils.measureTime(() -> {
                            for (int i = 0; i < readSize; ++i) {
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

        long fullTime = endTime - beginTime;
        int readsCount = iterations * readSize * readLoops;
        int writesCount = iterations * writeSize;
        long averageOpsPerSecond = (writesCount + readsCount) * 1000 / fullTime;

        String label = String.format("WriteReadLoop-%dW %dRx%d", writeSize, readSize, readLoops);
        print("%s: %5d average ops per second", label, averageOpsPerSecond);
    }

    @Test
    public void measureWriteDumpRead64k() {
        innerMeasureWriteDumpRead(65536, 1);
    }

    @Test
    public void measureWriteDumpRead16k() {
        innerMeasureWriteDumpRead(16384, 3);
    }

    @Test
    public void measureWriteDumpRead4k() {
        innerMeasureWriteDumpRead(4096, 10);
    }

    @Test
    public void measureWriteDumpRead1k() {
        innerMeasureWriteDumpRead(1024, 10);
    }

    @Test
    public void measureWriteReadChain16W32Rx2k() {
        innerMeasureWriteReadChain(16, 2048, 1);
    }

    @Test
    public void measureWriteReadChain8W16Rx1k() {
        innerMeasureWriteReadChain(8, 1024, 3);
    }

    @Test
    public void measureWriteReadChain4W8Rx512() {
        innerMeasureWriteReadChain(4, 512, 10);
    }

    @Test
    public void measureWriteReadChain2W4Rx256k() {
        innerMeasureWriteReadChain(2, 256, 10);
    }

    @Test
    public void measureWriteAndReadBack64k() {
        innerMeasureWriteAndReadBack(65536, 256, 256, 1);
    }

    @Test
    public void measureWriteAndReadBack16k() {
        innerMeasureWriteAndReadBack(16384, 128, 128, 3);
    }

    @Test
    public void measureWriteAndReadBack4k() {
        innerMeasureWriteAndReadBack(4096, 64, 64, 10);
    }

    @Test
    public void measureWriteAndReadBack1k() {
        innerMeasureWriteAndReadBack(1024, 32, 32, 10);
    }
}
