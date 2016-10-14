package ru.mipt.java2016.homework.g000.komarov.task1;

public class Token {
    double doubleToken;
    char charToken;
    boolean type; //true - double, false - char

    public Token(double value) {
        type = true;
        doubleToken = value;
    }

    public Token(char value) {
        type = false;
        charToken = value;
    }
}
