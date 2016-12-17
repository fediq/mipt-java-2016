package ru.mipt.java2016.homework.g594.stepanov.task4;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CalculatorImplementation {
    public double calculate(String username, String expression) { // no unary minuses
        InnerCalculator solver = new InnerCalculator();
        return solver.calculate(username, expression);
    }

    private class InnerCalculator {
        private String str; // TODO threads
        private String username;

        private double calculate(String username, String expression) {
            String start = expression.replaceAll("\\s", "");
            this.username = username;
            StringBuilder result = new StringBuilder();
            StringBuilder current = new StringBuilder();
            for (int i = 0; i <= start.length(); ++i) {
                if (i == start.length() || isOperation(start.charAt(i))) {
                    String word = current.toString();
                    current = new StringBuilder();
                    Double val = new Double(0); // TODO
                    try {
                        val = billingDao.getVariable(username, word);
                    } catch (Exception e) {
                        result.append(word);
                        if (i != start.length()) {
                            result.append(start.charAt(i));
                        }
                        continue;
                    }
                    result.append(val.toString());
                    if (i != start.length()) {
                        result.append(start.charAt(i));
                    }
                } else {
                    current.append(start.charAt(i));
                }
            }
            str = result.toString();
            System.out.println(str);
            return solve(0, str.length());
        }

        private double solve(int left, int right) {
            boolean onlyNumber = true;
            for (int i = left; i < right; ++i) {
                char curr = str.charAt(i);
                if (Character.isDigit(curr) || curr == '.' || curr == '-') {
                    continue;
                } else {
                    onlyNumber = false;
                }
            }
            if (onlyNumber) {
                return Double.parseDouble(str.substring(left, right));
            }
            int balance = 0;
            // split into summands firstly
            for (int i = left; i < right; ++i) {
                if (str.charAt(i) == '(') {
                    ++balance;
                    continue;
                }
                if (str.charAt(i) == ')') {
                    --balance;
                    continue;
                }
                if (balance == 0 && str.charAt(i) == '+') {
                    return solve(left, i) + solve(i + 1, right);
                }
                if (balance == 0 && str.charAt(i) == '-') {
                    return solve(left, i) - solve(i + 1, right);
                }
            }

            // split into multiples
            balance = 0;
            for (int i = left; i < right; ++i) {
                if (str.charAt(i) == '(') {
                    ++balance;
                    continue;
                }
                if (str.charAt(i) == ')') {
                    --balance;
                    continue;
                }
                if (balance == 0 && str.charAt(i) == '*') {
                    return solve(left, i) * solve(i + 1, right);
                }
                if (balance == 0 && str.charAt(i) == '/') {
                    return solve(left, i) / solve(i + 1, right);
                }
            }

            // functions time
            String currentStr = str.substring(left, right);
            int bracketPos = currentStr.indexOf('(');
            String functionName = currentStr.substring(0, bracketPos);
            List<Double> arguments = new ArrayList<>();
            int previousPos = left + functionName.length() + 1;
            balance = 0;
            for (int i = previousPos; i < right; ++i) {
                if (str.charAt(i) == '(') {
                    ++balance;
                    continue;
                }
                if (str.charAt(i) == ')') {
                    --balance;
                    continue;
                }
                if (balance == 0 && str.charAt(i) == ',') {
                    arguments.add(solve(previousPos, i));
                    previousPos = i + 1;
                }
            }
            arguments.add(solve(previousPos, right - 1));
            if (functionName.equals("cos")) {
                return Math.cos(arguments.get(0));
            }
            if (functionName.equals("sin")) {
                return Math.sin(arguments.get(0));
            }
            if (functionName.equals("tg")) {
                return Math.tan(arguments.get(0));
            }
            if (functionName.equals("sqrt")) {
                return Math.sqrt(arguments.get(0));
            }
            if (functionName.equals("pow")) {
                return Math.pow(arguments.get(0), arguments.get(1));
            }
            if (functionName.equals("abs")) {
                return Math.abs(arguments.get(0));
            }
            if (functionName.equals("sign")) {
                return Math.signum(arguments.get(0));
            }
            if (functionName.equals("log")) {
                return Math.log(arguments.get(1)) / Math.log(arguments.get(0));
            }
            if (functionName.equals("log2")) {
                return Math.log(arguments.get(0)) / Math.log(2.0);
            }
            if (functionName.equals("rnd")) {
                return Math.random();
            }
            if (functionName.equals("max")) {
                return Math.max(arguments.get(0), arguments.get(1));
            }
            if (functionName.equals("min")) {
                return Math.min(arguments.get(0), arguments.get(1));
            }
            String functionBody = billingDao.getFunction(username, functionName);
            StringBuilder result = new StringBuilder();
            StringBuilder current = new StringBuilder();
            for (int i = 0; i <= functionBody.length(); ++i) {
                System.out.println(i);
                if (i == functionBody.length() || isOperation(functionBody.charAt(i))) {
                    String word = current.toString();
                    current = new StringBuilder();
                    Double val = new Double(0);
                    System.out.println(word);
                    if (word.length() == 1 && Character.isAlphabetic(word.charAt(0))) {
                        val = arguments.get(word.charAt(0) - 'a');
                    } else {
                        result.append(word);
                        if (i != functionBody.length()) {
                            result.append(functionBody.charAt(i));
                        }
                        continue;
                    }
                    result.append(val.toString());
                    if (i != functionBody.length()) {
                        result.append(functionBody.charAt(i));
                    }
                } else {
                    current.append(functionBody.charAt(i));
                }
            }
            return calculate(username, result.toString());
        }
    }

    private String str; // TODO threads
    private String username;
    @Autowired
    private BillingDao billingDao;

    private boolean isOperation(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == ')' || c == '(' || c == ',';
    }
}
