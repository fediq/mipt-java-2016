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
    private static final String ILLEGAL_POSITION_IN_EXPRESSION =
            "Expression contains Illegal Symbol Position(s): ";

    /* An iterator for going through the string expression */
    private int expressionIterator = 0;

    /**
     * Convert a Character to its Double equivalent
     *
     * @param c - character to convert to Double
     * @return Double version value of c
     */
    private Double getDoubleDigit(Character c) {
        return (double) ((int) c - '0');
    }

    /**
     * Read a double number from the expressionIterator position in expression
     *
     * @param expression - the expression String
     * @return value of the read double number
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    private Double readDoubleNumberFromExpression(String expression) throws ParsingException {
        Character c = expression.charAt(expressionIterator);
        Double number = getDoubleDigit(c);

        while (++expressionIterator < expression.length()) {
            c = expression.charAt(expressionIterator);
            if (!Character.isDigit(c)) {
                break;
            }

            number *= 10;
            number += getDoubleDigit(c);
        }
        if (c == '.') {
            if (++expressionIterator == expression.length()) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + c);
            }

            c = expression.charAt(expressionIterator);
            double placeFactor = 10;
            number += getDoubleDigit(c) / placeFactor;

            if (!Character.isDigit(c)) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + c);
            }

            while (++expressionIterator < expression.length()) {
                c = expression.charAt(expressionIterator);
                if (!Character.isDigit(c)) {
                    break;
                }

                placeFactor *= 10;
                number += getDoubleDigit(c) / placeFactor;
            }
        }

        --expressionIterator;
        return number;
    }


    /**
     * Class for holding a calculation token
     */
    private class CalculationToken {
    }

    /**
     * Class for holding an operation and its priority as a token
     */
    private class OperationToken extends CalculationToken {
        static final int UNDEFINED_PRIORITY = -1;
        static final int PLUSMINUS_PRIORITY = 1;
        static final int TIMESDIVIDE_PRIORITY = 2;
        static final int BRACKET_PRIORITY = 3;

        private int priority;
        private Character operation;

        OperationToken(Character operationToWrap) {
            operation = operationToWrap;

            if (operation == null) {
                priority = -1;
            } else {
                switch (operation) {
                    case '+':
                    case '-':
                        priority = PLUSMINUS_PRIORITY;
                        break;
                    case '*':
                    case '/':
                        priority = TIMESDIVIDE_PRIORITY;
                        break;
                    case '(':
                    case ')':
                        priority = BRACKET_PRIORITY;
                        break;
                    default:
                        priority = UNDEFINED_PRIORITY;
                        break;
                }
            }
        }

        int getPriority() {
            return priority;
        }

        Character getOperation() {
            return operation;
        }
    }

    /**
     * Class for holding a number as a token
     */
    private class DoubleToken extends CalculationToken {
        private Double number;

        DoubleToken(Double numberToWrap) {
            number = numberToWrap;
        }

        Double getNumber() {
            return number;
        }
    }

    /**
     * Check whether c is an arithmetic (+ - * /) character
     */
    private boolean cIsArithmetic(Character c) {
        return (c == '+' || c == '-' || c == '*' || c == '/');
    }

    /**
     * Tokenize expression into a stack of numbers and operations.
     * Check that expression contains only recognised symbols in a readable sequence,
     * and has a correct bracket sequence.
     *
     * @param expression - arithmetic-containing string which is converted to tokens
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    private Stack<CalculationToken> tokenize(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is Null");
        }

        if (expression.isEmpty()) {
            throw new ParsingException("Expression is Empty");
        }

        Stack<CalculationToken> calculationTokenStack = new Stack<>();
        Character prevToken = '\0';
        int bracketSum = 0;

        for (expressionIterator = 0; expressionIterator < expression.length(); ++expressionIterator) {
            Character c = expression.charAt(expressionIterator);

            switch (c) {
                case ' ':
                case '\n':
                case '\t':
                    continue;
                case '.':
                    throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + " . ");
                default:
                    break;
            }
            if (!cIsArithmetic(c) && c != '(' && c != ')' && !Character.isDigit(c)) {
                throw new ParsingException("Unexpected symbol");
            }


            if (Character.isDigit(c) && (prevToken == 'n' || prevToken == ')')) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION +
                        " number in unexpected place ");
            }
            if (Character.isDigit(c)) {
                Double number = readDoubleNumberFromExpression(expression);
                calculationTokenStack.push(new DoubleToken(number));
                prevToken = 'n';
                continue;
            }

            if (prevToken == 'n' && c == '(') {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION +
                        " number in unexpected place ");
            }
            if ((prevToken == '\0' || prevToken == '(') && (c != '-' && c != '(')) {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + c);
            }
            if (cIsArithmetic(prevToken) && cIsArithmetic(c) && c != '-') {
                throw new ParsingException(ILLEGAL_POSITION_IN_EXPRESSION + prevToken + c);
            }


            calculationTokenStack.push(new OperationToken(c));

            if (c == '(') {
                ++bracketSum;
            } else if (c == ')') {
                --bracketSum;
            }

            prevToken = c;
        }

        if (cIsArithmetic(prevToken)) {
            throw new ParsingException("Expression ends with an unfinished arithmetic expression");
        }

        if (bracketSum != 0) {
            throw new ParsingException("Expression contains a Faulty Bracket Sequence");
        }

        return calculationTokenStack;
    }

    /**
     * A function returns the value of [first] [operation] [second]
     *
     * @param first     - first operand
     * @param second    - second operand
     * @param operation - the performed operation
     * @return Result of [first] [operation] [second]
     */
    private Double completeOperation(Double first, Double second, Character operation) {
        Double result = 0.0;

        switch (operation) {
            case '+':
                result = first + second;
                break;
            case '-':
                if (first == null) {
                    result = -second;
                } else {
                    result = first - second;
                }
                break;
            case '*':
                result = first * second;
                break;
            case '/':
                result = first / second;
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * A function to read the next number on the stack
     * (allows for reading even in cases of unary minuses)
     *
     * @param calculationStack - Stack to be read from
     * @return The next number on the stack
     */
    private Double readNextNumber(Stack<CalculationToken> calculationStack) {
        CalculationToken calculationToken = calculationStack.pop();
        DoubleToken doubleToken;
        Double nextNumber;

        if (calculationToken instanceof OperationToken) { /* unary minus */
            calculationToken = calculationStack.pop();
            doubleToken = (DoubleToken) calculationToken;
            nextNumber = -doubleToken.getNumber();
        } else {
            doubleToken = (DoubleToken) calculationToken;
            nextNumber = doubleToken.getNumber();
        }

        return nextNumber;
    }

    /**
     * Unravel the Stack for a block of equal-priority operations
     * and replace the block with the result of the operations
     * (shorten [x] [+] [y] ... [-] [z] to [x+y...-z] for example)
     *
     * @param calculationStack - Stack to be unravelled
     * @param endOperationToken - OperationToken whose priority is different
     *                                       from the block being calculated
     */
    private void unravelCalculationStack(Stack<CalculationToken> calculationStack,
                                         OperationToken endOperationToken) {
        OperationToken operationToken;
        Double doubleTokenNumber;
        /* calculationStack has a number (or a unary minus + number) on top */
        Double calculation = readNextNumber(calculationStack);

        while (!calculationStack.isEmpty()) {
            operationToken = (OperationToken) calculationStack.pop();

            if (operationToken.getPriority() == OperationToken.BRACKET_PRIORITY) {
                break;
            }
            if (operationToken.getPriority() == endOperationToken.getPriority()) {
                calculationStack.push(operationToken);
                break;
            }

            doubleTokenNumber = readNextNumber(calculationStack);
            calculation = completeOperation(calculation, doubleTokenNumber,
                                                         operationToken.getOperation());
        }

        calculationStack.push(new DoubleToken(calculation));

        if (cIsArithmetic(endOperationToken.getOperation())) {
            calculationStack.push(endOperationToken);
        }
    }

    /**
     * Calculate the arithmetic-containing string expression.
     *
     * @param expression - arithmetic-containing string which value is calculated
     * @return calculated expression value
     * @throws ParsingException - expression is illegal and cannot be calculated
     */
    public double calculate(String expression) throws ParsingException {
        Stack<CalculationToken> tokenizedExpression = tokenize(expression);
        Stack<CalculationToken> calculationStack = new Stack<>();

        OperationToken prevOperationToken = new OperationToken('\0');

        while (!tokenizedExpression.isEmpty()) {
            CalculationToken token = tokenizedExpression.pop();

            if (token instanceof DoubleToken) {
                calculationStack.push(token);
                continue;
            }
            /* else if (token instanceof OperationToken) */
            OperationToken operationToken = (OperationToken) token;

            if (operationToken.getPriority() == OperationToken.BRACKET_PRIORITY) {
                if (operationToken.getOperation() == '(') {
                    unravelCalculationStack(calculationStack, operationToken);
                } else /* (OperationToken.getOperation() == ')') */ {
                    calculationStack.push(token);
                }
                continue;
            }

            if (operationToken.getPriority() == OperationToken.PLUSMINUS_PRIORITY &&
                    prevOperationToken.getPriority() == OperationToken.TIMESDIVIDE_PRIORITY) {
                unravelCalculationStack(calculationStack, operationToken);
                prevOperationToken = operationToken;
                continue;
            }

            calculationStack.push(token);
            prevOperationToken = operationToken;
        }

        if (calculationStack.isEmpty()) {
            throw new ParsingException("Failed to evaluate expression. Maybe empty?");
        }

        unravelCalculationStack(calculationStack, new OperationToken('\0'));
        return ((DoubleToken) calculationStack.pop()).getNumber();
    }
}
