package ru.mipt.java2016.homework.g594.gorelick.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

class StudentFileWorker implements FileWorker<Student> {
    @Override
    public Student read(RandomAccessFile file, long position) throws IOException {
        IntegerFileWorker integerFileWorker = new IntegerFileWorker();
        StringFileWorker stringSerializer = new StringFileWorker();
        DateFileWorker dateFileWorker = new DateFileWorker();
        BooleanFileWorker booleanFileWorker = new BooleanFileWorker();
        DoubleFileWorker doubleFileWorker = new DoubleFileWorker();
        file.seek(position);
        int groupId = integerFileWorker.read(file, file.getFilePointer());
        String name = stringSerializer.read(file, file.getFilePointer());
        String hometown = stringSerializer.read(file, file.getFilePointer());
        Date birthDate = dateFileWorker.read(file, file.getFilePointer());
        boolean hasDormitory = booleanFileWorker.read(file, file.getFilePointer());
        double averageScore = doubleFileWorker.read(file, file.getFilePointer());
        return new Student(groupId, name, hometown, birthDate, hasDormitory, averageScore);
    }
    @Override
    public void write(RandomAccessFile file, Student object, long position) throws IOException {
        IntegerFileWorker integerFileWorker = new IntegerFileWorker();
        StringFileWorker stringSerializer = new StringFileWorker();
        DateFileWorker dateFileWorker = new DateFileWorker();
        BooleanFileWorker booleanFileWorker = new BooleanFileWorker();
        DoubleFileWorker doubleFileWorker = new DoubleFileWorker();
        file.seek(position);
        integerFileWorker.write(file, object.getGroupId(), file.getFilePointer());
        stringSerializer.write(file, object.getName(), file.getFilePointer());
        stringSerializer.write(file, object.getHometown(), file.getFilePointer());
        dateFileWorker.write(file, object.getBirthDate(), file.getFilePointer());
        booleanFileWorker.write(file, object.isHasDormitory(), file.getFilePointer());
        doubleFileWorker.write(file, object.getAverageScore(), file.getFilePointer());
    }
}

