package ru.mipt.java2016.homework.g594.islamov.task3;

import ru.mipt.java2016.homework.tests.task2.Student;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

class KVStorageStudentSerializer implements KVSSerializationInterface<Student> {
    @Override
    public String serialize(Student object) {
        return "<" + "'" + Integer.toString(object.getGroupId()) + "','" + object.getName() + "','" +
                object.getHometown() + "','" + object.getBirthDate().getTime() + "','" +
                Boolean.toString(object.isHasDormitory()) + "','" + Double.toString(object.getAverageScore()) + "'>";
    }

    @Override
    public Student deserialize(String object) throws IOException {
        int objectLength = object.length();
        checkOpenBracket(object.charAt(0));
        checkCloseBracket(object.charAt(objectLength - 1));
        String[] deserialized = object.substring(1, objectLength - 1).split(",");
        if (deserialized.length != 6) {
            throw new IOException("Deserialization Error");
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
            throw new IOException("Deserialization Error");
        }
        length = deserialized[1].length();
        checkQuote(deserialized[1].charAt(0));
        checkQuote(deserialized[1].charAt(length - 1));
        if (length == 2) {
            throw new IOException("Deserialization Error");
        }
        name = deserialized[1].substring(1, length - 1);
        length = deserialized[2].length();
        checkQuote(deserialized[2].charAt(0));
        checkQuote(deserialized[2].charAt(length - 1));
        if (length == 2) {
            throw new IOException("Deserealization Error");
        }
        hometown = deserialized[2].substring(1, length - 1);
        try {
            length = deserialized[3].length();
            checkQuote(deserialized[3].charAt(0));
            checkQuote(deserialized[3].charAt(length - 1));
            date = new Date(Long.parseLong(deserialized[3].substring(1, length - 1)));
        } catch (NumberFormatException e) {
            throw new IOException("Deserialization Error");
        }
        try {
            length = deserialized[4].length();
            checkQuote(deserialized[4].charAt(0));
            checkQuote(deserialized[4].charAt(length - 1));
            hasDomitory = Boolean.parseBoolean(deserialized[4].substring(1, length - 1));
        } catch (NumberFormatException e) {
            throw new IOException("Deserialization Error");
        }
        try {
            length = deserialized[5].length();
            checkQuote(deserialized[5].charAt(0));
            checkQuote(deserialized[5].charAt(length -  1));
            averageScore = Double.parseDouble(deserialized[5].substring(1, length - 1));
        } catch (NumberFormatException e) {
            throw new IOException("Deserialization Error");
        }
        return new Student(groupID, name, hometown, date, hasDomitory, averageScore);
    }

    private void checkOpenBracket(char symbol) throws IOException {
        if (symbol != '<') {
            throw new IOException("Deserialization Error");
        }
    }

    private void checkCloseBracket(char symbol) throws IOException {
        if (symbol != '>') {
            throw new IOException("Deserialization Error");
        }
    }

    private void checkQuote(char symbol) throws IOException {
        if (symbol != '\'') {
            throw new IOException("Deserialization Error");
        }
    }
}