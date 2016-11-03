package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * Created by Ivan on 03.11.2016.
 */
public class CharacterSerialization implements SerializationInterface<Character> {
    @Override
    public Character deserialize(BufferedReader input) throws IOException {
        return (char) input.read();
    }

    @Override
    public void serialize(PrintWriter output, Character object) throws IOException {
        output.print(object.toString());
    }
}