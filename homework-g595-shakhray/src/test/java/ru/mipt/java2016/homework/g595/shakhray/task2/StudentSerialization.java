package ru.mipt.java2016.homework.g595.shakhray.task2;

import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Classes.*;
import ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface.StorageSerialization;
import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Vlad on 29/10/2016.
 */
public class StudentSerialization implements StorageSerialization<Student> {

    /**
     * Required serializations of other types
     */
    private IntegerSerialization integerSerialization = IntegerSerialization.getSerialization();
    private StringSerialization stringSerialization = StringSerialization.getSerialization();
    private DateSerialization dateSerialization = DateSerialization.getSerialization();
    private BooleanSerialization booleanSerialization = BooleanSerialization.getSerialization();
    private DoubleSerialization doubleSerialization = DoubleSerialization.getSerialization();

    /**
     * This class is a singleton
     */
    private static StudentSerialization serialization = new StudentSerialization();

    private StudentSerialization() { }

    public static StudentSerialization getSerialization() {
        return serialization;
    }

    @Override
    public void write(RandomAccessFile file, Student object) throws IOException {
        try {
            integerSerialization.write(file, object.getGroupId());
            stringSerialization.write(file, object.getName());
            stringSerialization.write(file, object.getHometown());
            dateSerialization.write(file, object.getBirthDate());
            booleanSerialization.write(file, object.isHasDormitory());
            doubleSerialization.write(file, object.getAverageScore());
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public Student read(RandomAccessFile file) throws IOException {
        try {
            int groupId = integerSerialization.read(file);
            String name = stringSerialization.read(file);
            String hometown = stringSerialization.read(file);
            Date birthDate = dateSerialization.read(file);
            Boolean hasDormitory = booleanSerialization.read(file);
            Double averageScore = doubleSerialization.read(file);
            return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
        } catch (IOException e) {
            throw new IOException("Could not read from file.");
        }
    }
}
