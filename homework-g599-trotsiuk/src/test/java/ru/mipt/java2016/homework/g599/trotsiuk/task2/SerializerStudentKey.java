package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SerializerStudentKey implements Serializer<StudentKey> {


    @Override
    public void serializeWrite(StudentKey value, DataOutput stream) throws IOException {
        stream.writeInt(value.getGroupId());
        stream.writeInt(value.getName().getBytes("UTF-8").length);
        stream.write(value.getName().getBytes("UTF-8"));

    }

    @Override
    public StudentKey deserializeRead(DataInput stream) throws IOException {
        return new StudentKey(stream.readInt(), stream.readUTF());
    }
}
