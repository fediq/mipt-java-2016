package ru.mipt.java2016.homework.g595.turumtaev.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by galim on 31.10.2016.
 */
public class MyStudentKeySerializationStrategy implements MySerializationStrategy<StudentKey> {
    private static final MyStudentKeySerializationStrategy INSTANCE = new MyStudentKeySerializationStrategy();

    public static MyStudentKeySerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyStudentKeySerializationStrategy() {
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
