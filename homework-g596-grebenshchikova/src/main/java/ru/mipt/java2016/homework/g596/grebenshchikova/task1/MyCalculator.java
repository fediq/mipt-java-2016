package ru.mipt.java2016.homework.g596.grebenshchikova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

/**
 * Created by liza on 10.10.16.
 */
public class MyCalculator implements Calculator {

    private static final HashSet<Character> operator =
            new HashSet<>(Arrays.asList('+', '-', '*', '/'));


    private enum ParsingCondition {waiting_for_token, reading_number};

    private int operator_priority(char symb) throws ParsingException {
        switch (symb) {
            case '(':
                return 0;
            case ')':
                return 0;
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            case '%':
                return 3;
            default:
                throw new ParsingException("Incorrect character 1");
        }
    }

    private double operator_calculation(char operation, double operand1, double operand2)
            throws ParsingException {
        switch (operation) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                return operand1 / operand2;
            default:
                throw new ParsingException("Incorrect character 2");
        }
    }

    private void close_bracket(boolean unary, Stack<Character> operators, StringBuilder result)
            throws ParsingException {
        unary = false;
        boolean hasOpeningBracket = false;
        while (!operators.empty()) {
            Character curr_char = operators.pop();
            if (curr_char.equals('(')) {
                hasOpeningBracket = true;
                break;
            } else {
                result.append(' ');
                result.append(curr_char);
                result.append(' ');
            }
        }
        if (!hasOpeningBracket) {
            throw new ParsingException("The problem with the brackets");
        }
    }

    private String to_postfix(String expression) throws ParsingException {
        if (expression.length() == 0) {
            throw new ParsingException("Expression is empty");
        }
        StringBuilder result = new StringBuilder();
        Stack<Character> operators = new Stack<Character>();// стек для хранения операторов
        Character symb;
        boolean unary = true; // унарность оператора
        ParsingCondition cond = ParsingCondition.waiting_for_token;
        for (int i = 0; i < expression.length(); ++i) {
            symb = expression.charAt(i);
            switch (cond) {
                case waiting_for_token:
                    if (Character.isDigit(symb)) {
                        cond = ParsingCondition.reading_number;
                        result.append(symb);
                        unary = false;
                    } else if (symb.equals('.')) {
                        throw new ParsingException("Wrong expression 1");
                    } else if (symb.equals('(')) {
                        unary = true;
                        result.append(' ');
                        operators.push(symb);
                        cond = ParsingCondition.waiting_for_token;
                    } else if (symb.equals(')')) {
                        close_bracket(unary, operators, result);
                        cond = ParsingCondition.waiting_for_token;
                    } else if (operator.contains(symb)) {
                        if (unary) {
                            if (symb.equals('-')) {
                                operators.push('%');
                                unary = false;
                            } else if (symb.equals('+')) {
                                unary = false;
                            } else {
                                throw new ParsingException("Wrong expression 2");
                            }
                        } else {
                            unary = true;
                            result.append(' ');
                            while (!operators.empty()) {
                                Character curr_char = operators.pop();
                                if (operator_priority(curr_char) >= operator_priority(symb)) {
                                    result.append(' ');
                                    result.append(curr_char);
                                    result.append(' ');
                                } else {
                                    operators.push(curr_char);
                                    break;
                                }
                            }
                            operators.push(symb);
                        }
                        cond = ParsingCondition.waiting_for_token;
                    } else {
                        throw new ParsingException("Wrong expression 3");
                    }
                    break;
                case reading_number:
                    if (Character.isDigit(symb)) {
                        cond = ParsingCondition.reading_number;
                        result.append(symb);
                        unary = false;
                    } else if (symb.equals('.')) {
                        result.append(symb);
                        cond = ParsingCondition.reading_number;
                    } else if (symb.equals('(')) {
                        throw new ParsingException("Wrong expression 4");
                    } else if (symb.equals(')')) {
                        close_bracket(unary, operators, result);
                        cond = ParsingCondition.waiting_for_token;
                    } else if (operator.contains(symb)) {
                        if (unary) {
                            throw new ParsingException("Wrong expression 6");
                        } else {
                            unary = true;
                            result.append(' ');
                            while (!operators.empty()) {
                                Character curr_char = operators.pop();
                                if (operator_priority(curr_char) >= operator_priority(symb)) {
                                    result.append(' ').append(curr_char).append(' ');
                                } else {
                                    operators.push(curr_char);
                                    break;
                                }
                            }
                            operators.push(symb);
                        }
                        cond = ParsingCondition.waiting_for_token;

                    } else {
                        throw new ParsingException("Wrong expression 7");
                    }
                    break;
                default:
                    throw new ParsingException("Ups... Problem");
            }
        }
        while (!operators.empty()) {
            Character curr_char = operators.pop();
            if (operator.contains(curr_char) || curr_char.equals('%')) {
                result.append(' ').append(curr_char).append(' ');
            } else {
                throw new ParsingException("Wrong expression 8");
            }
        }
        return result.toString();
    }

    private double calculate_postfix_expression(String expression)
            throws ParsingException, ArithmeticException {
        Scanner scan = new Scanner(expression);
        Stack<Double> tmp_results = new Stack<Double>();
        double tmp_result;
        double operand1;
        double operand2;
        double tmp;
        while (scan.hasNext()) {
            String curr_str = scan.next();
            if ((operator.contains(curr_str.charAt(0)))) {
                if (tmp_results.size() >= 2) {
                    operand2 = tmp_results.pop();
                    operand1 = tmp_results.pop();
                    tmp_result = operator_calculation(curr_str.charAt(0), operand1, operand2);
                    tmp_results.push(tmp_result);
                } else {
                    throw new ParsingException("Wrong expression 9");
                }

            } else if ((curr_str.charAt(0) == '%')) {
                if (tmp_results.size() >= 1) {
                    operand1 = tmp_results.pop();
                    operand1 = operand1 * (-1);
                    tmp_results.push(operand1);

                } else {
                    throw new ParsingException("Wrong expression 10");
                }

            } else {
                boolean flag = true; // проверка, является  ли числом
                try {
                    tmp = Double.parseDouble(curr_str);
                } catch (NumberFormatException e) {
                    flag = false;
                    throw new ParsingException("Wrong expression 11");
                }
                if (flag) {
                    tmp_results.push(tmp);
                }

            }
        }
        if (tmp_results.size() == 1) {
            return tmp_results.pop();
        } else {
            throw new ParsingException("Wrong expression 12");
        }

    }

    @Override
    public double calculate(String expression) throws ParsingException, ArithmeticException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        String postfix_expression = to_postfix(expression.replaceAll("\\s", ""));
        double answer = calculate_postfix_expression(postfix_expression);
        return answer;
    }
}