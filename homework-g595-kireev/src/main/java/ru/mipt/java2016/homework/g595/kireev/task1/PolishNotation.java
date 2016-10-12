package ru.mipt.java2016.homework.g595.kireev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;

import static java.lang.Double.parseDouble;

/**
 * Created by Карим on 05.10.2016.
 */
public class PolishNotation {
    private boolean isOp (char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int priority (char op) {
        if (op == '+' || op == '-')
            return 1;
        else if (op == '*' || op == '/')
                return 2;
        else
            return -1;
    }
    private boolean isdigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isalnum(char c) {
        return isdigit(c) || c == '.';
    }

    private boolean minusHandler(char prevChar, char c, char nextChar) throws ParsingException {
        if ((prevChar == '(' || (isOp(prevChar) && prevChar != '-')) && c == '-')
        {
            if (nextChar == '(')
                return false;
            else if (isdigit(nextChar))
                return true;
            else
                throw new ParsingException("Bad operators");
        }
        else
            return false;
    }
    private void operatorHandler(char prevChar, char c, char nextChar) throws ParsingException {
        if (c == '-')
        {
            if (nextChar == '-') // I don't know
                throw new ParsingException("Bad operators");
        }
        else if (!((prevChar == ')' || isdigit(prevChar)) && (nextChar == '(' || isdigit(nextChar) || nextChar == '-')))
            throw new ParsingException("Bad operators");
    }
    void processOp (ArrayDeque<Double> st, char op) throws ParsingException {
        double r = st.getLast();  st.removeLast();
        if (st.isEmpty())
        {
            if (op == '-')
                st.addLast(-1 * r);
            else
                throw new ParsingException("Bad unary operator");
        }
        else {
            double l = st.getLast();
            st.removeLast();
            switch (op) {
                case '+':
                    st.addLast(l + r);
                    break;
                case '-':
                    st.addLast(l - r);
                    break;
                case '*':
                    st.addLast(l * r);
                    break;
                case '/':
                    st.addLast(l / r);
                    break;
                case '%':
                    st.addLast(l % r);
                    break;
            }
        }
    }

    double calc (String expression) throws ParsingException {
        String s = "(" + expression + ")";
        s = s.replaceAll("\\s", "");
        int bracketSummary = 0;
        boolean empty = true;
        ArrayDeque<Double> st = new ArrayDeque<Double>();
        ArrayDeque<Character> op = new ArrayDeque<Character>();
        for (int i = 0; i< s.length(); ++i)
        {
            if (s.charAt(i) == '(') {
                    ++bracketSummary;
                op.addLast('(');
            }
            else if (s.charAt(i) == ')') {
                --bracketSummary;
                if (bracketSummary < 0)
                    throw new ParsingException("Too many close brackets");
                while (op.getLast() != '(') {
                    processOp(st, op.getLast());
                    op.removeLast();
                }

                op.removeLast();
            }
            else if (minusHandler(s.charAt(i - 1), s.charAt(i), s.charAt(i + 1)) || isdigit(s.charAt(i)))
            {
                int points = 0;
                double coef = 1;
                if (s.charAt(i) == '-')
                {
                    coef = -1;
                    ++i;
                }
                StringBuilder operand = new StringBuilder();

                while (i < s.length() && isalnum (s.charAt(i)))
                {
                    if (s.charAt(i) == '.')
                        ++points;
                    operand.append(s.charAt(i++));
                }
                if (points > 1)
                    throw new ParsingException("Too many close points");
                --i;
                if (isdigit (operand.charAt(0))) {
                    st.addLast(coef * parseDouble(operand.toString()));
                    empty = false;
                }
                else
                    throw new ParsingException("Begin from points");
            }
            else if (isOp (s.charAt(i))) {
                operatorHandler(s.charAt(i - 1), s.charAt(i), s.charAt(i + 1));
                char curOp = s.charAt(i);
                while (!op.isEmpty() && priority(op.getLast()) >= priority(s.charAt(i))) {
                    processOp(st, op.getLast());
                    op.removeLast();
                }
                op.addLast (curOp);
            }
            else
                throw new ParsingException("error symbols");
        }
        if (bracketSummary != 0)
            throw new ParsingException ("Too many open brackets");
        if (empty)
            throw new ParsingException("Empty expression");
        while (!op.isEmpty()) {
            processOp(st, op.getLast());
            op.removeLast();
        }
        return st.getLast();
    }


}
