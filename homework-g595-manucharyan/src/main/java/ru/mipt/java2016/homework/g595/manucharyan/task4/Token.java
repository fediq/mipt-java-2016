package ru.mipt.java2016.homework.g595.manucharyan.task4;

import sun.print.SunMinMaxPage;

import jdk.nashorn.internal.ir.Symbol;

/**
 * Created by op on 17.12.2016.
 */
public class Token {

    public Token(RESTCalc.Symbol symbol_) {
        symbol = symbol_;
        valency = getValencyForOperator(symbol_);
    }

    public Token(RESTCalc.Symbol symbol_, double value_) {
        this(symbol_);
        value = value_;
    }

    public Token(RESTCalc.Symbol symbol_, String name_, int valency_) {
        this(symbol_);
        name = name_;
        valency = valency_;
    }

    public double value; // for numbers
    public String name; // for functions and variables
    public int valency = 0; // for functions
    public RESTCalc.Symbol symbol = RESTCalc.Symbol.NONE;

    private int getValencyForOperator(RESTCalc.Symbol s) {
        switch (s) {
            case ADD:
                return 2;
            case UNOADD:
                return 1;
            case SUB:
                return 2;
            case UNOSUB:
                return 1;
            case MUL:
                return 2;
            case DIV:
                return 2;
            default:
                return 0;
        }
    }
}
