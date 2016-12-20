package ru.mipt.java2016.homework.g596.gerasimov.task4.NewCalculator;

import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Created by geras-artem on 12.10.16.
 */

public class NewCalculator implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Wrong expression");
        }
        expression = expression.replaceAll("[\\s]", "");
        ArrayList<Token> iExpr = tokenize(expression);
        if (iExpr.size() == 0) {
            throw new ParsingException("Wrong expression");
        }
        return calculation(iExpr);
    }

    private ArrayList<Token> tokenize(String expression) throws ParsingException {
        ArrayList<Token> res = new ArrayList<>();
        char currentChar;
        boolean gotNum = false;
        boolean gotUOp = false;

        for (int i = 0; i < expression.length(); ++i) {
            currentChar = expression.charAt(i);

            if (Character.isDigit(currentChar)) {
                StringBuilder sNum = new StringBuilder();
                boolean metDot = false;
                while (i < expression.length() && (Character.isDigit(currentChar) || (
                        currentChar == '.' && !metDot))) {
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
                if (currentChar == '(' && res.size() > 0 && res
                        .get(res.size() - 1) instanceof BracketToken && ((BracketToken) res
                        .get(res.size() - 1)).isOpening()) {
                    throw new ParsingException("Wrong expression");
                }
                gotNum = !(currentChar == '(');
                gotUOp = false;
                res.add(new BracketToken(currentChar));
            } else if (currentChar == '+' || currentChar == '-' || currentChar == '*'
                    || currentChar == '/') {
                if (!gotNum) {
                    if (gotUOp) {
                        throw new ParsingException("Wrong expression");
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
                            throw new ParsingException("Wrong expression");
                    }
                }
                gotNum = false;
                res.add(new OperatorToken(currentChar));
            } else {
                StringBuilder functionName = new StringBuilder();

                ++i;
                for (; i < expression.length() && currentChar != '('; ++i) {
                    functionName.append(currentChar);
                    currentChar = expression.charAt(i);
                }

                if (i == expression.length()) {
                    throw new ParsingException("Wrong expression");
                }

                int tempBracketBalance = 1;

                Vector<Double> arguments = new Vector<>();
                StringBuilder argument = new StringBuilder();

                for (; i < expression.length() && tempBracketBalance > 0; ++i) {
                    currentChar = expression.charAt(i);
                    if (currentChar == ',' && tempBracketBalance == 1) {
                        arguments.addElement(calculate(argument.toString()));
                        argument.setLength(0);
                        continue;
                    }
                    if (currentChar == '(') {
                        ++tempBracketBalance;
                    }
                    if (currentChar == ')') {
                        --tempBracketBalance;
                        if (tempBracketBalance == 0) {
                            arguments.addElement(calculate(argument.toString()));
                            break;
                        }
                    }
                    argument.append(currentChar);
                }

                if (i == expression.length() && tempBracketBalance != 0) {
                    throw new ParsingException("Wrong expression");
                }

                gotNum = true;
                gotUOp = false;
                res.add(new FunctionToken(functionName.toString(), arguments));
            }
        }

        return res;
    }

    private double calculation(ArrayList<Token> iExpr) throws ParsingException {
        Stack<Double> numbers = new Stack<>();
        Stack<Token> operators = new Stack<>();
        int bracketBalance = 0;

        for (Token tmp : iExpr) {
            if (tmp instanceof NumToken) {
                numbers.push(((NumToken) tmp).getValue());
            } else if (tmp instanceof FunctionToken) {
                numbers.push(((FunctionToken) tmp).getValue());
            } else if (tmp instanceof OperatorToken) {
                Operator op = ((OperatorToken) tmp).getOperator();
                while (operators.size() > 0 && operators.peek() instanceof OperatorToken) {
                    Operator prev = ((OperatorToken) operators.peek()).getOperator();
                    if ((op.getPriority() <= prev.getPriority() && op.getIsLA()) || (
                            op.getPriority() < prev.getPriority())) {
                        prev.use(numbers);
                        operators.pop();
                    } else {
                        break;
                    }
                }

                operators.push(tmp);
            } else {
                boolean isOpening = ((BracketToken) tmp).isOpening();
                if (isOpening) {
                    operators.push(tmp);
                    ++bracketBalance;
                } else {
                    --bracketBalance;
                    if (bracketBalance < 0) {
                        throw new ParsingException("Wrong expression");
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

        if (bracketBalance != 0) {
            throw new ParsingException("Wrong expression");
        }
        if (numbers.size() != 1) {
            throw new ParsingException("Wrong expression");
        }

        return numbers.pop();
    }
}
