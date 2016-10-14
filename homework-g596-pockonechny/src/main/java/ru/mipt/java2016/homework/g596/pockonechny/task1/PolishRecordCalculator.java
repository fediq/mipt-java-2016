package ru.mipt.java2016.homework.g596.pockonechny.task1;

/**
 * Created by celidos on 13.10.16.
 * Reverse polish notation calculator
 */

import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class PolishRecordCalculator implements Calculator
{
    private static final char RPN_CONTAIN_NUMBER = 1;

    private class MyPair
    {
        public double value;
        public int newPos;
        MyPair(double _value, int _newPos) { value = _value; newPos = _newPos; }
    }

    private Stack <RpnElement> RPN_stack;

    PolishRecordCalculator() {
        RPN_stack = new Stack();
    }

    private boolean is_digit_separator(char t) { return t == '.' || t == ','; }

    private boolean is_space_symbol(char t) { return t == ' ' || t == '\t' || t == '\n'; }

    private boolean is_operation(char t) {
        return (t == '^' || t == '+' || t == '-' || t == '*' || t == '/');
    }

    private boolean is_right_op(char op) {
        return (op == '^' || op == '_');
    }

    private int     get_priority(char op) {
        if (op == '^' || op == '_')     return 0;
        if (op == '*' || op == '/')     return 1;
        if (op == '+' || op == '-')     return 2;
        if (op == '(' || op == ')')     return 3;
        return -1;
    }

    private boolean more(char op1, char op2) {
        return (get_priority(op1) < get_priority(op2));
    }

    private boolean more_or_eq(char op1, char op2) {
        return (get_priority(op1) <= get_priority(op2));
    }

    private boolean is_uno(char op) {
        return (op == '_');
    }

    private MyPair readDoubleFromPos(String s, int pos) throws ParsingException {
        boolean waspoint = false;
        double beforePoint = 0, afterPoint = 0, order = 10;
        for (int i = pos; i < s.length(); ++i)
        {
            if (is_digit_separator(s.charAt(i)) && waspoint)
                throw new ParsingException("Invalid number");
            else if (is_digit_separator(s.charAt(i)))
                waspoint = true;
            else if (Character.isDigit(s.charAt(i)))
            {
                double c = (double)(s.charAt(i) - '0');
                if (!waspoint)
                    beforePoint = beforePoint * 10 + c;
                else {
                    afterPoint += c / order;
                    order *= 10;
                }
            }
            else {
                return new MyPair(beforePoint + 0.0 + afterPoint, i);
            }
        }
        return new MyPair(beforePoint + afterPoint, s.length());
    }

    private boolean getRpn(String req) throws ParsingException {  // Получает обратную польскую запись

        Integer nextpos = -1;

        req = '(' + req + ')';

        Stack <Character> bufferStack = new Stack();         // Стек, в который складываются операции
        bufferStack.clear();
        int len = req.length();
        boolean canBeUno = true;                    // Флаг - может ли быть сл. операция унарной

        for (int i = 0; i < len; ++i)
        {
            char currChar = req.charAt(i);
            if (is_space_symbol(currChar))
                continue;
            else if (currChar == '(')
            {
                bufferStack.push('(');
                canBeUno = true;
            }
            else if (Character.isDigit(currChar))
            {
                MyPair newdata = readDoubleFromPos(req, i);
                RPN_stack.push(new RpnElement(RPN_CONTAIN_NUMBER, newdata.value));

                i = newdata.newPos - 1;
                canBeUno = false;
            }
            else if (is_operation(currChar))
            {
                char op = currChar;
                if (op == '-' && canBeUno)
                {
                    while (!bufferStack.empty()) {
                        if ( (!is_right_op(bufferStack.peek()) && more_or_eq(bufferStack.peek(), '_')) ||
                                (is_right_op(bufferStack.peek()) && more (bufferStack.peek(), '_'))) {
                            RPN_stack.push(new RpnElement(bufferStack.pop(), 0.0));
                        }
                        else break;
                    }
                    bufferStack.push('_');   // means uno minus
                    continue;
                }

                while (!bufferStack.empty()) {
                    if ( (!is_right_op(bufferStack.peek()) && more_or_eq(bufferStack.peek(), op)) ||
                            (is_right_op(bufferStack.peek()) && more (bufferStack.peek(), op))) {
                        RPN_stack.push(new RpnElement(bufferStack.pop(), 0.0));
                    }
                    else break;
                }
                bufferStack.push(op);
                canBeUno = true;
            }
            else if (currChar == ')')
            {
                if (bufferStack.empty())
                    throw new ParsingException("Brace balance failed");

                boolean okFlag = false;
                while (!bufferStack.empty()) {
                    if (bufferStack.peek() != '(') {
                        char op = bufferStack.pop();
                        RPN_stack.push(new RpnElement(op, 0.0));
                    }
                    else {
                        bufferStack.pop();
                        okFlag = true;
                        break;
                    }
                }
                if (!okFlag)
                    throw new ParsingException("Brace balance failed");
                canBeUno = false;
            }
            else
                throw new ParsingException("Unknown symbol");
        }

        if (!bufferStack.empty())
            throw new ParsingException("Brace balance failed / Invalid expression");

        Stack <RpnElement> outputResult = new Stack(); // reverting stack
        while (!RPN_stack.empty()) {
            outputResult.push(RPN_stack.pop());
        }
        RPN_stack = outputResult;
        return true;
    }

    private double rpn_count() throws ParsingException {
        Stack <Double> st = new Stack();
        while (!RPN_stack.empty())
        {
            RpnElement el = RPN_stack.peek();

            if (el.getOp() == RPN_CONTAIN_NUMBER)
                st.push(new Double(el.getValue()));
            else
            {
                char op = el.getOp();
                double o2 = 0.0, o1 = 0.0;  // operands
                if (!st.empty()) {
                    o2 = st.pop();
                }
                else {
                    throw new ParsingException("Invalid expression");
                }
                if (!is_uno(op))
                {
                    if (!st.empty()) {
                        o1 = st.pop();
                    }
                    else {
                        throw new ParsingException("Brace balance failed / Invalid expression");
                    }
                }

                if      (op == '+') st.push(o1 + o2);
                else if (op == '-') st.push(o1 - o2);
                else if (op == '*') st.push(o1 * o2);
                else if (op == '/') st.push(o1 / o2);
                else if (op == '^') {
                    if (o1 > 0.0)
                        st.push(Math.pow(o1, o2));
                    else {
                        throw new ParsingException("Incorrect operation : negative power");
                    }
                }
                else if (op == '_') st.push(-o2);
                else {
                    throw new ParsingException("Unknown symbols");
                }
            }
            RPN_stack.pop();
        }

        if (!RPN_stack.empty())
            throw new ParsingException("Brace balance failed / Invalid expression");

        if (st.empty())
            throw new ParsingException("Empty expresssion");

        return st.peek();
    }

    public double calculate(String expression) throws ParsingException {
        RPN_stack = new Stack();
        getRpn(expression);

        return rpn_count();
    }
}
