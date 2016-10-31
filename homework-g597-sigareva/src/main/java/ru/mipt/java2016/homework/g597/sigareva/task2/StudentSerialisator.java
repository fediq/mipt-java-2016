package ru.mipt.java2016.homework.g597.sigareva.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import javafx.util.Pair;
import java.io.IOException;
import java.util.Date;

/**
 * Created by 1 on 31.10.2016.
 *     private final String hometown;
 private final Date birthDate;
 private final boolean hasDormitory;
 private final double averageScore;

 private final int groupId;
 private final String name;
 */
class StudentSerialisator extends ObjectSerialisator <StudentKey, Student> {

    StudentSerialisator(String path_) {
        super(path_);
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

    @Override
    Pair<StudentKey, Student> read() throws IOException {
        String input = inputStream.readLine();
        //System.out.println(input);
        if (input == null) {
            throw new IOException("EOF");
        } else {
            StringBuilder newInput = new StringBuilder(input.subSequence(0, input.length()));
            int border = newInput.indexOf(" ");
            int StudentKeyGroupId = Integer.parseInt(newInput.substring(0, border));
            newInput.delete(0, border + 1);
            border = newInput.indexOf(":");
            String StudentKeyName = newInput.substring(0, border);
            newInput.delete(0, border + 1);
            border = newInput.indexOf(" ");
            String ValueHomeTown = newInput.substring(0, border);
            newInput.delete(0, border + 1);
            border = newInput.indexOf(" ");
            Date ValueDate = new Date(Long.parseLong(newInput.substring(0, border)));
            newInput.delete(0, border + 1);
            border = newInput.indexOf(" ");
            Boolean ValueHasDormitory = Boolean.parseBoolean(newInput.substring(0, border));
            newInput.delete(0, border + 1);
            Double ValueAverage = Double.parseDouble(newInput.toString());
            StudentKey key = new StudentKey(StudentKeyGroupId, StudentKeyName);
            Student value = new Student(StudentKeyGroupId, StudentKeyName, ValueHomeTown, ValueDate, ValueHasDormitory, ValueAverage);
            return new Pair(key, value);
        }
    }
}