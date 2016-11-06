package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializerStudentKey implements Serializer<StudentKey> {


    @Override
    public void serializeWrite(StudentKey value, RandomAccessFile dbFile) throws IOException {
        dbFile.writeInt(value.getGroupId());
        dbFile.writeInt(value.getName().getBytes("UTF-8").length);
        dbFile.write(value.getName().getBytes("UTF-8"));

    }

    @Override
    public StudentKey deserializeRead(RandomAccessFile dbFile) throws IOException {
        int id = dbFile.readInt();
        int wordLength = dbFile.readInt();
        byte[] word = new byte[wordLength];
        dbFile.read(word, 0, wordLength);
        return new StudentKey(id, new String(word, "UTF-8"));
    }
}
