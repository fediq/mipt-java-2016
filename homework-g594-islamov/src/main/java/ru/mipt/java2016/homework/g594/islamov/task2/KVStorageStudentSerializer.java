package ru.mipt.java2016.homework.g594.islamov.task2;

import ru.mipt.java2016.homework.tests.task2.Student;
import java.util.Date;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageStudentSerializer implements KVSSerializationInterface<Student> {

    @Override
    public String serialize(Student object) {
        StringBuilder serialized = new StringBuilder("<");
        serialized.append("'");
        serialized.append(Integer.toString(object.getGroupId()));
        serialized.append("','");
        serialized.append(object.getName());
        serialized.append("','");
        serialized.append(object.getHometown());
        serialized.append("','");
        serialized.append(object.getBirthDate().getTime());
        serialized.append("','");
        serialized.append(Boolean.toString(object.isHasDormitory()));
        serialized.append("','");
        serialized.append(Double.toString(object.getAverageScore()));
        serialized.append("'>");
        return serialized.toString();
    }

    @Override
    public Student deserialize(String object) throws BadStorageException {
        int objectLength = object.length();
        checkOpenBracket(object.charAt(0));
        checkCloseBracket(object.charAt(objectLength - 1));
        String[] deserialized = object.substring(1, objectLength - 1).split(",");
        if (deserialized.length != 6) {
            throw new BadStorageException("Deserialization Error");
        }
        int groupID;
        String name;
        String hometown;
        Date date;
        Boolean hasDomitory;
        Double averageScore;
        int length;
        try {
            length = deserialized[0].length();
            checkQuote(deserialized[0].charAt(0));
            checkQuote(deserialized[0].charAt(length - 1));
            groupID = Integer.parseInt(deserialized[0].substring(1, length - 1));
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
        length = deserialized[1].length();
        checkQuote(deserialized[1].charAt(0));
        checkQuote(deserialized[1].charAt(length - 1));
        if (length == 2) {
            throw new BadStorageException("Deserialization Error");
        }
        name = deserialized[1].substring(1, length - 1);
        length = deserialized[2].length();
        checkQuote(deserialized[2].charAt(0));
        checkQuote(deserialized[2].charAt(length - 1));
        if (length == 2) {
            throw new BadStorageException("Deserealization Error");
        }
        hometown = deserialized[2].substring(1, length - 1);
        try {
            length = deserialized[3].length();
            checkQuote(deserialized[3].charAt(0));
            checkQuote(deserialized[3].charAt(length - 1));
            date = new Date(Long.parseLong(deserialized[3].substring(1, length - 1)));
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
        try {
            length = deserialized[4].length();
            checkQuote(deserialized[4].charAt(0));
            checkQuote(deserialized[4].charAt(length - 1));
            hasDomitory = Boolean.parseBoolean(deserialized[4].substring(1, length - 1));
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
        try {
            length = deserialized[5].length();
            checkQuote(deserialized[5].charAt(0));
            checkQuote(deserialized[5].charAt(length -  1));
            averageScore = Double.parseDouble(deserialized[5].substring(1, length - 1));
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
        return new Student(groupID, name, hometown, date, hasDomitory, averageScore);
    }

    public void checkOpenBracket(char symbol) throws BadStorageException {
        if (symbol != '<') {
            throw new BadStorageException("Deserialization Error");
        }
    }

    public void checkCloseBracket(char symbol) throws BadStorageException {
        if (symbol != '>') {
            throw new BadStorageException("Deserialization Error");
        }
    }

    public void checkQuote(char symbol) throws BadStorageException {
        if (symbol != '\'') {
            throw new BadStorageException("Deserialization Error");
        }
    }
}
