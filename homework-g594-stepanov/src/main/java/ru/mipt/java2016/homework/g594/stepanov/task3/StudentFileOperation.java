package ru.mipt.java2016.homework.g594.stepanov.task3;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

public class StudentFileOperation extends FileOperation<StudentKey, Student> {

    StudentFileOperation(String fileName) {
        super(fileName);
        keys = new StudentKeySerialisator();
        values = new StudentSerialisator();
    }
}