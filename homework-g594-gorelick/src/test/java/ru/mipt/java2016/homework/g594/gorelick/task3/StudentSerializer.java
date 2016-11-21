package ru.mipt.java2016.homework.g594.gorelick.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

class StudentSerializer implements Serializer<Student> {
    @Override
    public Student read(RandomAccessFile file, long position) throws IOException {
        IntegerSerializer integerSerializer = new IntegerSerializer();
        StringSerializer stringSerializer = new StringSerializer();
        DateSerializer dateSerializer = new DateSerializer();
        BooleanSerializer booleanSerializer = new BooleanSerializer();
        DoubleSerializer doubleSerializer = new DoubleSerializer();
        file.seek(position);
        int groupId = integerSerializer.read(file, file.getFilePointer());
        String name = stringSerializer.read(file, file.getFilePointer());
        String hometown = stringSerializer.read(file, file.getFilePointer());
        Date birthDate = dateSerializer.read(file, file.getFilePointer());
        boolean hasDormitory = booleanSerializer.read(file, file.getFilePointer());
        double averageScore = doubleSerializer.read(file, file.getFilePointer());
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
    @Override
    public void write(RandomAccessFile file, Student object, long position) throws IOException {
        IntegerSerializer integerSerializer = new IntegerSerializer();
        StringSerializer stringSerializer = new StringSerializer();
        DateSerializer dateSerializer = new DateSerializer();
        BooleanSerializer booleanSerializer = new BooleanSerializer();
        DoubleSerializer doubleSerializer = new DoubleSerializer();
        file.seek(position);
        integerSerializer.write(file, object.getGroupId(), file.getFilePointer());
        stringSerializer.write(file, object.getName(), file.getFilePointer());
        stringSerializer.write(file, object.getHometown(), file.getFilePointer());
        dateSerializer.write(file, object.getBirthDate(), file.getFilePointer());
        booleanSerializer.write(file, object.isHasDormitory(), file.getFilePointer());
        doubleSerializer.write(file, object.getAverageScore(), file.getFilePointer());
    }
}

