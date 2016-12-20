package ru.mipt.java2016.homework.g596.litvinov.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 20.12.16.
 */
public class Calculator implements ru.mipt.java2016.homework.base.task1.Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null input");
        }
        if (!checkRightBraceSequence(expression)) {
            throw new ParsingException("Invalid braces numbber");
        }
        return new GrammarCalculator(expression).calculate();
    }

    private boolean checkRightBraceSequence(String expression) {
        int braceBalance = 0;
        for (Character ch : expression.toCharArray()) {
            if (braceBalance < 0) {
                return false;
            }
            if (ch == '(') {
                braceBalance++;
            } else if (ch == ')') {
                braceBalance--;
            }
        }
        return (braceBalance == 0);
    }
}

