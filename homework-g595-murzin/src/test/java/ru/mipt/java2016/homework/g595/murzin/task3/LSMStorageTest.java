package ru.mipt.java2016.homework.g595.murzin.task3;

import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.g595.murzin.task2.SimpleKeyValueStorage;
import ru.mipt.java2016.homework.g595.murzin.task2.SimpleSingleFileStorageTest;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.assertFullyMatch;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;

/**
 * Created by Дмитрий Мурзин on 05.11.16.
 */
public class LSMStorageTest extends SimpleSingleFileStorageTest {

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) {
        return new LSMStorage<>(path,
                SerializationStrategy.FOR_STRING,
                SerializationStrategy.FOR_STRING,
                String::compareTo);
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) {
        return new LSMStorage<>(path,
                SerializationStrategy.FOR_INTEGER,
                SerializationStrategy.FOR_DOUBLE,
                Integer::compareTo);
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) {
        return new SimpleKeyValueStorage<>(path,
                FOR_STUDENT_KEY,
                FOR_STUDENT);
    }

    protected KeyValueStorage<Integer, Integer> buildIntsStorage(String path) {
        return new LSMStorage<>(path,
                SerializationStrategy.FOR_INTEGER,
                SerializationStrategy.FOR_INTEGER,
                Integer::compareTo);
    }

    protected final KeyValueStorage<Integer, Integer> doWithInts(
            String path, StorageTestUtils.Callback<KeyValueStorage<Integer, Integer>> callback) throws Exception {
        return storageCallback(path, callback, this::buildIntsStorage);
    }

    @Test
    public void testWrite() throws Exception {
        doInTempDirectory(path -> {
            HashSet<Integer> keys = new HashSet<>();
            doWithInts(path, storage -> {
                Random random = new Random();
                for (int i = 0; i < 1000; i++) {
//                    int key = random.nextInt();
                    int key = i;
                    storage.write(key, getValueFromKey(key));
                    keys.add(key);
                }
            });
            doWithInts(path, storage -> {
                assertEquals(storage.size(), 1000);
                assertFullyMatch(storage.readKeys(), keys);
                for (int i = 0; i < 1000; i++) {
                    storage.read(i);
                }
            });
        });
    }

    private int getValueFromKey(int key) {
//        return (int) (key * 2654435761L);
        return key;
    }

    @Test
    public void memoryTest0() throws Exception {
        doInTempDirectory(path -> {
            doWithStrings(path, storage -> {
                Random random = new Random();
                char[] key = getRandomString(50, random).toCharArray();
                char[] value = getRandomString(50000, random).toCharArray();
                for (int i = 0; i < 100; i++) {
                    storage.write(new String(key), new String(value));
                    key[i % key.length] = (char) ('0' + (key[i % key.length] - '0' + 1) % 10);
                    value[i % value.length] = (char) ('0' + (value[i % value.length] - '0' + 1) % 10);
                }
            });
        });
    }

    @Test
    public void memoryTest() throws Exception {
        doInTempDirectory(path -> {
            doWithStrings(path, storage -> {
                int numberOperations = 100;
                int maxKey = numberOperations / 10;
                ArrayList<String> keys = new ArrayList<>(numberOperations);
                Random random = new Random();
                for (int i = 0; i < numberOperations; i++) {
                    int type = random.nextInt() % 100;
                    if (0 <= type && type < 50 || keys.isEmpty()) {
                        // write
                        String key = getRandomString(10, random);
                        String value = getRandomString(100000, random);
                        storage.write(key, value);
                        keys.add(key);
                    } else if (50 <= type && type < 85) {
                        // read
                        String key = keys.get(random.nextInt(keys.size()));
                        String value = storage.read(key);
//                        assertEquals(value, ...);
                    } else if (85 <= type && type < 100) {
                        // delete
                        int index = random.nextInt(keys.size());
                        String key = keys.get(index);
                        storage.delete(key);
                        keys.set(index, keys.get(keys.size() - 1));
                        keys.remove(keys.size() - 1);
                    }
                }
            });
        });
    }

    private String getRandomString(int approximateLenth, Random random) {
        double length = approximateLenth * (random.nextFloat() + 1);
        // 2^x = 10^length
        int x = (int) (length * Math.log(10) / Math.log(2));
        return new BigInteger(x, random).toString();
    }
}