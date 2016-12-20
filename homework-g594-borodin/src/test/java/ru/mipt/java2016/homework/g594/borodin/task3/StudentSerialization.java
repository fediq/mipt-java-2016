package ru.mipt.java2016.homework.g594.borodin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies.SerializationStrategy;
import ru.mipt.java2016.homework.tests.task2.Student;

/**
 * Created by Maxim on 10/31/2016.
 */
public class StudentSerialization implements SerializationStrategy<Student> {

    @Override
    public void serialize(Student value, DataOutput dataOutputStream) throws IOException {
        dataOutputStream.writeInt(value.getGroupId());
        dataOutputStream.writeUTF(value.getName());
        dataOutputStream.writeUTF(value.getHometown());
        dataOutputStream.writeLong(value.getBirthDate().getTime());
        dataOutputStream.writeBoolean(value.isHasDormitory());
        dataOutputStream.writeDouble(value.getAverageScore());
    }

    @Override
    public Student deserialize(DataInput dataInputStream) throws IOException {
        int groupId = dataInputStream.readInt();
        String name = dataInputStream.readUTF();
        String hometown = dataInputStream.readUTF();
        Date birthDate = new Date(dataInputStream.readLong());
        boolean hasDormitory = dataInputStream.readBoolean();
        double averageScore = dataInputStream.readDouble();

        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
}
