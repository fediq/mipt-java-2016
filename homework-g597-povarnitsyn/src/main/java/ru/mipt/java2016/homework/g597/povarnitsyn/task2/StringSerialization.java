package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Ivan on 30.10.2016.
 */
public class StringSerialization implements SerializationInterface<String> {
    private IntegerSerialization intSerializer = new IntegerSerialization();
    private CharacterSerialization chSerializer = new CharacterSerialization();

    @Override
    public String deserialize(BufferedReader input) throws IOException {
        Integer length = intSerializer.deserialize(input);
        String str = "";
        for (int i = 0; i < length; i++) {
            str += chSerializer.deserialize(input);
        }
        return str;
    }

    @Override
    public void serialize(PrintWriter output, String object) throws IOException {
        intSerializer.serialize(output, object.length());
        for (int i = 0; i < object.length(); i++) {
            chSerializer.serialize(output, object.charAt(i));
        }
    }
}
