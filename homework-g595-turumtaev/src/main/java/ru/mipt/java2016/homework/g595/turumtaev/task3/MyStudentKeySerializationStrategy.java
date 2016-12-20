package ru.mipt.java2016.homework.g595.turumtaev.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by galim on 20.11.2016.
 */
public class MyStudentKeySerializationStrategy implements MySerializationStrategy<StudentKey> {
    private static final MyStudentKeySerializationStrategy INSTANCE = new MyStudentKeySerializationStrategy();

    public static MyStudentKeySerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyStudentKeySerializationStrategy() {
    }

    @Override
    public Long write(StudentKey value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
        return offset;
    }

    @Override
    public StudentKey read(RandomAccessFile input) throws IOException {
        return new StudentKey(input.readInt(), input.readUTF());
    }
}
