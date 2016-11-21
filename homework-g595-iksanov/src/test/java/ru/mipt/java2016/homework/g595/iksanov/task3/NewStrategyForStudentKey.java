package ru.mipt.java2016.homework.g595.iksanov.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Эмиль
 */
public class NewStrategyForStudentKey implements NewSerializationStrategy<StudentKey> {
    private static final NewStrategyForStudentKey INSTANCE = new NewStrategyForStudentKey();

    public static NewStrategyForStudentKey getInstance() {
        return INSTANCE;
    }

    private NewStrategyForStudentKey() {}

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
