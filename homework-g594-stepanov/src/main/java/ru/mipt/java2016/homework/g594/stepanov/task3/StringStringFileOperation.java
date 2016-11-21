package ru.mipt.java2016.homework.g594.stepanov.task3;

public class StringStringFileOperation extends FileOperation<String, String> {

    StringStringFileOperation(String fileName) {
        super(fileName);
        keys = new StringSerialisator();
        values = new StringSerialisator();
    }
}