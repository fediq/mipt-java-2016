package ru.mipt.java2016.homework.g595.topilskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 * A simple calculator using only basic features.
 *
 * @author Artem K. Topilskiy
 * @since 10.10.16
 */
public class SimpleCalculator implements Calculator {
    /**
     * Check that expression contains only recognised symbols
     * and the bracket sequence in expression is correct
     *
     * @param expression
     * @throws ParsingException
     */
    private void check_expression(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is Null");
        }

        if (expression.isEmpty()) {
            throw new ParsingException("Expression is Empty");
        }

        int bracket_sum = 0;
        for (int i = 0; bracket_sum >= 0 && i < expression.length(); ++i) {
            Character c = expression.charAt(i);

            if (!c.isDigit(c)) {
                switch (c) {
                    case '(':
                        ++bracket_sum;
                        break;
                    case ')':
                        --bracket_sum;
                        break;
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '\n':
                    case '\t':
                    case '.':
                    case ' ':
                        break;
                    default:
                        throw new ParsingException("Expression contains Illegal Symbol(s)");
                }
            }
        }

        if (bracket_sum != 0) {
            throw new ParsingException("Expression contains a Faulty Bracket Sequence");
        }
    }

    /**
     * Calculate the arithmetic-containing string expression
     *
     * @param expression - arithmetic-containing string which value is calculated
     * @return calculated expression value
     * @throws ParsingException
     */
    public double calculate(String expression) throws ParsingException {
        check_expression(expression);

        

        return 0.0;
    }
}
