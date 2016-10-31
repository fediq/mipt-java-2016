package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by liza on 31.10.16.
 */
public class StudentKeySerializer implements MySerializerInterface<StudentKey> {
    private final IntegerSerializer integerSerializer = IntegerSerializer.getExample();
    private final StringSerializer stringSerializer = StringSerializer.getExample();

    @Override
    public void write(RandomAccessFile file, StudentKey object) throws IOException {
        integerSerializer.write(file, object.getGroupId());
        ;
        stringSerializer.write(file, object.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupID = integerSerializer.read(file);
        String name = stringSerializer.read(file);
        return new StudentKey(groupID, name);
    }

    private static final StudentKeySerializer EXAMPLE = new StudentKeySerializer();

    public static StudentKeySerializer getExample() {
        return EXAMPLE;
    }

    private StudentKeySerializer() {
    }
}

