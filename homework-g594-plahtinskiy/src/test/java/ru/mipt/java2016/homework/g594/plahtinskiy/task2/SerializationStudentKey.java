package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationStudentKey extends Serialization<StudentKey> {

    @Override
    public void write(RandomAccessFile file, StudentKey obj) throws IOException {
        SerializationInt serializationGroupId = new SerializationInt();
        SerializationString serializationName = new SerializationString();
        serializationGroupId.write(file, obj.getGroupId());
        serializationName.write(file, obj.getName());
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        SerializationInt serializationGroupId = new SerializationInt();
        SerializationString serializationName = new SerializationString();
        Integer groupId = serializationGroupId.read(file);
        String name = serializationName.read(file);
        return new StudentKey(groupId, name);
    }
}
