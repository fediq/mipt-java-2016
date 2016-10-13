package ru.mipt.java2016.homework.g595.kryloff.task1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 *
 *
 * @author Gregory Kryloff
 * @since 09.10.2016
 */
public class JMyCalculator implements Calculator {

    private static final HashSet<Character> ACCEPTABLE_SYMBOLS
            = new HashSet<>(Arrays.asList(
                '(', ')', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', '+', '-', '*', '/'));
    private static final HashSet<Character> SPACE_SYMBOLS
            = new HashSet<>(Arrays.asList('\n', '\f', ' ', '\t', '\r'));
    private static final HashSet NUMBER_SYMBOLS
            = new HashSet<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.'));
    private static final HashSet OPERATORS_AND_BRACES
            = new HashSet(Arrays.asList('+', '-', '*', '/', '(', ')'));
    private static final HashSet OPERATORS
            = new HashSet(Arrays.asList('+', '-', '*', '/'));

    JMyCalculator() {

    }

    //delete all space symbols from string
    private String deleteSpaces(String expression) {
        String temp = "";
        for (char currentChar : expression.toCharArray()) {
            if (!SPACE_SYMBOLS.contains(currentChar)) {
                temp += currentChar;
            }
        }
        return temp;

    }

    //check the expression for brace balance and unvalid symbols
    private void checkExpressionSymbolsAndBalance(String expression) throws ParsingException {
        int balance = 0;
        for (char currentChar : expression.toCharArray()) {
            if (!ACCEPTABLE_SYMBOLS.contains(currentChar)) {
                throw new ParsingException("Invalid expression");
            }
            if (currentChar == '(') {
                balance++;
            }
            if (currentChar == ')') {
                balance--;
            }
            if (balance < 0) {
                throw new ParsingException("Bad balance");
            }
        }
        if (balance != 0) {
            throw new ParsingException("Bad balance");
        }
    }

    private Vector<String> parseExpression(String expression) { //Get vector of lexems
        String curLexem = "";
        Vector<String> lexems = new Vector<String>();
        for (char currentChar : expression.toCharArray()) {
            if (curLexem == "") {
                curLexem += currentChar;
                continue;
            }
            if (NUMBER_SYMBOLS.contains(curLexem.charAt(curLexem.length() - 1))
                    && NUMBER_SYMBOLS.contains(currentChar)) {
                curLexem += currentChar;
            } else {
                lexems.add(curLexem);
                curLexem = "" + currentChar;
            }

        }
        if (!curLexem.equals("")) {
            lexems.add(curLexem);
        }
        return lexems;

    }

    private boolean checkNumberValidity(String number) throws ParsingException {
        if (number == null) {
            throw new ParsingException("Empty number");
        }
        if (number.indexOf('.') != number.lastIndexOf('.')) {
            throw new ParsingException("Bad number with two points");
        }
        for (char currentChar : number.toCharArray()) {
            if (!NUMBER_SYMBOLS.contains(currentChar)) {
                throw new ParsingException("Bad number with incorrect symbol");
            }
        }
        if (number.charAt(0) == '.') {
            throw new ParsingException("Bad number without whole part");
        }
        return true;
    }

    private boolean isOperator(String lexem) {
        return OPERATORS_AND_BRACES.contains(lexem.charAt(0));
    }

    private boolean isNumber(String lexem) {
        return NUMBER_SYMBOLS.contains(lexem.charAt(0));
    }

    private boolean isBadCombination(String operator1, String operator2) {
        switch (operator1 + operator2) {
            case "++":
            case "--":
            case "**":
            case "*/":
            case "/*":
            case "//":
            case "-*":
            case "+*":
            case "-/":
            case "+/":
                return true;
            default:
                break;
        }
        return false;
    }

    //some checking for validity
    private void checkParsedExpression(Vector<String> parsedExpression) throws ParsingException {
        if (parsedExpression == null) {
            throw new ParsingException("Null vector");
        }
        for (int i = 0; i < parsedExpression.size() - 1; ++i) {
            if (OPERATORS.contains(parsedExpression.get(i).charAt(0))
                    && OPERATORS.contains(parsedExpression.get(i + 1).charAt(0))) {
                if (isBadCombination(parsedExpression.get(i), parsedExpression.get(i + 1))) {
                    throw new ParsingException("Bad combination of two operators together");
                }
            }
            if (parsedExpression.get(i).charAt(0) == '(' && parsedExpression.get(i + 1).charAt(0) == ')') {
                throw new ParsingException("Empty braces");
            }
        }
        for (String lexem : parsedExpression) {
            if (isNumber(lexem)) {
                try {
                    checkNumberValidity(lexem);
                } catch (ParsingException e) {
                    throw new ParsingException("Invalid number", e.getCause());
                }
            }
        }
    }

    //convert to reverse polish notation
    private Vector<String> infixToPostfix(Vector<String> infixExpression) throws ParsingException {
        Vector<String> result = new Vector<>();
        Stack<String> operators = new Stack<>();
        boolean isNextUnary = true;
        for (String lexem : infixExpression) {
            if (isNumber(lexem)) {
                result.add(lexem);
                isNextUnary = false;
            } else if (isOperator(lexem)) {
                switch (lexem) {
                    case "(": 
                        operators.add(lexem);
                        isNextUnary = true;
                        break;
                    
                    case "-":
                        if (!isNextUnary) {
                            while (!operators.empty() && operators.peek().charAt(0) != '(') {
                                result.add(operators.pop());
                            }
                            operators.add(lexem);
                        } else {
                            operators.add("?"); // "?" means unary minus
                        }
                        isNextUnary = true;
                        break;
                    case "+": 
                        if (!isNextUnary) {
                            while (!operators.empty() && operators.peek().charAt(0) != '(') {
                                result.add(operators.pop());
                            }
                            operators.add(lexem);
                        } else {
                            operators.add("!"); // "!' means unary plus
                        }
                        isNextUnary = true;
                        break;
                    
                    case "*": 
                        if (isNextUnary) {
                            throw new ParsingException("Unary dividion / multiply");
                        }
                        while (!operators.empty() 
                                && (operators.peek().charAt(0) == '*' || operators.peek().charAt(0) == '/')) {
                            result.add(operators.pop());
                        }
                        operators.add(lexem);
                        isNextUnary = true;
                        break;
                    
                    case "/": 
                        if (isNextUnary) {
                            throw new ParsingException("Unary dividion / multiply");
                        }
                        while (!operators.empty() 
                                && (operators.peek().charAt(0) == '*' || operators.peek().charAt(0) == '/')) {
                            result.add(operators.pop());
                        }
                        operators.add(lexem);
                        isNextUnary = true;
                        break;
                    
                    case ")": 
                        while (!operators.empty() && operators.peek().charAt(0) != '(') {
                            result.add(operators.pop());
                        }
                        operators.pop();
                        isNextUnary = false;
                        break;
                    default:
                        break;
                }
            }
        }
        while (!operators.empty()) {
            result.add(operators.pop());
        }
        return result;
    }

    //calculate in polish notation 
    private double calculatePostfix(Vector<String> postfixExpression) throws ParsingException {
        System.out.println(postfixExpression);
        Stack<Double> operands = new Stack<>();
        for (String lexem : postfixExpression) {
            if (isNumber(lexem)) {
                operands.add(Double.valueOf(lexem));
            } else {
                switch (lexem) {
                    case "!":
                        break;
                    case "?": 
                        operands.add(operands.pop() * (-1));
                        break;
                    
                    case "+": 
                        operands.add(operands.pop() + operands.pop());
                        break;
                    
                    case "-": 
                        operands.add(-(operands.pop() - operands.pop()));
                        break;
                    
                    case "*": 
                        operands.add(operands.pop() * operands.pop());
                        break;
                    
                    case "/": 
                        operands.add(1 / (operands.pop() / operands.pop()));
                        break;
                    default:
                        break;
                }
            }
        }
        if (operands.empty()) {
            throw new ParsingException("Invalid expression");
        } else { 
            return operands.pop();
        }
        

    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        try {
            expression = deleteSpaces(expression);
            checkExpressionSymbolsAndBalance(expression);
            Vector<String> parsedExpression = parseExpression(expression);
            checkParsedExpression(parsedExpression);
            Vector<String> postfixExpression = infixToPostfix(parsedExpression);
            return calculatePostfix(postfixExpression);
        } catch (ParsingException e) {
            throw new ParsingException("Invalid expression", e.getCause());
        }
    }
}
