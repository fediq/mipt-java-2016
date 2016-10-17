package ru.mipt.java2016.homework.g596.pockonechny.task1;

/**
 * Created by celidos on 13.10.16.
 * Reverse polish notation calculator
 */

import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class PolishRecordCalculator implements Calculator {

    private static final char RPN_CONTAIN_NUMBER = 1;

    private class MyPair {
        private double value;
        private int newPos;

        MyPair(double newValue, int setPos) {
            value = newValue;
            newPos = setPos;
        }

        double getValue() {
            return value;
        }

        double getNewPos() {
            return newPos;
        }
    }

    private class RpnElement {
        private char operation;               // Определяет тип содержимого: число или операция
        private double value;                 // Если число, то чему оно равно

        public char getOp()      {
            return operation;
        }

        public double getValue() {
            return value;
        }

        RpnElement(char newOperation, double newValue) {
            operation = newOperation;
            value = newValue;
        }
    }

    private Stack<RpnElement> rpnStack;

    PolishRecordCalculator() {
        rpnStack = new Stack();
    }

    private boolean isDigitSeparator(char t) {
        return t == '.' || t == ',';
    }

    private boolean isSpaceSymbol(char t) {
        return t == ' ' || t == '\t' || t == '\n';
    }

    private boolean isOperation(char t) {
        return (t == '^' || t == '+' || t == '-' || t == '*' || t == '/');
    }

    private boolean isRightOp(char op) {
        return (op == '^' || op == '_');
    }

    private int getPriority(char op) {
        if (op == '^' || op == '_') {
            return 0;
        } else if (op == '*' || op == '/') {
            return 1;
        } else if (op == '+' || op == '-') {
            return 2;
        } else if (op == '(' || op == ')') {
            return 3;
        }
        return -1;
    }

    private boolean more(char op1, char op2) {
        return (getPriority(op1) < getPriority(op2));
    }

    private boolean moreOrEq(char op1, char op2) {
        return (getPriority(op1) <= getPriority(op2));
    }

    private boolean isUno(char op) {
        return (op == '_');
    }

    private MyPair readDoubleFromPos(String s, int pos) throws ParsingException {
        boolean waspoint = false;
        double beforePoint = 0;
        double afterPoint = 0;
        double order = 10;
        for (int i = pos; i < s.length(); ++i) {
            if (isDigitSeparator(s.charAt(i)) && waspoint) {
                throw new ParsingException("Invalid number");
            } else if (isDigitSeparator(s.charAt(i))) {
                waspoint = true;
            } else if (Character.isDigit(s.charAt(i))) {
                double c = (double) (s.charAt(i) - '0');
                if (!waspoint) {
                    beforePoint = beforePoint * 10 + c;
                } else {
                    afterPoint += c / order;
                    order *= 10;
                }
            } else {
                return new MyPair(beforePoint + 0.0 + afterPoint, i);
            }
        }
        return new MyPair(beforePoint + afterPoint, s.length());
    }

    private boolean getRpn(String req) throws ParsingException {  // Получает обратную польскую запись

        req = '(' + req + ')';

        Stack<Character> bufferStack = new Stack(); // временный стек, в который складываются операции
                                                    // потому что потом его надо будет развернуть
        int len = req.length();
        boolean canBeUno = true; // Флаг - может ли быть следующая операция унарной

        for (int i = 0; i < len; ++i) {
            char currChar = req.charAt(i);
            if (isSpaceSymbol(currChar)) {
                continue;
            } else if (currChar == '(') {
                bufferStack.push('(');
                canBeUno = true;
            } else if (Character.isDigit(currChar)) {
                MyPair newdata = readDoubleFromPos(req, i);
                rpnStack.push(new RpnElement(RPN_CONTAIN_NUMBER, newdata.value));
                i = newdata.newPos - 1;
                canBeUno = false;
            } else if (isOperation(currChar)) {
                char op = currChar;
                if (op == '-' && canBeUno) {
                    while (!bufferStack.empty()) {
                        if ((!isRightOp(bufferStack.peek()) && moreOrEq(bufferStack.peek(), '_')) ||
                                (isRightOp(bufferStack.peek()) && more(bufferStack.peek(), '_'))) {
                            rpnStack.push(new RpnElement(bufferStack.pop(), 0.0));
                        } else {
                            break;
                        }
                    }
                    bufferStack.push('_');   // means uno minus
                    continue;
                }

                while (!bufferStack.empty()) {
                    if ((!isRightOp(bufferStack.peek()) && moreOrEq(bufferStack.peek(), op)) ||
                            (isRightOp(bufferStack.peek()) && more(bufferStack.peek(), op))) {
                        rpnStack.push(new RpnElement(bufferStack.pop(), 0.0));
                    } else {
                        break;
                    }
                }
                bufferStack.push(op);
                canBeUno = true;
            } else if (currChar == ')') {
                if (bufferStack.empty()) {
                    throw new ParsingException("Brace balance failed");
                }
                boolean okFlag = false;
                while (!bufferStack.empty()) {
                    if (bufferStack.peek() != '(') {
                        char op = bufferStack.pop();
                        rpnStack.push(new RpnElement(op, 0.0));
                    } else {
                        bufferStack.pop();
                        okFlag = true;
                        break;
                    }
                }
                if (!okFlag) {
                    throw new ParsingException("Brace balance failed");
                }
                canBeUno = false;
            } else {
                throw new ParsingException("Unknown symbol");
            }
        }

        if (!bufferStack.empty()) {
            throw new ParsingException("Brace balance failed / Invalid expression");
        }

        Stack<RpnElement> outputResult = new Stack(); // reverting stack
        while (!rpnStack.empty()) {
            outputResult.push(rpnStack.pop());
        }
        rpnStack = outputResult;
        return true;
    }

    private double rpnCount() throws ParsingException {
        Stack<Double> st = new Stack();
        while (!rpnStack.empty()) {
            RpnElement el = rpnStack.peek();

            if (el.getOp() == RPN_CONTAIN_NUMBER) {
                st.push(el.getValue());
            } else {
                char op = el.getOp();
                double o2 = 0.0;
                double o1 = 0.0;  // operands
                if (!st.empty()) {
                    o2 = st.pop();
                } else {
                    throw new ParsingException("Invalid expression");
                }
                if (!isUno(op)) {
                    if (!st.empty()) {
                        o1 = st.pop();
                    } else {
                        throw new ParsingException("Brace balance failed / Invalid expression");
                    }
                }

                if (op == '+') {
                    st.push(o1 + o2);
                } else if (op == '-') {
                    st.push(o1 - o2);
                } else if (op == '*') {
                    st.push(o1 * o2);
                } else if (op == '/') {
                    st.push(o1 / o2);
                } else if (op == '^') {
                    if (o1 > 0.0) {
                        st.push(Math.pow(o1, o2));
                    } else {
                        throw new ParsingException("Incorrect operation : negative power");
                    }
                } else if (op == '_') {
                    st.push(-o2);
                } else {
                    throw new ParsingException("Unknown symbols");
                }
            }
            rpnStack.pop();
        }

        if (!rpnStack.empty()) {
            throw new ParsingException("Brace balance failed / Invalid expression");
        }

        if (st.empty()) {
            throw new ParsingException("Empty expresssion");
        }

        return st.peek();
    }

    public double calculate(String expression) throws ParsingException {
        rpnStack = new Stack();
        getRpn(expression);

        return rpnCount();
    }
}
