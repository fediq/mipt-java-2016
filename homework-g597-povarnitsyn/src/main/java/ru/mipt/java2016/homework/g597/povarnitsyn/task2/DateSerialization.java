package ru.mipt.java2016.homework.g597.povarnitsyn.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


/**
 * Created by Ivan on 30.10.2016.
 */
public class DateSerialization implements SerializationInterface<Date> {
    @Override
    public Date deserialize(BufferedReader input) throws IOException {
        return new Date(Long.parseLong(input.readLine()));
    }
    @Override
    public void serialize(PrintWriter output, Date object) throws IOException{
        output.println(object.toString());
    }
}
