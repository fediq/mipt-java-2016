package ru.mipt.java2016.homework.g595.shakhray.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.Vector;

/**
 * Created by Vlad Shakhray on 12/10/2016.
 */
// The implementation follows Dijkstra's algorithm with two stacks
public class SwagCalculator implements Calculator {

    private Stack<Double> stack = new Stack<>();
    private Vector<Character> operations = new Vector<>();

    @Override
    public double calculate(String expression) throws ParsingException {

        // Testing for nullability
        if (expression == null) {
            throw new ParsingException("Expression cannot be null.");
        }

        // Removing all space symbols and indentations
        expression = removeSpaceSymbols(expression);

        // Checking for empty expression
        if (checkForEmptyExpression(expression)) {
            throw new ParsingException("Expression is either empty or consists of delimiters only.");
        }

        // Checking for invalid symbols
        if (!checkForInvalidSymbols(expression)) {
            throw new ParsingException("Expression contains invalid symbols.");
        }

        // Checking for the correctness of the bracket balance
        if (bracketBalance(expression) != 0) {
            throw new ParsingException("Incorrect bracket balance.");
        }

        // Checking for incorrect lexems, such as .45 or 1.4.5
        if (checkForIncorrectLexems(expression)) {
            throw new ParsingException("Expression contains incorrect lexem.");
        }

        transformToRPN(expression);
        return calculateRPN();
    }

    private int getOperationPriority(char op) {
        if (op == '+' || op == '-') {
            return 1;
        }
        if (op == '*' || op == '/') {
            return 2;
        }
        if (op == 'u') {
            return 3;
        }
        return -1;
    }

    private double calculateOperation(double l, double r, char op) throws ParsingException {
        switch (op) {
            case '+':
                return l + r;
            case '-':
                return l - r;
            case '*':
                return l * r;
            case '/':
                return l / r;
            default:
                throw new ParsingException("Invalid operation symbol.");
        }
    }

    private void processStackOperation() throws ParsingException {
        char op = operations.lastElement();
        double r = stack.lastElement();
        stack.pop();
        if (op == 'u') {
            stack.add(-r);
            return;
        }
        double l = stack.lastElement();
        stack.pop();
        stack.push(calculateOperation(l, r, op));
    }

    private int bracketBalance(String expression) {
        int count = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                count += 1;
            } else if (expression.charAt(i) == ')') {
                count -= 1;
            }
            if (count < 0) {
                return -1;
            }
        }
        return count;
    }

    private boolean checkForIncorrectLexems(String expression) {
        String currentLexem = "";
        boolean dotEncountered = false;
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (Character.isDigit(ch)) {
                currentLexem += Character.toString(ch);
            } else if (ch == '.') {
                currentLexem += Character.toString(ch);
                if (dotEncountered) {
                    return true;
                }
                dotEncountered = true;
            } else {
                if (currentLexem != "" && currentLexem.charAt(currentLexem.length() - 1) == '.') {
                    return true;
                }
                currentLexem = "";
                dotEncountered = false;
            }
        }
        return false;
    }

    private String removeSpaceSymbols(String expression) {
        expression = expression.replaceAll("\\s+", "");
        return expression;
    }

    private boolean checkForInvalidSymbols(String expression) {
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (isOperator(ch) || Character.isDigit(ch) || ch == '.' || isDelimiter(ch) || isBracket(ch)) {
                continue;
            }
            return false;
        }
        return true;
    }

    private boolean isBracket(char ch) {
        return ch == '(' || ch == ')';
    }

    private boolean checkForEmptyExpression(String expression) {
        return expression.length() == 0 || expression == "()";
    }

    private boolean isDelimiter(char ch) {
        return ch == ' ';
    }

    private boolean isOperator(char ch) {
        return ch == '*' || ch == '/' || ch == '+' || ch == '-';
    }

    private void transformToRPN(String expression) throws ParsingException {
        boolean mayBeUnary = true;
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            // Should not be encountered! Just in case
            if (isDelimiter(ch)) {
                continue;
            }

            // We always push the opening bracket to the stack
            if (ch == '(') {
                operations.add('(');
                mayBeUnary = true;
                continue;
            }

            // Process operations while opening bracket is not found
            if (ch == ')') {
                while (operations.lastElement() != '(') {
                    processStackOperation();
                    operations.removeElementAt(operations.size() - 1);
                }
                operations.removeElementAt(operations.size() - 1);
                mayBeUnary = false;
                continue;
            }

            // Add operator
            if (isOperator(ch)) {
                if (mayBeUnary) {
                    if (ch == '+' || ch == '*' || ch == '/') {
                        throw new ParsingException("Invalid unary operator.");
                    }
                    ch = 'u';
                }
                while (!operations.isEmpty() && (ch != 'u' && getOperationPriority(operations.lastElement())
                        >= getOperationPriority(ch)
                        || ch == 'u' && getOperationPriority(operations.lastElement())
                        > getOperationPriority(ch))) {
                    processStackOperation();
                    operations.removeElementAt(operations.size() - 1);
                }
                operations.add(ch);
                mayBeUnary = true;
                continue;
            }

            // The following symbols must make up a lexem
            String lexem = "";
            while (i < expression.length() && (Character.isDigit(expression.charAt(i))
                    || expression.charAt(i) == '.')) {
                lexem += Character.toString(expression.charAt(i));
                i += 1;
            }
            stack.add(Double.parseDouble(lexem));
            mayBeUnary = false;
            i -= 1;
        }
    }

    private double calculateRPN() throws ParsingException {
        while (!operations.isEmpty()) {
            processStackOperation();
            operations.removeElementAt(operations.size() - 1);
        }
        return stack.lastElement();
    }
}
