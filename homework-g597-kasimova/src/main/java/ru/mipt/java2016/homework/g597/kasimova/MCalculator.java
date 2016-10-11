package ru.mipt.java2016.homework.g597.kasimova;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Надежда on 11.10.2016.
 **/

@SuppressWarnings("DefaultFileTemplate")
public class MCalculator implements Calculator {
    private ArrayList<String> polish_notation = new ArrayList<>();
    private String operation = "+-/*";


    private String delSpace(String expression) throws ParsingException {
        String temp = "";
        String valid_characters = "0123456789.+-*/() \n\t";
        for (int i = 0; i < expression.length(); ++i) {
            temp = temp + expression.charAt(i);
            if (!valid_characters.contains(temp)) {
                throw new ParsingException("Unknown symbol.\n");
            }
            temp = "";
        }
        expression = expression.replaceAll(" ", "");
        expression = expression.replaceAll("\n", "");
        expression = expression.replaceAll("\t", "");
        if (expression.length() == 0)
            throw new ParsingException("Incorrect expression.\n");
        String new_expression = "";
        String temp_operation = "+-()";

        for (int i = 0; i < expression.length(); ++i) {
            if (i > 0 && expression.charAt(i) == '-' && (expression.charAt(i - 1) == '/' || expression.charAt(i - 1) == '*'))
                new_expression = new_expression + "(0-1)" + expression.charAt(i - 1);
            else {
                if (expression.charAt(i) == '-') {
                    if (i == 0)
                        new_expression = new_expression + '0';
                    else {
                        temp = temp + expression.charAt(i - 1);
                        if (temp_operation.contains(temp))
                            new_expression = new_expression + '0';
                    }
                }
                new_expression = new_expression + expression.charAt(i);
            }
            temp = "";
        }
        return new_expression;

    }

    private void getPolishNotation(String expression) throws ParsingException {
        String number = "";

        ArrayList<String> stack = new ArrayList<>();

        String temp = "";
        Map<String, Integer> get_prior = new HashMap<String, Integer>() {{
            put("(", 1);
            put(")", 1);
            put("+", 2);
            put("-", 2);
            put("*", 3);
            put("/", 3);
        }};
        int i = 0;
        int brackets_balance = 0;
        int points_counter;
        while (i < expression.length()) {
            points_counter = 0;
            if (expression.charAt(i) >= '0' && expression.charAt(i) <= '9' || expression.charAt(i) == '.') {
                while ((i < expression.length()) && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    if (expression.charAt(i) == '.')
                        points_counter++;
                    number = number + expression.charAt(i);
                    i++;
                }
                if (points_counter > 1) {
                    throw new ParsingException("Incorrect expression.\n");

                }
                polish_notation.add(number);
                number = "";
                if (i >= expression.length())
                    i -= 1;
            }
            temp = "";
            temp = temp + expression.charAt(i);
            if (expression.charAt(i) == '(') {
                stack.add(temp);
                brackets_balance++;
            }
            if (expression.charAt(i) == ')') {
                if (i > 0 && expression.charAt(i - 1) == '(') {
                    throw new ParsingException("Empty brackets.\n");
                }
                brackets_balance--;
                temp = "";
                temp = temp + '(';
                while (!stack.isEmpty() && temp.compareTo(stack.get(stack.size() - 1)) != 0) {
                    polish_notation.add(stack.get(stack.size() - 1));
                    stack.remove(stack.size() - 1);
                }
                if (!stack.isEmpty())
                    stack.remove(stack.size() - 1);
            }
            if (operation.contains(temp)) {

                while (!stack.isEmpty() && get_prior.get(temp) <= get_prior.get(stack.get(stack.size() - 1))) {
                    polish_notation.add(stack.get(stack.size() - 1));
                    stack.remove(stack.size() - 1);
                }
                stack.add(temp);
            }
            if (brackets_balance < 0) {
                throw new ParsingException("Wrong balance of the brackets.\n");
            }
            temp = "";
            i += 1;
        }
        if (brackets_balance != 0) {
            throw new ParsingException("Wrong balance of the brackets.\n");
        }
        while (!stack.isEmpty()) {
            polish_notation.add(stack.get(stack.size() - 1));
            stack.remove(stack.size() - 1);
        }
    }

    private double calculateValue() throws ParsingException {
        ArrayList<Double> result = new ArrayList<>();
        double temp;
        int position = -1;
        for (String aPolish_notation : polish_notation) {
            if (!operation.contains(aPolish_notation)) {
                position++;
                temp = Double.parseDouble(aPolish_notation);
                result.add(temp);
            } else {
                if (aPolish_notation.compareTo("*") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) * result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
                if (aPolish_notation.compareTo("/") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) / result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
                if (aPolish_notation.compareTo("+") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) + result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
                if (aPolish_notation.compareTo("-") == 0) {
                    if (result.size() > 1) {
                        result.set(position - 1, result.get(position - 1) - result.get(position));
                        result.remove(result.size() - 1);
                        position--;
                    } else {
                        throw new ParsingException("Incorrect expression.\n");
                    }
                }
            }
        }
        if (position < 0)
            throw new ParsingException("Incorrect expression.\n");
        return result.get(position);
    }

    private double getResult(String expression) throws ParsingException {
        expression = delSpace(expression);
        getPolishNotation(expression);
        return calculateValue();
    }


    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression.\n");
        }
        if (expression.length() == 0) {
            throw new ParsingException("Incorrect expression.\n");
        }
        return getResult(expression);
    }
}