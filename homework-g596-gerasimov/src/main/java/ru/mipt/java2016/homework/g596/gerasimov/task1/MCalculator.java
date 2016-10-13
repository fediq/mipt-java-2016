package ru.mipt.java2016.homework.g596.gerasimov.task1;

import java.util.ArrayList;
import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by geras-artem on 12.10.16.
 */

public class MCalculator implements Calculator {

    private ArrayList<Token> Tokenize(String expression) throws ParsingException {
        ArrayList<Token> res = new ArrayList<>();
        char c;
        boolean gotNum = false;

        for (int i = 0; i < expression.length(); ++i) {
            c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            } else if (Character.isDigit(c)) {
                StringBuilder num_s = new StringBuilder();
                boolean met_dot = false;
                while (i < expression.length() && (Character.isDigit(c) || (c == '.'
                        && !met_dot))) {
                    num_s.append(c);
                    if (c == '.') {
                        met_dot = true;
                    }
                    ++i;
                    if (i < expression.length()) {
                        c = expression.charAt(i);
                    }
                }
                --i;
                gotNum = true;
                res.add(new NumToken(Double.parseDouble(num_s.toString())));
            } else if (c == '(' || c == ')') {
                if (c == '(' && res.size() > 0 && res.get(res.size() - 1) instanceof BracketToken
                        && ((BracketToken) res.get(res.size() - 1)).getIsOpening()) {
                    throw new ParsingException("Wrong bracket combination!");
                }
                if (c == '(') {
                    gotNum = false;
                } else {
                    gotNum = true;
                }
                res.add(new BracketToken(c));
            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (!gotNum) {
                    switch (c) {
                        case '+':
                            c = '#';
                            break;
                        case '-':
                            c = '&';
                            break;
                        default:
                            throw new ParsingException("Wrong operator order");
                    }
                }
                gotNum = false;
                res.add(new OperatorToken(c));
            } else {
                throw new ParsingException("Wrong symbol!");
            }
        }

        return res;
    }

    private double Calculation(ArrayList<Token> i_expr) throws ParsingException {
        Stack<Double> numbers = new Stack<>();
        Stack<Token> operators = new Stack<>();
        int b_balance = 0;
        boolean gotNum = false;

        for (Token tmp : i_expr) {
            if (tmp instanceof NumToken) {
                numbers.push(((NumToken) tmp).getValue());
                gotNum = true;
            } else if (tmp instanceof OperatorToken) {
                Operator op = ((OperatorToken) tmp).getOperator();
                while (operators.size() > 0 && operators.peek() instanceof OperatorToken) {
                    Operator prev = ((OperatorToken) operators.peek()).getOperator();
                    if ((op.priority <= prev.priority && op.isLA) || (op.priority
                            < prev.priority)) {
                        prev.use(numbers);
                        operators.pop();
                    } else {
                        break;
                    }
                }

                operators.push(tmp);
            } else {
                boolean isOpening = ((BracketToken) tmp).getIsOpening();
                if (isOpening) {
                    operators.push(tmp);
                    ++b_balance;
                } else {
                    --b_balance;
                    if (b_balance < 0) {
                        throw new ParsingException("Wrong bracket balance!");
                    }
                    while (operators.peek() instanceof OperatorToken) {
                        ((OperatorToken) operators.pop()).getOperator().use(numbers);
                    }
                    operators.pop();
                }
            }
        }
        while (!operators.empty()) {
            ((OperatorToken) operators.pop()).getOperator().use(numbers);
        }

        if (b_balance != 0) {
            throw new ParsingException("Wrong bracket balance!");
        }
        if (numbers.size() != 1) {
            throw new ParsingException("Wrong input!");
        }

        return numbers.pop();
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null string");
        }
        ArrayList<Token> i_expr = Tokenize(expression);
        if (i_expr.size() == 0) {
            throw new ParsingException("Empty input!");
        }

        return Calculation(i_expr);
    }

    enum Operator {
        PLUS(1, 2, true), MINUS(1, 2, true), MULTIPLY(2, 2, true), DIVIDE(2, 2, true), U_PLUS(3, 1,
                false), U_MINUS(3, 1, false);

        int priority;
        int valency;
        boolean isLA;

        Operator(int priority_, int valency_, boolean isLA_) {
            priority = priority_;
            valency = valency_;
            isLA = isLA_;
        }

        private void use(Stack<Double> nums) throws ParsingException {
            if (nums.size() < valency) {
                throw new ParsingException("Not enought args for operator");
            }

            double tmp;
            switch (this) {
                case PLUS:
                    tmp = nums.pop();
                    tmp += nums.pop();
                    break;
                case MINUS:
                    tmp = -nums.pop();
                    tmp += nums.pop();
                    break;
                case MULTIPLY:
                    tmp = nums.pop();
                    tmp *= nums.pop();
                    break;
                case DIVIDE:
                    tmp = 1 / nums.pop();
                    tmp *= nums.pop();
                    break;
                case U_PLUS:
                    tmp = nums.pop();
                    break;
                case U_MINUS:
                    tmp = -nums.pop();
                    break;
                default:
                    throw new IllegalStateException();
            }
            nums.push(tmp);
        }
    }


    private class Token {

    }


    private class BracketToken extends Token {
        private boolean isOpening;

        private BracketToken(char c) {
            switch (c) {
                case '(':
                    isOpening = true;
                    break;
                case ')':
                    isOpening = false;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private boolean getIsOpening() {
            return isOpening;
        }
    }


    private class NumToken extends Token {
        private double value;

        private NumToken(double value_) {
            value = value_;
        }

        private double getValue() {
            return value;
        }
    }


    private class OperatorToken extends Token {
        Operator op;

        private OperatorToken(char c) {
            switch (c) {
                case '+':
                    op = Operator.PLUS;
                    break;
                case '-':
                    op = Operator.MINUS;
                    break;
                case '*':
                    op = Operator.MULTIPLY;
                    break;
                case '/':
                    op = Operator.DIVIDE;
                    break;
                case '#':
                    op = Operator.U_PLUS;
                    break;
                case '&':
                    op = Operator.U_MINUS;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        private Operator getOperator() {
            return op;
        }
    }

}
