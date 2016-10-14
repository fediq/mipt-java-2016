package ru.mipt.java2016.homework.g000.komarov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

public class BinOperator {
    private double firstArg, secondArg;
    private char operator;

    public BinOperator(double a, double b, char op){
        firstArg = a;
        secondArg = b;
        operator = op;
    }
    public double run() throws ParsingException{
        if (operator == '+'){
            return firstArg + secondArg;
        }
        if (operator == '-'){
            return firstArg - secondArg;
        }
        if (operator == '*'){
            return firstArg * secondArg;
        }
        if (operator == '/'){
            return firstArg / secondArg;
        }
        else throw new ParsingException("Wrong expression");
    }
}
