package ru.mipt.java2016.homework.g596.kozlova.task3;

public class MyStringSerialization implements MySerialization<String> {
    @Override
    public String read(String obj) {
        return obj;
    }

    @Override
    public String write(String s) {
        return s;
    }
}