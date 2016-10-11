package ru.mipt.java2016.homework.g594.stepanov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilia on 11.10.16.
 */
public class CalculatorImplementation implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        s = expression;
        // validity test passed: allowed symbols only
        // bracket balance, no two signs in a row
        double result;
        try {
            result = f(0, s.length() - 1);
        } catch (ParsingException e) {
            throw e;
        }
        return result;
    }

    private String s;

    private double f(int lf, int rg) throws ParsingException {
        if (s.charAt(lf) != '-' && !(s.charAt(lf) >= '0' && s.charAt(lf) <= '9') && s.charAt(lf) != '(') {
            throw new ParsingException("Invalid first symbol");
        }
        boolean one_number = true;
        int tmp_lf = lf;
        if (s.charAt(lf) == '-') {
            ++tmp_lf;
        }
        if (tmp_lf > rg) {
            throw new ParsingException("Unary minus, but no number");
        }
        boolean found_dot = false;
        for (int i = tmp_lf; i <= rg; ++i) {
            if (s.charAt(i) == '.') {
                if (found_dot) {
                    throw new ParsingException("Too many dots");
                }
                found_dot = true;
            } else if (!(s.charAt(i) >= '0' && s.charAt(i) <= '9')) {
                one_number = false;
                break;
            }
        }
        if (one_number) {
            return Double.parseDouble(s.substring(lf, rg));
        }
        List<Double> values = new ArrayList<>();
        List<Character> operations = new ArrayList<>();
        int balance = 0;
        int previous_position_of_bracket = -1;
        int number_start = -1;
        boolean in_number = false;
        for (int i = lf; i <= rg; ++i) {
            if (s.charAt(i) == '(' || s.charAt(i) == ')') {
                if (s.charAt(i) == '(') {
                    if (balance == 0) {
                        previous_position_of_bracket = i;
                    }
                    ++balance;
                } else {
                    if (balance == 1) {
                        values.add(f(previous_position_of_bracket + 1, i - 1));
                    }
                    --balance;
                }
            } else if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
                if (!in_number) {
                    number_start = i;
                }
                in_number = true;
            } else {
                if (in_number) {
                    values.add(Double.parseDouble(s.substring(number_start, i - 1)));
                    in_number = false;
                }
                operations.add(s.charAt(i));
            }
        }
        double curr_value = values.get(0);
        double ans = 0;
        for (int i = 0; i < operations.size(); ++i) {
            if (operations.get(i) == '-' || operations.get(i) == '+') {
                ans += curr_value;
                if (operations.get(i) == '-') {
                    curr_value = -values.get(i + 1);
                } else {
                    curr_value = values.get(i + 1);
                }
            } else {
                if (operations.get(i) == '*') {
                    curr_value *= values.get(i + 1);
                } else {
                    curr_value /= values.get(i + 1);
                }
            }
        }
        return ans + curr_value;
    }
}
