package ru.mipt.java2016.homework.g595.tkachenko.task1;

import java.util.Stack;
import java.util.regex.Pattern;
import java.util.Scanner;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Калькулятор, преобразующий входящую строку в ОПЗ и считающий её значение.
 *
 * by Dmitry Tkachenko and just me and google for now, 10.10.2016
 *
 */


public class MyCalc implements Calculator {

    private static class RPN {

        private static Boolean isOperator(Character c) {
            return Pattern.matches("(\\+)|(\\-)|(\\/)|(\\*)|(\\~)", c.toString());
        }

        private static Boolean isBrace(Character c) {
            return Pattern.matches("(\\()|(\\))", c.toString());
        }

        private static Boolean isDelimiter(Character c) {
            return Pattern.matches("\\s", c.toString());
        }

        private static Boolean isDigit(Character c) {
            return Pattern.matches("[0-9]|(\\.)", c.toString());
        }

        private static Byte getPriority(Character c) {
            switch (c) {
                case '(': return 0;
                case ')': return 0;
                case '+': return 1;
                case '-': return 1;
                case '*': return 2;
                case '/': return 2;
                case '^': return 3;
                default: return -1;
            }
        }

        private static String getExpression(String input) throws ParsingException {

            StringBuilder output = new StringBuilder();
            Stack<Character> operations = new Stack<>();
            Boolean isUnary = true;

            if (input == null) {
                throw new ParsingException("Input is null!");
            }

            for (Character c : input.toCharArray()) {

                if (RPN.isDigit(c)) {
                    isUnary = false;
                    output.append(c);
                    continue;
                }

                if (RPN.isOperator(c)) {

                    if (isUnary) {

                        if (c.equals('-')) {
                            operations.push('~');
                            isUnary = false;
                            continue;
                        } else {
                            if (c.equals('+')) {
                                isUnary = false;
                                continue;
                            } else {
                                throw new ParsingException("Expression is illegal!");
                            }

                        }

                    }

                    if (!isUnary) {
                        isUnary = true;
                        output.append(' ');
                        while (!operations.empty()) {
                            Character top = operations.pop();
                            if (RPN.getPriority(c) <= RPN.getPriority(top)) {
                                output.append(' ').append(top).append(' ');
                            } else {
                                operations.push(top);
                                break;
                            }
                        }
                        operations.push(c);
                        continue;
                    }
                }

                if (RPN.isBrace(c)) {

                    if (c.equals('(')) {
                        isUnary = true;
                        output.append(' ');
                        operations.push(c);
                        continue;
                    }

                    if (c.equals(')')) {
                        isUnary = false;
                        boolean bracketBalance = false;
                        while (!operations.empty()) {
                            Character top = operations.pop();
                            if (top.equals('(')) {
                                bracketBalance = true;
                                break;
                            } else {
                                output.append(' ').append(top).append(' ');
                            }
                        }
                        if (!bracketBalance) {
                            throw new ParsingException("Brackets balance gone bad.");
                        }
                        continue;
                    }
                }

                if (RPN.isDelimiter(c)) {
                    output.append(' ');
                }

                if (!RPN.isOperator(c) && !RPN.isBrace(c) && !RPN.isDigit(c) && !RPN.isDelimiter(c)) {
                    throw new ParsingException("Expression is illegal!");
                }
            }

            while (!operations.empty()) {
                Character top = operations.pop();
                if (RPN.isOperator(top)) {
                    output.append(' ').append(top).append(' ');
                } else {
                    throw new ParsingException("Expression is illegal!");
                }
            }

            return output.toString();
        }

        private static double makeOperation(double d1, double d2, Character op) {
            switch (op) {
                case '+' : return d1 + d2;
                case '-' : return d1 - d2;
                case '*' : return d1 * d2;
                case '/' : return d1 / d2;
                default : return 0.0;
            }
        }

        private static double counting(String input) throws ParsingException {

            Scanner sc = new Scanner(input);
            Stack<Double> operands = new Stack<>();

            while (sc.hasNext()) {

                String s = sc.next();

                if (s.length() == 1 && RPN.isOperator(s.charAt(0)) && (s.charAt(0) != '~')) {
                    if (operands.size() >= 2) {
                        double top2 = operands.pop();
                        double top1 = operands.pop();
                        double result = makeOperation(top1, top2, s.charAt(0));
                        operands.push(result);
                    } else {
                        throw new ParsingException("Expression is invalid!");
                    }
                } else if (s.length() == 1 && s.charAt(0) == '~') {
                    if (operands.size() >= 1) {
                        double top = operands.pop();
                        operands.push(-1 * top);
                    } else {
                        throw new ParsingException("Expresion is invalid!");
                    }
                } else {
                    try  {
                        Double number = Double.parseDouble(s);
                        operands.push(number);
                    } catch (NumberFormatException nfe) {
                        throw new ParsingException("Expression is invalid!");
                    }
                }
            }

            if (operands.size() == 1) {
                return operands.pop();
            } else {
                throw new ParsingException("Expression is invalid!");
            }
        }

        public static double calculate(String input) throws ParsingException {
            String output;
            output = RPN.getExpression(input);
            double result = counting(output);
            return result;
        }

    }

    @Override
    public double calculate(String expression) throws ParsingException {
        return RPN.calculate(expression);
    }

}