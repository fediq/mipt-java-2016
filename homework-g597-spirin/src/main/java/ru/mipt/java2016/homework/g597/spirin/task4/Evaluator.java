package ru.mipt.java2016.homework.g597.spirin.task4;

/**
 * Created by whoami on 12/13/16.
 */
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class Evaluator implements Calculator {
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

