package ru.mipt.java2016.homework.g595.romanenko.task3.comapators;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.util.Comparator;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task3.comapators
 *
 * @author Ilya I. Romanenko
 * @since 30.10.16
 **/
public class StudentKeyComparator implements Comparator<StudentKey> {

    @Override
    public int compare(StudentKey obj1, StudentKey obj2) {
        int groupDelta = obj1.getGroupId() - obj2.getGroupId();
        if (groupDelta != 0) {
            return groupDelta;
        }
        return obj1.getName().compareTo(obj2.getName());
    }

}
