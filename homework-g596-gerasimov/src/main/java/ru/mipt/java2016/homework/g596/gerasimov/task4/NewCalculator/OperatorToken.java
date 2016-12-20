package ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator;

/**
 * Created by geras-artem on 19.12.16.
 */
public class OperatorToken extends Token {
    private Operator op;

    public OperatorToken(char c) {
        switch (c) {
            case '+':
                op = Operator.PLUS;
                break;
            case '-':
                op = Operator.MINUS;
                break;
            case '*':
                op = Operator.MULTIPLY;
                break;
            case '/':
                op = Operator.DIVIDE;
                break;
            case '#':
                op = Operator.U_PLUS;
                break;
            case '&':
                op = Operator.U_MINUS;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Operator getOperator() {
        return op;
    }
}

