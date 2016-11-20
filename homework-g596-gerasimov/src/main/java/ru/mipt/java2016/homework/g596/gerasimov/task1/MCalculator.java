package ru.mipt.java2016.homework.g596.gerasimov.task1;

import java.util.ArrayList;
import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by geras-artem on 12.10.16.
 */

public class MCalculator implements Calculator {

    private ArrayList<Token> tokenize(String expression) throws ParsingException {
        ArrayList<Token> res = new ArrayList<>();
        char currentChar;
        boolean gotNum = false;
        boolean gotUOp = false;

        for (int i = 0; i < expression.length(); ++i) {
            currentChar = expression.charAt(i);

            if (Character.isWhitespace(currentChar)) {
                continue;
            } else if (Character.isDigit(currentChar)) {
                StringBuilder sNum = new StringBuilder();
                boolean metDot = false;
                while (i < expression.length() && (Character.isDigit(currentChar)
                        || (currentChar == '.' && !metDot))) {
                    sNum.append(currentChar);
                    if (currentChar == '.') {
                        metDot = true;
                    }
                    ++i;
                    if (i < expression.length()) {
                        currentChar = expression.charAt(i);
                    }
                }
                --i;
                gotNum = true;
                gotUOp = false;
                res.add(new NumToken(Double.parseDouble(sNum.toString())));
            } else if (currentChar == '(' || currentChar == ')') {
                if (currentChar == '(' && res.size() > 0
                        && res.get(res.size() - 1) instanceof BracketToken &&
                        ((BracketToken) res.get(res.size() - 1)).getIsOpening()) {
                    throw new ParsingException("Wrong bracket combination!");
                }
                gotNum = !(currentChar == '(');
                gotUOp = false;
                res.add(new BracketToken(currentChar));
            } else if (currentChar == '+' || currentChar == '-' || currentChar == '*'
                       || currentChar == '/') {
                if (!gotNum) {
                    if (gotUOp) {
                        throw new ParsingException("Wrong usage operators!");
                    }
                    switch (currentChar) {
                        case '+':
                            currentChar = '#';
                            gotUOp = true;
                            break;
                        case '-':
                            currentChar = '&';
                            gotUOp = true;
                            break;
                        default:
                            throw new ParsingException("Wrong operator order");
                    }
                }
                gotNum = false;
                res.add(new OperatorToken(currentChar));
            } else {
                throw new ParsingException("Wrong symbol!");
            }
        }

        return res;
    }

    private double calculation(ArrayList<Token> iExpr) throws ParsingException {
        Stack<Double> numbers = new Stack<>();
        Stack<Token> operators = new Stack<>();
        int bBalance = 0;

        for (Token tmp : iExpr) {
            if (tmp instanceof NumToken) {
                numbers.push(((NumToken) tmp).getValue());
            } else if (tmp instanceof OperatorToken) {
                Operator op = ((OperatorToken) tmp).getOperator();
                while (operators.size() > 0 && operators.peek() instanceof OperatorToken) {
                    Operator prev = ((OperatorToken) operators.peek()).getOperator();
                    if ((op.getPriority() <= prev.getPriority() && op.getIsLA())
                        || (op.getPriority() < prev.getPriority())) {
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
                    ++bBalance;
                } else {
                    --bBalance;
                    if (bBalance < 0) {
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

        if (bBalance != 0) {
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
        ArrayList<Token> iExpr = tokenize(expression);
        if (iExpr.size() == 0) {
            throw new ParsingException("Empty input!");
        }
        return calculation(iExpr);
    }

    private enum Operator {
        PLUS(1, 2, true), MINUS(1, 2, true), MULTIPLY(2, 2, true), DIVIDE(2, 2, true),
        U_PLUS(3, 1, false), U_MINUS(3, 1, false);

        private int priority;
        private int valency;
        private boolean isLA;

        Operator(int priority, int valency, boolean isLA) {
            this.priority = priority;
            this.valency = valency;
            this.isLA = isLA;
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

        private int getPriority() {
            return priority;
        }

        private int getValency() {
            return valency;
        }

        private boolean getIsLA() {
            return isLA;
        }
    }


    abstract class Token {
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

        private NumToken(double value) {
            this.value = value;
        }

        private double getValue() {
            return value;
        }
    }


    private class OperatorToken extends Token {
        private Operator op;

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
