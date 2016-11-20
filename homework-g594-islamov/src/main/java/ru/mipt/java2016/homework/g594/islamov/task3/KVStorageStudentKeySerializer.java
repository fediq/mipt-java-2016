package ru.mipt.java2016.homework.g594.islamov.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

class KVStorageStudentKeySerializer implements KVSSerializationInterface<StudentKey> {
    @Override
    public String serialize(StudentKey object) {
        return "<" + "'" + Integer.toString(object.getGroupId()) + "','" + object.getName() + "'>";
    }

    @Override
    public StudentKey deserialize(String object) throws IOException {
        int objectLength = object.length();
        checkOpenBracket(object.charAt(0));
        checkCloseBracket(object.charAt(objectLength - 1));
        String[] deserialized = object.substring(1, objectLength - 1).split(",");
        if (deserialized.length != 2) {
            throw new IOException("Deserialization Error");
        }
        int groupID;
        String name;
        try {
            int groupIDLength = deserialized[0].length();
            checkQuote(deserialized[0].charAt(0));
            checkQuote(deserialized[0].charAt(groupIDLength - 1));
            groupID = Integer.parseInt(deserialized[0].substring(1, groupIDLength - 1));
        } catch (NumberFormatException e) {
            throw new IOException("Deserialization Error");
        }
        int nameLength = deserialized[1].length();
        checkQuote(deserialized[1].charAt(0));
        checkQuote(deserialized[1].charAt(nameLength - 1));
        name = deserialized[1].substring(1, nameLength - 1);
        if (name.length() == 0) {
            throw new IOException("Deserialization Error");
        }
        return new StudentKey(groupID, name);
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