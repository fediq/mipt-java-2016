package ru.mipt.java2016.homework.g595.topilskiy.task3;

import ru.mipt.java2016.homework.base.task2.KeyValueStorage;
import ru.mipt.java2016.homework.base.task2.MalformedDataException;

import ru.mipt.java2016.homework.g595.topilskiy.task3.Verification.Adler32Verification;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.tests.task3.KeyValueStoragePerformanceTest;

import ru.mipt.java2016.homework.g595.topilskiy.task3.OptimisedByteKeyValueStorage;
import ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer.*;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author Artem K. Topilskiy
 * @since 20.11.16
 */
public class OptimisedByteKeyValueStoragePerformanceTest extends KeyValueStoragePerformanceTest {
    @Test
    public void testAdler32() throws IOException {
        final String TEST_FILE_PATH = "/tmp/test_adler.txt";

        File file = new File(TEST_FILE_PATH);
        file.createNewFile();

        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.writeUTF("TESTING ADLER 32. THIS MUST WORK, MUST IT NOT?!");
        raf.close();

        if(!Adler32Verification.checkAdler32Checksum(
                TEST_FILE_PATH, Adler32Verification.calculateAdler32Checksum(TEST_FILE_PATH))) {
            throw new IOException("Bad Adler32 Checker");
        }
    }

    @Override
    protected KeyValueStorage<String, String> buildStringsStorage(String path) throws MalformedDataException {
        return new OptimisedByteKeyValueStorage<>(path, StringSerializerSingleton.getInstance(),
                                                        StringSerializerSingleton.getInstance());
    }

    @Override
    protected KeyValueStorage<Integer, Double> buildNumbersStorage(String path) throws MalformedDataException {
        return new OptimisedByteKeyValueStorage<>(path, IntegerSerializerSingleton.getInstance(),
                                                        DoubleSerializerSingleton.getInstance());
    }

    @Override
    protected KeyValueStorage<StudentKey, Student> buildPojoStorage(String path) throws MalformedDataException {
        return new OptimisedByteKeyValueStorage<>(path, StudentKeySerializerSingleton.getInstance(),
                                                        StudentSerializerSingleton.getInstance());
    }
}
