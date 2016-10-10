package ru.mipt.java2016.homework.g595.topilskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import java.text.ParseException;
import java.util.Stack;

/**
 * A simple calculator using only basic features.
 *
 * @author Artem K. Topilskiy
 * @since 10.10.16
 */
class SimpleCalculator implements Calculator {
    private final static String ILLEGAL_POSITION_IN_EXPRESSION =
                                "Expression contains Illegal Symbol Position(s)";

    private int expression_iterator = 0;

    /**
     * Check that expression contains only recognised symbols
     * and the bracket sequence in expression is correct.
     *
     * @param expression - arithmetic-containing string which correctness is checked
     * @throws ParsingException - expression is illegal and cannot be calculated
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
            char c = expression.charAt(i);

            if (!Character.isDigit(c)) {
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
     * Class for holding an intermediate calculation.
     * Mostly for handling intermediate calculation of a bracket element
     */
    private class CalculationSnapshot {
        Double calculation;
        Character operation;

        CalculationSnapshot(Double calculation_init, Character operation_init) {
            calculation = calculation_init;
            operation = operation_init;
        }
    }

    /**
     * A function returns the value of [first] [operation] [second]
     *
     * @param first - first operand
     * @param second - second operand
     * @param operation - the performed operation
     * @return Result of [first] [operation] [second]
     */
    private Double complete_operation(Double first, Double second, Character operation)
                                                                throws ParsingException {
        if (operation == null || second == null || (first == null && operation != '-')) {
            throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION);
        }

        Double result = 0.0;

        switch(operation) {
            case '+':
                result = first + second;
                break;
            case '-':
                if (first == null) {
                    result = -second;
                }
                else {
                    result = first - second;
                }
                break;
            case '*':
                result = first * second;
                break;
            case '/':
                result = first / second;
                break;
        }

        return result;
    }

    /**
     * Convert a Character to its Double equivalent
     *
     * @param c - character to convert to Double
     * @return Double version value of c
     */
    private Double get_double_digit(Character c) {
        return (double)((int)c - '0');
    }

    /**
     * Read a double number from the expression_iterator position in expression
     *
     * @param expression - the expression String
     * @return value of the read double number
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    private Double read_double_number_from_expression(String expression) throws ParsingException {
        Character c = expression.charAt(expression_iterator);
        Double number = get_double_digit(c);

        for(; expression_iterator < expression.length() && Character.isDigit(c); ++expression_iterator) {
            c = expression.charAt(expression_iterator);
            number *= 10;
            number += get_double_digit(c);
        }
        if (c == '.') {
            if (++expression_iterator == expression.length()) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION);
            }

            double place_factor = 1;

            c = expression.charAt(expression_iterator);
            place_factor *= 10;
            number += get_double_digit(c) / place_factor;

            if (!Character.isDigit(c)) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION);
            }

            for(; expression_iterator < expression.length() && Character.isDigit(c); ++expression_iterator) {
                c = expression.charAt(expression_iterator);
                place_factor *= 10;
                number += get_double_digit(c) / place_factor;
            }
        }

        return number;
    }

    /**
     * Calculate the arithmetic-containing string expression.
     *
     * @param expression - arithmetic-containing string which value is calculated
     * @return calculated expression value
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    public double calculate(String expression) throws ParsingException {
        check_expression(expression);

        Stack<CalculationSnapshot> calculation_stack = new Stack<>();

        Character operation = null;
        Double calculation = null;
        Double number = null;

        for (expression_iterator = 0; expression_iterator < expression.length();
                                            ++expression_iterator, number = null) {
            Character c = expression.charAt(expression_iterator);

            if (c == ' ' || c == '\n' || c == '\t') {
                continue;
            }

            if (c == '.' || (calculation != null && operation == null && Character.isDigit(c))) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " 1 ");
            }

            if (c == '(') {
                calculation_stack.push(new CalculationSnapshot(calculation, operation));
                calculation = null;
                operation = null;
            } else if (c == ')') {
                CalculationSnapshot last_snapshot = calculation_stack.pop();
                calculation = complete_operation(calculation, last_snapshot.calculation,
                                                              last_snapshot.operation);
                operation = last_snapshot.operation;
            }

            if (Character.isDigit(c)) {
                number = read_double_number_from_expression(expression);
            }

            if (number == null) { /* c == '+' '-' '*' '/' */
                if (operation != null) {
                    throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " 2");
                } else {
                    operation = c;
                }
            }
            else {
                if (operation == null) {
                    if (calculation == null) {
                        calculation = number;
                    } else {
                        throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " 3");
                    }
                } else {
                    calculation = complete_operation(calculation, number, operation);
                    operation = null;
                }
            }
        }

        if (calculation == null) {
            throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " 4");
        }
        return calculation;
    }
}
