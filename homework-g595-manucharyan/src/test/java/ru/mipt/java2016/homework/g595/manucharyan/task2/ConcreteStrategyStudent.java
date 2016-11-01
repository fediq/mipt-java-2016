package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;

import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStudent implements SerializationStrategy<Student> {
    @Override
    public void serializeToStream(Student value, DataOutputStream stream) throws IOException {
        stream.writeInt(value.getGroupId());
        ConcreteStrategyString.writeString(stream, value.getName());
        ConcreteStrategyString.writeString(stream, value.getHometown());
        stream.writeLong(value.getBirthDate().getTime());
        stream.writeBoolean(value.isHasDormitory());
        stream.writeDouble(value.getAverageScore());
    }

    @Override
    public Student deserializeFromStream(DataInputStream stream) throws IOException {
        return new Student(stream.readInt(), ConcreteStrategyString.readString(stream),
                ConcreteStrategyString.readString(stream), new Date(stream.readLong()),
                stream.readBoolean(), stream.readDouble());
    }
}
