package ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator;

/**
 * Created by geras-artem on 19.12.16.
 */
public class BracketToken extends Token {
    private boolean opening;

    public BracketToken(char c) {
        switch (c) {
            case '(':
                opening = true;
                break;
            case ')':
                opening = false;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public boolean isOpening() {
        return opening;
    }
}
