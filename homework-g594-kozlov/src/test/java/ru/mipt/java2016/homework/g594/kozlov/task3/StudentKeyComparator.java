package ru.mipt.java2016.homework.g594.kozlov.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Comparator;

/**
 * Created by Anatoly on 15.11.2016.
 */
public class StudentKeyComparator implements Comparator<StudentKey> {
    @Override
    public int compare(StudentKey o1, StudentKey o2) {
        return o1.compareTo(o2);
    }
}
