package ru.mipt.java2016.homework.g599.trotsiuk.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;


public abstract class Lexeme {

    public static Operator fromString(String s) throws ParsingException {
        if (s.length() < 1) {
            throw new ParsingException("Empty string is not a lexeme");
        }
        switch (s.charAt(0)) {
            case '+':
                return new PlusOperator();
            case '-':
                return new BinaryMinusOperator();
            case '*':
                return new MultiplyOperator();
            case '/':
                return new DivideOperator();
            case '~':
                return new UnaryMinusOperator();
            case '(':
                return new OpenParenthesisOperator();
            case ')':
                return new CloseParenthesisOperator();
            default:
                return new NumberOperator(s);
        }
    }



}
