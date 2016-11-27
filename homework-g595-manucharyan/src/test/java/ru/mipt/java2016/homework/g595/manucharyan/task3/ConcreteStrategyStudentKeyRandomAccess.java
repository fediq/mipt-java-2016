package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStudentKeyRandomAccess implements SerializationStrategyRandomAccess<StudentKey> {

    @Override
    public void serializeToFile(StudentKey value, DataOutput output) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
    }

    @Override
    public StudentKey deserializeFromFile(DataInput input) throws IOException {
        return new StudentKey(input.readInt(), input.readUTF());
    }
}
