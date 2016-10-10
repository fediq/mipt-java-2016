package ru.mipt.java2016.homework.g597.grishutin.task1;

import ru.mipt.java2016.homework.base.task1.*;

import java.security.InvalidParameterException;
import java.util.*;

class MyCalculator implements Calculator{

    static final Calculator INSTANCE = new MyCalculator();
    private static final HashSet<String> OPERATORS = new HashSet<>(Arrays.asList(
            "+", "-", "*", "/", "^"
    ));

    private enum ParsingCondition {WaitingToken, ReadingNumber}

    private boolean IsOperator(String s) {
        return OPERATORS.contains(s);
    }


    @Override
    public double calculate (String expression) throws ParsingException, ArithmeticException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        return evaluatePostfix(infixToPostfix(expression.replaceAll("\\s", "")));
    }


    private static double count(String operator, double op1, double op2) throws InvalidParameterException {
        switch (operator) {
            case "+":
                return op1 + op2;
            case "-":
                return op1 - op2;
            case "*":
                return op1 * op2;
            case "/":
                return op1 / op2;
            case "^":
                return Math.pow(op1, op2);
            default:
                throw new InvalidParameterException(String.format("Invalid operator %s", operator));
        }
    }

    private static int getPriority(String operator) throws InvalidParameterException {
        switch (operator) {
            case "+":
                return 1;
            case "-":
                return 1;
            case "*":
                return 2;
            case "/":
                return 2;
            case "(":
                return 0;
            case ")":
                return 0;
            case "&":
                return 3;
            default:
                throw new InvalidParameterException(
                        String.format("Invalid operator: %s", operator));
        }
    }

    private double evaluatePostfix(String postfix) throws ParsingException {
        Stack<Double> operands = new Stack<>();
        Double operand1, operand2;
        Double result = 0.;
        String token;
        try (Scanner sc = new Scanner(postfix)) {
            while (sc.hasNext()) {
                token = sc.next();
                if (IsOperator(token)) {
                    if (operands.size() < 2) {
                        throw new ParsingException("Incorrect expression");
                    }
                    operand2 = operands.pop();
                    operand1 = operands.pop();
                    try {
                        result = count(token, operand1, operand2);
                    } catch (InvalidParameterException ipe) {
                        throw new ParsingException(String.format("Incorrect expression: %s", ipe.getMessage()));
                    } finally {
                        operands.push(result);
                    }

                } else if (token.equals("#")) {
                  if (operands.size() < 1) {
                      throw new ParsingException("Invalid expression: expected number near unary -");
                  } else {
                      double operand = operands.pop();
                      operands.push(-1 * operand);
                  }
                } else {
                    try {
                        operand1 = Double.parseDouble(token);
                        operands.push(operand1);
                    } catch (NumberFormatException npe) {
                        throw new ParsingException(String.format("Invalid operand: %s", token));
                    }
                }
            }
            if (operands.size() != 1) { // we expect result to be only resulting number in stack
                throw new ParsingException("Incorrect expression");
            } else {
                return operands.pop(); // and here it goes
            }
        }
    }

    private String infixToPostfix(String expression) throws ParsingException {
        System.out.println(expression);
        StringBuilder answer = new StringBuilder();
        Stack<String> operators = new Stack<>();
        Character c;
        boolean unary = true; // true if next met operator is unary
        ParsingCondition cond = ParsingCondition.WaitingToken;
        for (int i = 0; i < expression.length(); ++i) {
            c = expression.charAt(i);
            if (Character.isDigit(c)) {
                unary = false;
                answer.append(c);
                cond = ParsingCondition.ReadingNumber;
            } else if (c.equals('.')) {
                if (cond == ParsingCondition.WaitingToken)
                    throw new ParsingException("Invalid expression: unexpected symbol '.'");
                else {
                    answer.append('.');
                    cond = ParsingCondition.ReadingNumber;
                }
            } else if (c.equals('(')) {
                if (cond == ParsingCondition.ReadingNumber)
                    throw new ParsingException("Invalid expression: unexpected symbol '('");
                else {
                    unary = true;
                    answer.append(' ');
                    operators.push("(");

                    cond = ParsingCondition.WaitingToken;
                }
            } else if (c.equals(')')) {
                if (cond == ParsingCondition.WaitingToken)
                    throw new ParsingException("Invalid expression: unexpected ) after operator");
                else {
                    boolean hasCorrespondingOpeningBracket = false;
                    while (!(operators.empty())) {
                        String curOperator = operators.pop();
                        if (curOperator.equals("(")) {
                            hasCorrespondingOpeningBracket = true;
                            break;
                        } else {
                            answer.append(' ').append(curOperator).append(' ');
                        }
                    }
                    if (!hasCorrespondingOpeningBracket) {
                        throw new ParsingException("Invalid expression: invalid brackets");
                    }

                }
            } else if (IsOperator(c.toString())) {
                if (unary) {
                    switch (c.toString()) {
                        case "-":
                            operators.push("#");
                        case "+":
                            unary = true;
                            break;
                        default:
                            throw new ParsingException(
                                    String.format("Invalid expression: invalid unary operator: %c", c));
                    }
                } else {
                    unary = true;
                    answer.append(' ');
                    int curOperatorPriority = getPriority(c.toString());
                    while (!(operators.empty()) && curOperatorPriority <= getPriority(operators.peek())) {
                        answer.append(' ').append(operators.pop()).append(' ');
                    }
                    operators.push(c.toString());
                }

                cond = ParsingCondition.WaitingToken;
            } else {
                throw new ParsingException(String.format("Invalid expression: unexpected %s", c.toString()));
            }
        }
        while (!operators.empty()) {
            String curOperator = operators.pop();
            System.out.println(String.format("Found %s in stack", curOperator));
            if (OPERATORS.contains(curOperator) || curOperator.equals("#")) {
                answer.append(String.format(" %s ", curOperator));
            } else {
                throw new ParsingException(String.format("Invalid expression: unexpected %s", curOperator));
            }
        }
        return answer.toString();
    }
}