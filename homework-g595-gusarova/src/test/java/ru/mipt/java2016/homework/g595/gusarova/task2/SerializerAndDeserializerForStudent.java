package ru.mipt.java2016.homework.g595.gusarova.task2;

/**
 * Created by Дарья on 17.12.2016.
 */

import ru.mipt.java2016.homework.tests.task2.Student;
import java.io.DataOutput;
import java.io.DataInput;
import java.io.IOException;
import java.util.Date;

public class SerializerAndDeserializerForStudent implements SerializerAndDeserializer<Student> {
    @Override
    public void serialize(Student data, DataOutput file) throws IOException {
        file.writeInt(data.getGroupId());
        file.writeUTF(data.getName());
        file.writeUTF(data.getHometown());
        file.writeLong(data.getBirthDate().getTime());
        file.writeBoolean(data.isHasDormitory());
        file.writeDouble(data.getAverageScore());
    }

    @Override
    public Student deserialize(DataInput file) throws IOException {
        Student temp = new Student(file.readInt(), file.readUTF(), file.readUTF(),
                new Date(file.readLong()), file.readBoolean(),
                file.readDouble());
        return temp;
    }
}