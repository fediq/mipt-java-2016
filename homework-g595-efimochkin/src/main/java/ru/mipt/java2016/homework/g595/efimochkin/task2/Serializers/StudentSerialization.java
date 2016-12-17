package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;


/**
 * Created by sergejefimockin on 28.11.16.
 */
public class StudentSerialization implements BaseSerialization<Student> {

   // private BooleanSerialization booleanSerialization = BooleanSerialization.getInstance();
    //private DateSerialization dateSerialization = DateSerialization.getInstance();
    //private DoubleSerialization doubleSerialization = DoubleSerialization.getInstance();
    //private IntegerSerialization integerSerialization = IntegerSerialization.getInstance();
    //private LongSerialization longSerialization = LongSerialization.getInstance();
    private StringSerialization stringSerialization = StringSerialization.getInstance();

    private static StudentSerialization instance = new StudentSerialization();

    public static StudentSerialization getInstance() {
        return instance;
    }

    private StudentSerialization() {

    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        int groupId = file.readInt();
        String name = stringSerialization.read(file);
        String hometown = stringSerialization.read(file);
        Date birthDate = new Date(file.readLong());
        boolean hasDormiory = file.readBoolean();
        double averageScore = file.readDouble();
        return new Student(groupId, name, hometown, birthDate, hasDormiory, averageScore);
    }

    @Override
    public Long write(RandomAccessFile file, Student arg) throws IOException {
        Long offset = file.getFilePointer();
        file.writeInt(arg.getGroupId());
        stringSerialization.write(file, arg.getName());
        stringSerialization.write(file, arg.getHometown());
        file.writeLong(arg.getBirthDate().getTime());
        file.writeBoolean(arg.isHasDormitory());
        file.writeDouble(arg.getAverageScore());
        return offset;
    }
}

