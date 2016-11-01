package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStudentKey implements SerializationStrategy<StudentKey> {

    @Override
    public void serializeToStream(StudentKey value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.getGroupId());
        ConcreteStrategyString.writeString(stream, value.getName());
    }

    @Override
    public StudentKey deserializeFromStream(DataInputStream stream) throws IOException {
        return new StudentKey(stream.readInt(), ConcreteStrategyString.readString(stream));
    }
}
