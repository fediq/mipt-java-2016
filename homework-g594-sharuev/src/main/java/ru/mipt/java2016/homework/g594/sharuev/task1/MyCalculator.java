package ru.mipt.java2016.homework.g594.sharuev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class MyCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        } else if (expression.equals("")) {
            throw new ParsingException("Empty string");
        }
        StringBuilder ss = new StringBuilder();
        ToReversePolish(new StringReader(expression), ss);
        if (ss.toString().equals("")) {
            throw new ParsingException("String with only whitespaces");
        }

        return CalculateReversePolish(new StringReader(ss.toString()));
    }

    private Associativity associativity(char c) throws ParsingException {
        switch (c) {
            case '+':
            case '*':
            case '/':
            case '-':
                return Associativity.left;
            case '^':
                return Associativity.right;
            default:
                assert (false);
        }
        throw new ParsingException("Wrong associativity");
    }

    private int priority(char c) throws ParsingException {
        switch (c) {
            case '(':
            case ')':
                return 0;
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                assert (false);
        }
        throw new ParsingException("Wrong associativity");
    }

    private boolean isOperator(char c) {
        switch (c) {
            case '+':
            case '-':
            case '*':
            case '/':
            case '^':
                return true;
            default:
                return false;
        }
    }

    private int readNumber(StringReader is, StringBuilder os, char c) throws IOException {
        int readed;
        do {
            os.append(c);
            readed = is.read();
            if (readed == -1) break;
            else c = (char) readed;
        } while (Character.isDigit(c) || c == '.');
        if (readed != -1) readed = c;
        return readed;
    }

    private boolean ToReversePolish(StringReader is, StringBuilder os) throws ParsingException {
        Stack<Character> oper = new Stack<Character>();
        char c;
        int unary_flag = 1; //If 1, then - or + must be unary. If 0, then binary.
        int readed = -1; // Value that came in. If we want value to be passed to the next iteration, we must store it here.
        boolean lastWasUnary = false;
        try {
            while (true) {
                if (readed == -1) {
                    readed = is.read();
                    if (readed == -1)
                        break;
                }
                c = (char) readed;
                readed = -1;

                // If read digit, read all the number (including decimal delimiter).
                // Last read character is not a digit, so process it as a character.
                if (Character.isDigit(c)) {
                    readed = readNumber(is, os, c);
                    os.append(' ');
                } else if ((c == '-' || c == '+') && unary_flag == 1) {
                    // Unary + or - in the beginning, after ( or after operator.
                    lastWasUnary = true;
                    os.append("0 ");
                    readed = c;
                } else if (c == '(') {
                    oper.push('(');
                    unary_flag = 2; // Will be 1 at the beginning of the next iteration.
                } else if (c == ')') {
                    if (oper.empty()) throw new ParsingException("Closing bracket without opening one");
                    while (oper.peek() != '(') {
                        os.append(oper.pop());
                        if (oper.empty()) throw new ParsingException("Closing bracket without opening one");
                    }
                    oper.pop(); // Remove ( from stack.
                } else if (isOperator(c)) {
                    if (associativity(c) == Associativity.left) {
                        if (unary_flag == 1) throw new ParsingException(String.format("Missing operand for %c", c));
                        if (lastWasUnary == false)
                            while (!oper.empty() && oper.peek() != '(' && priority(c) <= priority(oper.peek())) {
                                os.append(oper.pop());
                            }
                        else lastWasUnary = false;
                        oper.push(c);
                    } else if (associativity(c) == Associativity.right) {
                        if (unary_flag == 1) throw new ParsingException(String.format("Missing operand for %c", c));
                        if (lastWasUnary == false)
                            while (!oper.empty() && oper.peek() != '(' && priority(c) < priority(oper.peek())) {
                                os.append(oper.pop());
                            }
                        else lastWasUnary = false;
                        oper.push(c);
                    }
                    unary_flag = 2;
                } else if (Character.isWhitespace(c)) {
                    continue;
                } else throw new ParsingException(String.format("Unknown character %c", c));

                if (unary_flag > 0)
                    --unary_flag;
            }
            while (!oper.empty()) {
                if (oper.peek() == '(') throw new ParsingException("No closing bracket");
                os.append(oper.pop());
            }
        } catch (IOException e) {
            throw new ParsingException(String.format("Some weird IO error: %s", e.getMessage()));
        }
        return true;
    }

    private double CalculateReversePolish(StringReader is) throws ParsingException {
        StringBuilder sb = new StringBuilder();
        Stack<Double> st = new Stack<Double>();
        char c;
        int readed = -1;
        double a;
        try {
            while (true) {
                readed = is.read();
                if (readed == -1)
                    break;
                c = (char) readed;

                if (Character.isDigit(c)) {
                    do {
                        sb.append(c);
                        c = (char) is.read();
                    } while (Character.isDigit(c) || c == '.');
                    // Catching double decimal delimiters, for example.
                    try {
                        st.push(Double.parseDouble(sb.toString()));
                    } catch (NumberFormatException nfe) {
                        throw new ParsingException(String.format("Wrong decimal literal: %s", sb.toString()));
                    }
                    sb.setLength(0);
                } else if (c == '+') {
                    double a1 = st.pop();
                    double a2 = st.pop();
                    st.push(a1 + a2);
                } else if (c == '-') {
                    double a2 = st.pop();
                    double a1 = st.pop();
                    // -Infinity fix.
                    if (a1 == 0.0 && a2 == 0.0)
                        st.push(-0.0d);
                    else st.push(a1 - a2);
                } else if (c == '*') {
                    double a1 = st.pop();
                    double a2 = st.pop();
                    st.push(a1 * a2);
                } else if (c == '/') {
                    double a2 = st.pop();
                    double a1 = st.pop();
                    st.push(a1 / a2);
                } else if (c == '^') {
                    double a2 = st.pop();
                    double a1 = st.pop();
                    double res = Math.pow(a1, a2);
                    st.push(res);
                }
            }
        } catch (
                IOException e)

        {
            throw new ParsingException(String.format("Some weird IO error: %s", e.getMessage()));
        }
        return st.pop();
    }

    private enum Associativity {
        left, right
    }


}
