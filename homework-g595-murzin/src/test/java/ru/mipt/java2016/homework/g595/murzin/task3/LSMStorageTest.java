package ru.mipt.java2016.homework.g595.murzin.task3;

import org.junit.Ignore;
import org.junit.Test;
import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.tests.task2.StorageTestUtils;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.assertFullyMatch;
import static ru.mipt.java2016.homework.tests.task2.StorageTestUtils.doInTempDirectory;
import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomKey;
import static ru.mipt.java2016.homework.tests.task3.PerformanceTestUtils.randomValue;

/**
 * Created by Дмитрий Мурзин on 05.11.16.
 */
@Ignore
public class LSMStorageTest extends KeyValueStoragePerformanceTest {

    private static final SerializationStrategy<StudentKey> FOR_STUDENT_KEY = new SerializationStrategy<StudentKey>() {
        @Override
        public void serializeToStream(StudentKey studentKey, DataOutputStream output) throws IOException {
            output.writeInt(studentKey.getGroupId());
            output.writeUTF(studentKey.getName());
        }

        @Override
        public StudentKey deserializeFromStream(DataInputStream input) throws IOException {
            return new StudentKey(input.readInt(), input.readUTF());
        }
    };

    private static final SerializationStrategy<Student> FOR_STUDENT = new SerializationStrategy<Student>() {
        @Override
        public void serializeToStream(Student student, DataOutputStream output) throws IOException {
            output.writeInt(student.getGroupId());
            output.writeUTF(student.getName());
            output.writeUTF(student.getHometown());
            output.writeLong(student.getBirthDate().getTime());
            output.writeBoolean(student.isHasDormitory());
            output.writeDouble(student.getAverageScore());
        }

        @Override
        public Student deserializeFromStream(DataInputStream input) throws IOException {
            return new Student(input.readInt(), input.readUTF(), input.readUTF(), new Date(input.readLong()), input.readBoolean(), input.readDouble());
        }
    };

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
        return new LSMStorage<>(path,
                FOR_STUDENT_KEY,
                FOR_STUDENT,
                StudentKey::compareTo);
    }

    private KeyValueStorage<Integer, Integer> buildIntsStorage(String path) {
        return new LSMStorage<>(path,
                SerializationStrategy.FOR_INTEGER,
                SerializationStrategy.FOR_INTEGER,
                Integer::compareTo);
    }

    private KeyValueStorage<Integer, Integer> doWithInts(
            String path, StorageTestUtils.Callback<KeyValueStorage<Integer, Integer>> callback) throws Exception {
        return storageCallback(path, callback, this::buildIntsStorage);
    }

    @Test
    public void testWrite() throws Exception {
        doInTempDirectory(path -> {
            HashSet<Integer> keys = new HashSet<>();
            int n = 1000;
            doWithInts(path, storage -> {
                Random random = new Random();
                for (int i = 0; i < n; i++) {
                    int key = random.nextInt();
//                    int key = i;
                    storage.write(key, getValueFromKey(key));
                    keys.add(key);
                }
            });
            doWithInts(path, storage -> {
                assertEquals(storage.size(), n);
                assertFullyMatch(storage.readKeys(), keys);
                for (int key : keys) {
                    assertEquals(getValueFromKey(key), (int) storage.read(key));
                }
            });
        });
    }

    private int getValueFromKey(int key) {
        return (int) (key * 2654435761L);
//        return key;
    }

    @Test
    public void memoryTest0() throws Exception {
        doInTempDirectory(path ->
                doWithStrings(path, storage -> {
                    Random random = new Random();
                    char[] key = getRandomString(50, random).toCharArray();
                    char[] value = getRandomString(50000, random).toCharArray();
                    for (int i = 0; i < 100; i++) {
                        storage.write(new String(key), new String(value));
                        key[i % key.length] = (char) ('0' + (key[i % key.length] - '0' + 1) % 10);
                        value[i % value.length] = (char) ('0' + (value[i % value.length] - '0' + 1) % 10);
                    }
                }));
    }

    @Test
    public void memoryTest() throws Exception {
        doInTempDirectory(path ->
                doWithStrings(path, storage -> {
                    int numberOperations = 100;
                    ArrayList<String> keys = new ArrayList<>(numberOperations);
                    HashMap<String, String> map = new HashMap<>();
                    Random random = new Random(0);
                    for (int i = 0; i < numberOperations; i++) {
                        int type = random.nextInt() % 100;
                        if (0 <= type && type < 50 || keys.isEmpty()) {
                            // write
                            String key = getRandomString(10, random);
                            String value = getRandomString(10000, random);
                            storage.write(key, value);
                            keys.add(key);
                            map.put(key, value);
                        } else if (50 <= type && type < 85) {
                            // read
                            String key = keys.get(random.nextInt(keys.size()));
                            String value = storage.read(key);
                            assertEquals(value, map.get(key));
                        } else if (85 <= type && type < 100) {
                            // delete
                            int index = random.nextInt(keys.size());
                            String key = keys.get(index);
                            storage.delete(key);
                            keys.set(index, keys.get(keys.size() - 1));
                            keys.remove(keys.size() - 1);
                        }
                    }
                }));
    }

    private String getRandomString(int approximateLenth, Random random) {
        double length = approximateLenth * (random.nextFloat() + 1);
        // 2^x = 10^length
        int x = (int) (length * Math.log(10) / Math.log(2));
        return new BigInteger(x, random).toString();
    }

    @Test
    public void superTest() throws Exception {
        doInTempDirectory(path -> {
            doWithStrings(path, storage -> {
                Random random = new Random(42);
                long writeTime = StorageTestUtils.measureTime(() -> {
                    for (int i = 0; i < 100000; ++i) {
                        String key = randomKey(random);
                        String value = randomValue(random);
                        storage.write(key, value);
                    }
                });
            });
        });
    }
}