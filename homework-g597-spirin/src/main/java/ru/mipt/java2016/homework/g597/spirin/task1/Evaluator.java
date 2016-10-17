package ru.mipt.java2016.homework.g597.spirin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by whoami on 10/12/16.
 */

class Evaluator implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null can't be interpreted as an expression");
        }

        if (!checkIfBalanceCorrect(expression)) {
            throw new ParsingException("Incorrect bracket sequence");
        }

        return new EvaluatorHelper(expression).evaluate();
    }

    // Check correctness of bracket sequence
    private boolean checkIfBalanceCorrect(String expression) {
        int balance = 0;

        for (Character ch : expression.toCharArray()) {
            if (balance < 0) {
                return false;
            }
            if (ch == '(') {
                balance++;
            } else if (ch == ')') {
                balance--;
            }

        }

        return balance == 0;
    }
}
