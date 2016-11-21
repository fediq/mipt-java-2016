package ru.mipt.java2016.homework.g594.stepanov.task3;

public class StringSerialisator extends ObjectSerialisator<String> {

    @Override
    public String toString(String object) {
        return object + "\n";
    }

    @Override
    public String toObject(String s) {
        return s;
    }
}
