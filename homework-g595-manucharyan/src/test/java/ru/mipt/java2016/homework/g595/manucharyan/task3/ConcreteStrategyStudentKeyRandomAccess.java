package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStudentKeyRandomAccess implements SerializationStrategyRandomAccess<StudentKey> {

    @Override
    public void serializeToFile(StudentKey value, RandomAccessFile output) throws IOException {
        output.writeInt(value.getGroupId());
        ConcreteStrategyStringRandomAccess.writeString(output, value.getName());
    }

    @Override
    public StudentKey deserializeFromFile(RandomAccessFile input) throws IOException {
        return new StudentKey(input.readInt(), ConcreteStrategyStringRandomAccess.readString(input));
    }
}
