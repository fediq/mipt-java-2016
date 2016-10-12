package ru.mipt.java2016.homework.g595.kireev.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

import static java.lang.Character.isDigit;
import static java.lang.Double.parseDouble;

/**
 * Created by Карим on 05.10.2016.
 */
public class PolishNotation {
    private boolean isOperation(char c) {
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

    private boolean isAllNum(char c) {
        return isDigit(c) || c == '.';
    }

    private boolean minusHandler(char prevChar, char c, char nextChar) throws ParsingException {
        if (c == '-' &&
                (prevChar == '(' || (isOperation(prevChar) && prevChar != '-')) )
        {
            if (nextChar == '(')
                return false;
            else if (isDigit(nextChar))
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
        else if (!((prevChar == ')' || isDigit(prevChar)) &&
                        (nextChar == '(' || isDigit(nextChar) || nextChar == '-')))
            throw new ParsingException("Bad operators");
    }
    void operationProcess (Stack<Double> st, char operation) throws ParsingException {
        double r = st.pop();
        if (st.isEmpty())
        {
            if (operation == '-')
                st.push(-1 * r);
            else
                throw new ParsingException("Bad unary operator");
        }
        else {
            double l = st.pop();
            switch (operation) {
                case '+':
                    st.push(l + r);
                    break;
                case '-':
                    st.push(l - r);
                    break;
                case '*':
                    st.push(l * r);
                    break;
                case '/':
                    st.push(l / r);
                    break;
                case '%':
                    st.push(l % r);
                    break;
            }
        }
    }

    double calc (String expression) throws ParsingException {
        String s = "(" + expression + ")";
        s = s.replaceAll("\\s", "");
        int bracketSummary = 0;
        boolean empty = true;
        Stack<Double> st = new Stack<Double>();
        Stack<Character> op = new Stack<Character>();
        for (int i = 0; i < s.length(); ++i)
        {
            if (s.charAt(i) == '(') {
                    ++bracketSummary;
                op.push('(');
            }
            else if (s.charAt(i) == ')') {
                --bracketSummary;
                if (bracketSummary < 0)
                    throw new ParsingException("Too many close brackets");
                while (op.peek() != '(') {
                    operationProcess(st, op.peek());
                    op.pop();
                }

                op.pop();
            }
            else if (minusHandler(s.charAt(i - 1), s.charAt(i), s.charAt(i + 1)) || isDigit(s.charAt(i)))
            {
                int points = 0;
                double coef = 1;
                if (s.charAt(i) == '-')
                {
                    coef = -1;
                    ++i;
                }
                StringBuilder operand = new StringBuilder();

                while (i < s.length() && isAllNum(s.charAt(i)))
                {
                    if (s.charAt(i) == '.')
                        ++points;
                    operand.append(s.charAt(i++));
                }
                if (points > 1)
                    throw new ParsingException("Too many close points");
                --i;
                if (isDigit(operand.charAt(0))) {
                    st.push(coef * parseDouble(operand.toString()));
                    empty = false;
                }
                else
                    throw new ParsingException("Begin from points");
            }
            else if (isOperation(s.charAt(i))) {
                operatorHandler(s.charAt(i - 1), s.charAt(i), s.charAt(i + 1));
                char currentOp = s.charAt(i);
                while (!op.isEmpty() && priority(op.peek()) >= priority(s.charAt(i)))
                    operationProcess(st, op.pop());
                op.push (currentOp);
            }
            else
                throw new ParsingException("error symbols");
        }
        if (bracketSummary != 0)
            throw new ParsingException ("Too many open brackets");
        if (empty)
            throw new ParsingException("Empty expression");
        while (!op.isEmpty())
            operationProcess(st, op.pop());
        return st.peek();
    }


}
