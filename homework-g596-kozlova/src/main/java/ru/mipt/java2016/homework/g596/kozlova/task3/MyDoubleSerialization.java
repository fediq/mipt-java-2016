package ru.mipt.java2016.homework.g596.kozlova.task3;

public class MyDoubleSerialization implements MySerialization<Double> {
    @Override
    public String write(Double obj) {
        return obj.toString();
    }

    @Override
    public Double read(String s) {
        return Double.parseDouble(s);
    }
}
