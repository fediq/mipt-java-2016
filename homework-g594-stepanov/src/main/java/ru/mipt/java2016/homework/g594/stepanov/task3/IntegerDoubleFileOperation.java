package ru.mipt.java2016.homework.g594.stepanov.task3;


public class IntegerDoubleFileOperation extends FileOperation<Integer, Double> {

    IntegerDoubleFileOperation(String fileName) {
        super(fileName);
        keys = new IntegerSerialisator();
        values = new DoubleSerialisator();
    }

}
