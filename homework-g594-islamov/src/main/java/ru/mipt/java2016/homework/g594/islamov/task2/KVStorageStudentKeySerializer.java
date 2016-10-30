package ru.mipt.java2016.homework.g594.islamov.task2;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageStudentKeySerializer implements KVSSerializationInterface<StudentKey> {

    @Override
    public String serialize(StudentKey object) {
        StringBuilder serialized = new StringBuilder("<");
        serialized.append("'");
        serialized.append(Integer.toString(object.getGroupId()));
        serialized.append("','");
        serialized.append(object.getName());
        serialized.append("'>");
        return serialized.toString();
    }

    @Override
    public StudentKey deserialize(String object) throws BadStorageException {
        int objectLength = object.length();
        checkOpenBracket(object.charAt(0));
        checkCloseBracket(object.charAt(objectLength - 1));
        String[] deserialized = object.substring(1, objectLength - 1).split(",");
        if (deserialized.length != 2) {
            throw new BadStorageException("Deserialization Error");
        }
        int groupID;
        String name;
        try {
            int groupIDLength = deserialized[0].length();
            checkQuote(deserialized[0].charAt(0));
            checkQuote(deserialized[0].charAt(groupIDLength - 1));
            groupID = Integer.parseInt(deserialized[0].substring(1, groupIDLength - 1));
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
        int nameLength = deserialized[1].length();
        checkQuote(deserialized[1].charAt(0));
        checkQuote(deserialized[1].charAt(nameLength - 1));
        name = deserialized[1].substring(1, nameLength - 1);
        if (name.length() == 0) {
            throw new BadStorageException("Deserialization Error");
        }
        return new StudentKey(groupID, name);
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