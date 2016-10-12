package ru.mipt.java2016.homework.g595.topilskiy.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import java.util.Stack;

/**
 * A simple calculator using only basic features.
 *
 * @author Artem K. Topilskiy
 * @since 10.10.16
 */
class SimpleCalculator implements Calculator {
    /* A ParsingException standard error string */
    private final static String ILLEGAL_POSITION_IN_EXPRESSION =
                                "Expression contains Illegal Symbol Position(s): ";

    /* An iterator for going through the string expression */
    private int expression_iterator = 0;

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

        while(++expression_iterator < expression.length()) {
            c = expression.charAt(expression_iterator);
            if (!Character.isDigit(c))
                break;

            number *= 10;
            number += get_double_digit(c);
        }
        if (c == '.') {
            if (++expression_iterator == expression.length()) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + c);
            }

            c = expression.charAt(expression_iterator);
            double place_factor = 10;
            number += get_double_digit(c) / place_factor;

            if (!Character.isDigit(c)) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + c);
            }

            while(++expression_iterator < expression.length()) {
                c = expression.charAt(expression_iterator);
                if (!Character.isDigit(c))
                    break;

                place_factor *= 10;
                number += get_double_digit(c) / place_factor;
            }
        }

        --expression_iterator;
        return number;
    }



    /**
     * Class for holding a calculation token
     */
    private class CalculationToken {}

    /**
     * Class for holding an operation and its priority as a token
     */
    private class OperationToken extends CalculationToken {
        int priority;
        Character operation;

        OperationToken(Character operation_to_wrap) {
            operation = operation_to_wrap;

            if (operation == null) {
                priority = -1;
            }
            else {
                switch (operation) {
                    case '+':
                    case '-':
                        priority = 1;
                        break;
                    case '*':
                    case '/':
                        priority = 2;
                        break;
                    case '(':
                    case ')':
                        priority = 3;
                        break;
                    default:
                        priority = -1;
                        break;
                }
            }
        }
    }

    /**
     * Class for holding a number as a token
     */
    private class DoubleToken extends CalculationToken {
        Double number;

        DoubleToken(Double number_to_wrap) {
            number = number_to_wrap;
        }
    }


    /**
     * Tokenise expression into a stack of numbers and operations.
     * Check that expression contains only recognised symbols in a readable sequence,
     * and has a correct bracket sequence.
     *
     * @param expression - arithmetic-containing string which is converted to tokens
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    private Stack<CalculationToken> tokenise(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is Null");
        }

        if (expression.isEmpty()) {
            throw new ParsingException("Expression is Empty");
        }

        Stack<CalculationToken> calculation_token_stack = new Stack<>();
        boolean prev_token_is_a_number_token = false;
        int bracket_sum = 0;

        for (expression_iterator = 0; expression_iterator < expression.length(); ++expression_iterator) {
            Character c = expression.charAt(expression_iterator);

            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                    continue;
                case '.':
                    throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " . ");
            }

            if (Character.isDigit(c) && prev_token_is_a_number_token) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION +
                        " Two numbers back to back ");
            }
            if (Character.isDigit(c)) {
                Double number = read_double_number_from_expression(expression);
                calculation_token_stack.push(new DoubleToken(number));
                prev_token_is_a_number_token = true;
                continue;
            }

            if (c == '(') {
                ++bracket_sum;
            } else {
                --bracket_sum;
            }

            calculation_token_stack.push(new OperationToken(c));
        }

        if (bracket_sum != 0) {
            throw new ParsingException("Expression contains a Faulty Bracket Sequence");
        }

        return calculation_token_stack;
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
     * Calculate the arithmetic-containing string expression.
     *
     * @param expression - arithmetic-containing string which value is calculated
     * @return calculated expression value
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    public double calculate(String expression) throws ParsingException {
        Stack<CalculationToken> calculation_stack = tokenise(expression);

//        Character operation = null;
//        OperationWrapper operation_wrap = new OperationWrapper('\0');
//        Double calculation = null;
//        Double number = null;
//
//        for (expression_iterator = 0; expression_iterator < expression.length(); ++expression_iterator) {
//            Character c = expression.charAt(expression_iterator);
//
//            switch (c) {
//                case ' ':
//                case '\n':
//                case '\t':
//                    continue;
//                case '.':
//                    throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " . ");
//            }
//            if (Character.isDigit(c) && number != null) {
//                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION +
//                        " Two numbers back to back ");
//            }
//
//
//            if (Character.isDigit(c) && number == null) {
//                number = read_double_number_from_expression(expression);
//                continue;
//            } else {
//                operation_wrap.setter(c);
//            }
//
//
//            if (operation_wrap.priority == 3) {
//                if (operation_wrap.operation == '(') {
//                    if (number != null) {
//                        throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " Number( ");
//                    }
//
//                    calculation_stack.push(new CalculationSnapshot(calculation, operation));
//                    calculation = null;
//                    operation_wrap.setter('\0');
//                    continue;
//                } else if (operation_wrap.operation == ')') {
//                    if (calculation == null) {
//                        throw new ParsingException("Empty Braces");
//                    }
//
//
//                    number = calculation;
//                    operation_wrap.setter('\0');
//                    continue;
//                }
//            }
//
//
//            if (number == null) { /* c == '+' '-' '*' '/' */
//                if (operation != null) {
//                    throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " 2");
//                } else {
//                    operation = c;
//                }
//            } else {
//                if (operation == null) {
//                    if (calculation == null) {
//                        calculation = number;
//                    } else {
//                        throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " 3");
//                    }
//                } else {
//                    calculation = complete_operation(calculation, number, operation);
//                    operation = null;
//                }
//            }
//        }
//
//        if (calculation == null) {
//            throw new ParsingException("Resulting calculation is empty");
//        }
//
//        return calculation;

        return 0.0;
    }
}
