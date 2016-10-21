package ru.mipt.java2016.homework.g597.komarov.task1;

public class Token {
    private double doubleToken;
    private char charToken;
    private boolean type; //true - double, false - char

    public Token(double value) {
        type = true;
        doubleToken = value;
    }

    public Token(char value) {
        type = false;
        charToken = value;
    }

    public double getDouble() {
        return doubleToken;
    }

    public char getChar() {
        return charToken;
    }

    public boolean getType() {
        return type;
    }
}
