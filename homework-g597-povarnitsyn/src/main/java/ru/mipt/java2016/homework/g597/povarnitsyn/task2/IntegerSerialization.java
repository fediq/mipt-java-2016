package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Ivan on 30.10.2016.
 */
public class IntegerSerialization implements SerializationInterface<Integer> {

    IntegerSerialization(){}
    @Override
    public Integer deserialize(BufferedReader input) throws IOException{
         return Integer.parseInt(input.readLine());
     }
    @Override
    public void serialize(PrintWriter output, Integer object) throws IOException{
        output.println(object.toString());
    }
}
