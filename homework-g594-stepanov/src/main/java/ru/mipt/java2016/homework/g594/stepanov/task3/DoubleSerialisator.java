package ru.mipt.java2016.homework.g594.stepanov.task3;

public class DoubleSerialisator extends ObjectSerialisator<Double> {

    @Override
    public String toString(Double object) {
        StringBuilder sb = new StringBuilder(object.toString());
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public Double toObject(String s) {
        return Double.parseDouble(s);
    }
}
