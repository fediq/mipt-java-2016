package ru.mipt.java2016.homework.g597.komarov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;


/**
 * Created by Михаил on 31.10.2016.
 */
public class StudentSerializer implements Serializer<Student> {
    private final StringSerializer helper = new StringSerializer();

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        int groupId = file.readInt();
        String name = helper.read(file);
        String hometown = helper.read(file);
        Date birthDate = new Date(file.readLong());
        boolean hasDormiory = file.readBoolean();
        double averageScore = file.readDouble();
        return new Student(groupId, name, hometown, birthDate, hasDormiory, averageScore);
    }

    @Override
    public void write(RandomAccessFile file, Student arg) throws IOException {
        file.writeInt(arg.getGroupId());
        helper.write(file, arg.getName());
        helper.write(file, arg.getHometown());
        file.writeLong(arg.getBirthDate().getTime());
        file.writeBoolean(arg.isHasDormitory());
        file.writeDouble(arg.getAverageScore());
    }
}
