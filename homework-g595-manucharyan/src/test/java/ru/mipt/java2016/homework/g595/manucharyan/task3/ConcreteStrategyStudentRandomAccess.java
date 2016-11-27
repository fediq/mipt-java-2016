package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyStudentRandomAccess implements SerializationStrategyRandomAccess<Student> {
    @Override
    public void serializeToFile(Student value, DataOutput output) throws IOException {
        output.writeInt(value.getGroupId());
        output.writeUTF(value.getName());
        output.writeUTF(value.getHometown());
        output.writeLong(value.getBirthDate().getTime());
        output.writeBoolean(value.isHasDormitory());
        output.writeDouble(value.getAverageScore());
    }

    @Override
    public Student deserializeFromFile(DataInput input) throws IOException {
        return new Student(input.readInt(), input.readUTF(),
                input.readUTF(), new Date(input.readLong()),
                input.readBoolean(), input.readDouble());
    }
}
