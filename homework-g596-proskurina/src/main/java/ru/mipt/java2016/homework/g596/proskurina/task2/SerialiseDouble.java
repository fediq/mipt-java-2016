package ru.mipt.java2016.homework.g596.proskurina.task2;

/**
 * Created by Lenovo on 31.10.2016.
 */
public class SerialiseDouble implements SerialiserInterface<Double> {
    @Override
    public String serialise(Double inDouble) {
        return inDouble.toString();
    }

    @Override
    public Double deserialise(String inString) {
        return Double.parseDouble(inString);
    }

    @Override
    public String getType() {
        return "Double";
    }
}
