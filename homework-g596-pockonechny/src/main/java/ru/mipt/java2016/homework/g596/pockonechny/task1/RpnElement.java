package ru.mipt.java2016.homework.g596.pockonechny.task1;

/**
 * Created by celidos on 13.10.16.
 */

public class RpnElement {
    private char operation;               // Определяет тип содержимого: число или операция
    private double value;                 // Если число, то чему оно равно

    public char getOp()      { return operation; }
    public double getValue() { return value;     }

    RpnElement(char _operation, double _value) {
        operation = _operation;
        value = _value;
    }
}
