package ru.mipt.java2016.homework.g595.romanenko.task3;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.romanenko.task2.StudentKeySerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.StudentSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.DoubleSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.IntegerSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task2.serialization.StringSerializer;
import ru.mipt.java2016.homework.g595.romanenko.task3.comapators.StudentKeyComparator;
import ru.mipt.java2016.homework.g595.romanenko.utils.FileDigitalSignatureAdler32;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomKey;
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
                new MergerSST<>(String::compareTo)
        );

        return result;
    }


    protected KeyValueStorage<String, String> buildStringsStorage(String path, int cached, int updated) {
        Storage<String, String> result = new Storage<>(
                path,
                StringSerializer.getInstance(),
                StringSerializer.getInstance(),
                FileDigitalSignatureAdler32.getInstance(),
                new MergerSST<>(String::compareTo),
                cached,
                updated
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
                new MergerSST<>(Integer::compareTo)
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
                new MergerSST<>(new StudentKeyComparator())
        );
        return result;
    }


    @Test
    @Ignore
    public void measure10W20Rx1k_myTest() {
        for (int cached = 100; cached > 0; cached -= 10) {
            for (int updated = 1700; updated < 3000; updated += 200) {
                System.out.println("Cached : " + cached + " Updated : " + updated);
                measure10W20Rx1k_my(cached, updated);
                System.out.println();
                System.out.println();
                System.out.println();
            }
        }
    }

    @Test
    @Ignore
    public void measure10W20Rx1kRepeat() {
        for (int i = 0; i < 20; i++) {
            System.out.println("Iteration " + i);
            measure10W20Rx1k();
            System.out.println();
            System.out.println();
        }
    }


    @SuppressWarnings("Duplicates")
    public void measure10W20Rx1k_my(int cached, int updated) {
        AtomicLong summaryWriteTime = new AtomicLong(0L);
        AtomicLong summaryReadTime = new AtomicLong(0L);
        long beginTime = System.currentTimeMillis();
        for (int t = 0; t < 10; ++t) {
            StorageTestUtils.doInTempDirectory(path -> {
                KeyValueStorage<String, String> storage = null;
                try {
                    {
                        storage = buildStringsStorage(path, cached, updated);
                        Random writeRandom = new Random(42);
                        Random readRandom = new Random(42);
                        for (int generation = 0; generation < 1000; ++generation) {
                            KeyValueStorage<String, String> finalStorage = storage;
                            long writeTime = StorageTestUtils.measureTime(() -> {
                                for (int i = 0; i < 10; ++i) {
                                    String key = randomKey(writeRandom);
                                    String value = randomValue(writeRandom);
                                    finalStorage.write(key, value);
                                }
                            });
                            summaryWriteTime.addAndGet(writeTime);

                            if (generation > 0) {
                                KeyValueStorage<String, String> finalStorage1 = storage;
                                long readTime = StorageTestUtils.measureTime(() -> {
                                    for (int i = 0; i < 20; ++i) {
                                        String key = randomKey(readRandom);
                                        String value = randomValue(readRandom); // Not used, just to fit load balance
                                        Assert.assertNotNull(finalStorage1.read(key));
                                    }
                                });
                                summaryReadTime.addAndGet(readTime);
                                if (Arrays.asList(1, 3, 7, 15, 31, 63, 127, 255, 511).contains(generation)) {
                                    readRandom.setSeed(42);
                                }
                            }
                        }
                    }
                } finally {
                    storage.close();
                }
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
    @Ignore
    @Override
    public void measure100kWDump100kR() {
        super.measure100kWDump100kR();
    }
/*
    @Test
    public void testOne() {
        doInTempDirectory(path -> doWithNumbers(path, storage -> {
            for (int i = 0; i < 3 * chunkSize; i++) {
                storage.write(0, 1e-7);
            }
            assertEquals(storage.size(), 1);
            Set<Integer> keys = new HashSet<>();
            keys.add(0);
            assertFullyMatch(storage.readKeys(), keys);
        }));
    }

    @Test
    public void testBigAmountWrites() {
        int totalAmount = 50 * chunkSize;
        Random random = new Random();
        Map<Integer, Double> mp = new HashMap<>();
        doInTempDirectory(path -> doWithNumbers(path, storage -> {
            for (int i = 0; i < totalAmount; i++) {
                Integer key = random.nextInt();
                Double value = random.nextDouble();
                mp.put(key, value);
                storage.write(key, value);
            }
            assertEquals(storage.size(), mp.size());
            assertFullyMatch(storage.readKeys(), mp.keySet());
        }));
    }

    @Test
    public void testSameKeys() {
        int mod = 50;
        Random random = new Random(chunkSize * mod);
        int totalAmount = 20 * mod;
        Map<Integer, Double> mp = new HashMap<>();

        doInTempDirectory(path -> {
            doWithNumbers(path, storage -> {
                for (int i = 0; i < totalAmount; i++) {
                    Integer key = i % mod;
                    Double value = random.nextDouble();
                    mp.put(key, value);
                    storage.write(key, value);
                }
                assertFullyMatch(storage.readKeys(), mp.keySet());
                for (Map.Entry<Integer, Double> entry : mp.entrySet()) {
                    assertEquals(entry.getValue(), storage.read(entry.getKey()));
                }
            });
        });
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testEraseKeys() {
        int totalAmount = 50 * chunkSize;//* 2048;
        Random random = new Random(chunkSize);
        Map<Integer, Double> mp = new HashMap<>();
        List<Integer> keys = new ArrayList<>();

        doInTempDirectory(path -> {
            doWithNumbers(path, storage -> {
                for (int i = 0; i < totalAmount; i++) {
                    Integer key = random.nextInt();
                    Double value = random.nextDouble();
                    if (!mp.containsKey(key)) {
                        keys.add(key);
                    }
                    mp.put(key, value);
                    storage.write(key, value);
                }
                assertFullyMatch(storage.readKeys(), mp.keySet());
            });
            keys.sort(Integer::compareTo);
            doWithNumbers(path, storage -> {
                for (int i = 0; i < chunkSize * 2; i++) {
                    Integer key = keys.get(Math.abs(random.nextInt()) % keys.size());
                    mp.remove(key);
                    storage.delete(key);
                    keys.remove(key);
                }
                assertFullyMatch(storage.readKeys(), mp.keySet());
            });
        });
    }

    @SuppressWarnings("Duplicates")
    @Test
    public void testBigAmountWritesPersistent() {
        int totalAmount = 50 * chunkSize;//* 2048;
        Random random = new Random(chunkSize);
        Map<Integer, Double> mp = new HashMap<>();
        List<Integer> keys = new ArrayList<>();

        doInTempDirectory(path -> {
            doWithNumbers(path, storage -> {
                for (int i = 0; i < totalAmount; i++) {
                    Integer key = random.nextInt();
                    Double value = random.nextDouble();
                    if (!mp.containsKey(key)) {
                        keys.add(key);
                    }
                    mp.put(key, value);
                    storage.write(key, value);
                }
                assertFullyMatch(storage.readKeys(), mp.keySet());
            });
            keys.sort(Integer::compareTo);

            doWithNumbers(path, storage -> {
                assertEquals(mp.size(), storage.size());
                assertFullyMatch(storage.readKeys(), mp.keySet());

                for (int i = 0; i < totalAmount; i++) {
                    Integer key = keys.get(Math.abs(random.nextInt()) % keys.size());
                    Double value = random.nextDouble();
                    mp.put(key, value);
                    storage.write(key, value);
                }
                assertFullyMatch(storage.readKeys(), mp.keySet());

                for (Map.Entry<Integer, Double> entry : mp.entrySet()) {
                    assertEquals(entry.getValue(), storage.read(entry.getKey()));
                }
            });
        });
    }

    @Test
    public void testVeryBigAmountWrites() {
        int totalAmount = 100 * chunkSize; // 500 * 2048 ~ 10^6
        // only for speed check
        // MD5 hash runs more than 10 second quickly than RSA
        doInTempDirectory(path -> doWithNumbers(path, storage -> {
            for (int i = 0; i < totalAmount; i++) {
                Integer key = totalAmount - i;
                Double value = i / (double) totalAmount;
                storage.write(key, value);
            }
        }));
    }*/
}
