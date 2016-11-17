package ru.mipt.java2016.homework.g595.popovkin.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Howl on 17.11.2016.
 */
public class StudentKeyParserRandomAccess implements ParserInterface<StudentKey> {

    @Override
    public void serialize(StudentKey arg, RandomAccessFile out) throws IOException {
        out.writeInt(arg.getGroupId());
        out.writeUTF(arg.getName());
    }

    @Override
    public StudentKey deserialize(RandomAccessFile in) throws IOException {
        return new StudentKey(in.readInt(), in.readUTF());
    }
}