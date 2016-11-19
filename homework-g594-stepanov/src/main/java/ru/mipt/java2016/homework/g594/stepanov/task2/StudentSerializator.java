package ru.mipt.java2016.homework.g594.stepanov.task2;

import javafx.util.Pair;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.util.Date;

public class StudentSerializator extends ObjectSerializator<StudentKey, Student> {
    public StudentSerializator(String directory) throws IOException {
        super(directory);
    }

    @Override
    void write(StudentKey key, Student value) throws IOException {
        Pair p = new Pair(key, value);
        currentHash += p.hashCode();
        StringBuilder sb = new StringBuilder("");
        sb.append(start);
        sb.append(key.getGroupId());
        sb.append(numerator);
        sb.append(key.getName());
        sb.append(separator);
        sb.append(value.getHometown());
        sb.append(numerator);
        sb.append(value.getBirthDate().getTime());
        sb.append(numerator);
        sb.append(value.isHasDormitory());
        sb.append(numerator);
        sb.append(value.getAverageScore());
        sb.append(finish);
        sb.append("\n");
        outputStream.print(sb.toString());
    }

    @Override
    Pair<StudentKey, Student> read() throws IOException {
        String s = inputStream.readLine();
        if (s == null) {
            throw new IOException("File end");
        }
        int pos = s.indexOf(separator);
        if (!s.startsWith(start) || !s.endsWith(finish) || pos == -1) {
            throw new IOException("Invalid string in input file");
        }
        int pos1 = s.indexOf(numerator);
        Integer groupId;
        if (pos1 == -1) {
            throw new IOException("Invalid string in input file");
        }
        groupId = Integer.parseInt(s.substring(1, pos1));
        String name = s.substring(pos1 + 1, pos);
        s = s.substring(pos + 1, s.length());
        pos1 = s.indexOf(numerator);
        if (pos1 == -1) {
            throw new IOException("Invalid string in input file");
        }
        String homeTown = s.substring(0, pos1);
        s = s.substring(pos1 + 1, s.length());
        pos1 = s.indexOf(numerator);
        if (pos1 == -1) {
            throw new IOException("Invalid string in input file");
        }
        Date dd = new Date(Long.parseLong(s.substring(0, pos1)));
        s = s.substring(pos1 + 1, s.length());
        pos1 = s.indexOf(numerator);
        if (pos1 == -1) {
            throw new IOException("Invalid string in input file");
        }
        Boolean hasDormitory = Boolean.parseBoolean(s.substring(0, pos1));
        s = s.substring(pos1 + 1, s.length());
        pos1 = s.indexOf(finish);
        Double average = Double.parseDouble(s.substring(0, pos1));
        StudentKey key = new StudentKey(groupId, name);
        Student value = new Student(groupId, name, homeTown, dd, hasDormitory, average);
        return new Pair(key, value);
    }
}
