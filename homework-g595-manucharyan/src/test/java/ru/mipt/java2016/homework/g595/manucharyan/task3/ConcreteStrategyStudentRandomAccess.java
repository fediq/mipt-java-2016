package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStudentRandomAccess implements SerializationStrategyRandomAccess<Student> {
    @Override
    public void serializeToFile(Student value, RandomAccessFile output) throws IOException {
        output.writeInt(value.getGroupId());
        ConcreteStrategyStringRandomAccess.writeString(output, value.getName());
        ConcreteStrategyStringRandomAccess.writeString(output, value.getHometown());
        output.writeLong(value.getBirthDate().getTime());
        output.writeBoolean(value.isHasDormitory());
        output.writeDouble(value.getAverageScore());
    }

    @Override
    public Student deserializeFromFile(RandomAccessFile input) throws IOException {
        return new Student(input.readInt(), ConcreteStrategyStringRandomAccess.readString(input),
                ConcreteStrategyStringRandomAccess.readString(input), new Date(input.readLong()),
                input.readBoolean(), input.readDouble());
    }
}
