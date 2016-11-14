package ru.mipt.java2016.homework.g597.sigareva.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import javafx.util.Pair;
import java.util.Date;

class StudentSerializer extends ObjectSerializer<StudentKey, Student> {

    StudentSerializer(String newPath) {
        super(newPath);
    }

    @Override
    protected Pair<StudentKey, Student> convert() {
        StringBuilder newInput = new StringBuilder(lastRead.subSequence(0, lastRead.length()));
        int border = newInput.indexOf(" ");
        int studentKeyGroupId = Integer.parseInt(newInput.substring(0, border));
        newInput.delete(0, border + 1);
        border = newInput.indexOf(":");
        String studentKeyName = newInput.substring(0, border);
        newInput.delete(0, border + 1);
        border = newInput.indexOf(" ");
        String valueHomeTown = newInput.substring(0, border);
        newInput.delete(0, border + 1);
        border = newInput.indexOf(" ");
        Date valueDate = new Date(Long.parseLong(newInput.substring(0, border)));
        newInput.delete(0, border + 1);
        border = newInput.indexOf(" ");
        Boolean valueHasDormitory = Boolean.parseBoolean(newInput.substring(0, border));
        newInput.delete(0, border + 1);
        Double valueAverage = Double.parseDouble(newInput.toString());
        StudentKey key = new StudentKey(studentKeyGroupId, studentKeyName);
        Student value = new Student(studentKeyGroupId, studentKeyName,
                valueHomeTown, valueDate, valueHasDormitory, valueAverage);
        return new Pair(key, value);
    }

    @Override
    void write(StudentKey key, Student value) {
        outputStream.print(key.getGroupId());
        outputStream.print(" ");
        outputStream.print(key.getName());
        outputStream.print(":");
        outputStream.print(value.getHometown());
        outputStream.print(" ");
        outputStream.print(value.getBirthDate().getTime());
        outputStream.print(" ");
        outputStream.print(value.isHasDormitory());
        outputStream.print(" ");
        outputStream.print(value.getAverageScore());
        outputStream.print("\n");
    }
}