package ru.mipt.java2016.homework.g594.stepanov.task3;

public class IntegerSerialisator extends ObjectSerialisator<Integer> {

    @Override
    public String toString(Integer object) {
        StringBuilder sb = new StringBuilder(object.toString());
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public Integer toObject(String s) {
        return Integer.parseInt(s);
    }
}
