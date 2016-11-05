package ru.mipt.java2016.homework.g596.proskurina.task2;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class SerialiseString implements SerialiserInterface<String> {


    @Override
    public String serialise(String inString) {
        return inString;
    }

    @Override
    public String deserialise(String inString) {
        return inString;
    }

    @Override
    public String getType() {
        return "String";
    }
}
