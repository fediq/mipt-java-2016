package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

//import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class StudentKeySerialization implements BaseSerialization<StudentKey> {

    private static StudentKeySerialization instance = new StudentKeySerialization();

    public static StudentKeySerialization getInstance() {
        return instance;
    }

    private StudentKeySerialization() {

    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupId = file.readInt();
        StringSerialization helper = StringSerialization.getInstance();
        String name = helper.read(file);
        return new StudentKey(groupId, name);
    }

    @Override
    public Long write(RandomAccessFile file, StudentKey arg) throws IOException {
        Long offset = file.getFilePointer();
        file.writeInt(arg.getGroupId());
        StringSerialization helper = StringSerialization.getInstance();
        helper.write(file, arg.getName());
        return offset;    }

}