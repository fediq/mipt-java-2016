package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

class IntegerDoubleSerializer extends ObjectSerializer<Integer, Double> {

    IntegerDoubleSerializer(String newPath) {
        super(newPath);
    }

    @Override
    protected Pair<Integer, Double> convert() {
        int border = lastRead.indexOf(":");
        Integer key = Integer.parseInt(lastRead.substring(0, border));
        Double value = Double.parseDouble(lastRead.substring(border + 1, lastRead.length()));
        return new Pair(key, value);
    }

    @Override
    void write(Integer key, Double value) {
        outputStream.print(key);
        outputStream.print(":");
        outputStream.print(value);
        outputStream.print("\n");
    }
}
