package ru.mipt.java2016.homework.g596.proskurina.task2;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class SerialiseInteger implements SerialiserInterface<Integer> {

    @Override
    public String serialise(Integer inInteger) {
        return inInteger.toString();
    }

    @Override
    public Integer deserialise(String inString) {
        return Integer.parseInt(inString);
    }

    @Override
    public String getType() {
        return "Integer";
    }
}
