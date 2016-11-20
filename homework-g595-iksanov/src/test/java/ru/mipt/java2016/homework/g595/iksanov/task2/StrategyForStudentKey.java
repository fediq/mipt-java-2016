package ru.mipt.java2016.homework.g595.iksanov.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эмиль
 */
public class StrategyForStudentKey implements SerializationStrategy<StudentKey> {
    private static final StrategyForStudentKey INSTANCE = new StrategyForStudentKey();

    public static StrategyForStudentKey getInstance() {
        return INSTANCE;
    }

    private StrategyForStudentKey() {
    }

    @Override
    public void write(StudentKey value, DataOutputStream output) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
    }

    @Override
    public StudentKey read(DataInputStream input) throws IOException {
        return new StudentKey(input.readInt(), input.readUTF());
    }
}
