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
    private Stack <RpnElement> RPN_stack;

    private final char RPN_NUM = 1;

    private int nextpos = -1;

    PolishRecordCalculator() {
        RPN_stack = new Stack();
    }

    private double read_double_from_pos(String s, int pos) throws ParsingException {
        boolean waspoint = false;
        double r1 = 0, r2 = 0, d = 10;
        for (int i = pos; i < s.length(); ++i)
        {
            if ((s.charAt(i) == '.' || s.charAt(i) == ',') && waspoint)
                throw new ParsingException("Invalid number");
            else if (s.charAt(i) == '.' || s.charAt(i) == ',')
                waspoint = true;
            else if (Character.isDigit(s.charAt(i)))
            {
                double c = (double)(s.charAt(i) - '0');
                if (!waspoint)
                    r1 = r1 * 10 + c;
                else {
                    r2 += c / d;
                    d *= 10;
                }
            }
            else {
                nextpos = i;
                return r1 + r2;
            }
        }
        return Double.NaN;
    }

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

    private boolean getRpn(String req) throws ParsingException {  // Получает обратную польскую запись
        req = '(' + req + ')';

        Stack <Character> st = new Stack();             // Стек, в который складываются операции
        st.clear();
        int len = req.length();
        int i = 0;
        boolean canbeuno = true;          // Флаг - может ли быть сл. операция унарной

        for (i = 0; i < len; ++i)
        {
            char req_i = req.charAt(i);
            if (req_i == ' ' || req_i == '\t' || req_i == '\n')
                continue;
            else if (req_i == '(')
            {
                st.push('(');
                canbeuno = true;
            }
            else if (Character.isDigit(req_i))
            {
                Integer ni = 0;
                RPN_stack.push(new RpnElement(RPN_NUM, read_double_from_pos(req, i), false));

                i = nextpos - 1;
                canbeuno = false;
            }
            else if (is_operation(req_i))
            {
                char op = req_i;
                if (op == '-' && canbeuno)
                {
                    while (!st.empty()) {
                        if ( (!is_right_op(st.peek()) && more_or_eq(st.peek(), '_')) ||
                                (is_right_op(st.peek()) && more (st.peek(), '_')))
                        {
                            char opp = st.pop();
                            RPN_stack.push(new RpnElement(opp, 0.0, is_right_op(opp)));
                        }
                        else break;
                    }
                    st.push('_');   // means uno minus
                    continue;
                }

                while (!st.empty()) {
                    if ( (!is_right_op(st.peek()) && more_or_eq(st.peek(), op)) ||
                            (is_right_op(st.peek()) && more (st.peek(), op)))
                    {
                        char opp = st.pop();
                        RPN_stack.push(new RpnElement(opp, 0.0, is_right_op(opp)));
                    }
                    else break;
                }
                st.push(op);
                canbeuno = true;
            }
            else if (req_i == ')')
            {
                if (st.empty())
                    throw new ParsingException("Brace balance failed");

                boolean currentok = false;
                while (!st.empty()) {
                    if (st.peek() != '(') {
                        char op = st.pop();
                        RPN_stack.push(new RpnElement(op, 0.0, is_right_op(op)));
                    }
                    else {
                        st.pop();
                        currentok = true;
                        break;
                    }
                }
                if (!currentok)
                    throw new ParsingException("Brace balance failed");
                canbeuno = false;
            }
            else
                throw new ParsingException("Unknown symbol");
        }

        if (!st.empty())
            throw new ParsingException("Brace balance failed / Invalid expression");

        Stack <RpnElement> outres = new Stack();
        while (!RPN_stack.empty()) {
            outres.push(RPN_stack.pop());
        }
        RPN_stack = outres;

        return true;
    }

    private double rpn_count() throws ParsingException {
        Stack <Double> st = new Stack();
        Boolean ok = true;
        while (!RPN_stack.empty())
        {
            RpnElement el = RPN_stack.peek();

            if (el.op == RPN_NUM)
                st.push(new Double(el.x));
            else
            {
                char op = el.op;
                double o2 = 0.0, o1 = 0.0;
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

                if (op == '+') st.push(o1 + o2);
                else if (op == '-') st.push(o1 - o2);
                else if (op == '*') st.push(o1 * o2);
                else if (op == '/') {
                    st.push(o1 / o2);
                }
                else if (op == '^') {
                    if (o1 > 0.0)
                        st.push(Math.pow(o1, o2));
                    else {
                        throw new ParsingException("Incorrect operation : negative power");
                    }
                }
                else if (op == '_') {
                    st.push(-o2);
                }
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
        nextpos = -1;
        getRpn(expression);

        return rpn_count();
    }
}
