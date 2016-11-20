package ru.mipt.java2016.homework.g596.kozlova.task3;

public class MyIntegerSerialization implements MySerialization<Integer>{
    @Override
    public String write(Integer obj) { return obj.toString(); }

    @Override
    public Integer read(String s) {
        return Integer.parseInt(s);
    }
}
