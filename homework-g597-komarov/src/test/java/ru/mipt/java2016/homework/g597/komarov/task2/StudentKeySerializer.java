package ru.mipt.java2016.homework.g597.komarov.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Михаил on 31.10.2016.
 */
public class StudentKeySerializer implements Serializer<StudentKey> {
    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        int groupId = file.readInt();
        StringSerializer helper = new StringSerializer();
        String name = helper.read(file);
        return new StudentKey(groupId, name);
    }

    @Override
    public void write(RandomAccessFile file, StudentKey arg) throws IOException {
        file.writeInt(arg.getGroupId());
        StringSerializer helper = new StringSerializer();
        helper.write(file, arg.getName());
    }
}
