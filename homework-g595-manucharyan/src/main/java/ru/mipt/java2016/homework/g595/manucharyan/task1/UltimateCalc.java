package ru.mipt.java2016.homework.g595.manucharyan.task1;

import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Пример реализации калькулятора средствами JEval.
 *
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public class UltimateCalc implements Calculator {

    private Stack<Symbol> operations = new Stack<>();
    private Stack<Double> stack = new Stack<>(); // contains expression in postfix notation
    private int pos = 0; //position of current symbol in expression


    private int getPriority(Symbol s) throws ParsingException {
        switch (s) {
            case OBRACKET:
                return 0;
            case CBRACKET:
                return 0;
            case ADD:
                return 1;
            case SUB:
                return 1;
            case MUL:
                return 2;
            case DIV:
                return 2;
            case UNOADD:
                return 3;
            case UNOSUB:
                return 3;
            default:
                freeResource();
                throw new ParsingException("wrong");
        }
    }

    private static SymbolType getSymbolType(Symbol s) {
        switch (s) {
            case DIGIT:
                return SymbolType.NUMBER;
            case ADD:
                return SymbolType.OPERATOR;
            case UNOADD:
                return SymbolType.OPERATOR;
            case SUB:
                return SymbolType.OPERATOR;
            case UNOSUB:
                return SymbolType.OPERATOR;
            case MUL:
                return SymbolType.OPERATOR;
            case DIV:
                return SymbolType.OPERATOR;
            case OBRACKET:
                return SymbolType.BRACKET;
            case CBRACKET:
                return SymbolType.BRACKET;
            case SPACE:
                return SymbolType.SPACE;
            default:
                return SymbolType.NONE;
        }
    }

    //is this symbol right-Associativity
    private static boolean isRightAssociative(Symbol s) {
        //in future, here can be added some right-ass operations =)
        switch (s) {
            default:
                return false;
        }
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        try {
            if (expression == null) {
                throw new ParsingException("wrong");
            }

            //parse expresion and execute it it at hte same time
            //Really, why should we make reverse polish notation and only than calculate it?
            //We can do it both!
            while (pos < expression.length()) {
                Symbol s = parseString(expression); //current symbol

                switch (getSymbolType(s)) {
                    case NUMBER:
                        double val = getNumber(expression);
                        stack.push(val);
                        break;
                    case OPERATOR:
                        pushOperationInStack(s);
                        break;
                    case BRACKET:
                        if (s == Symbol.OBRACKET) { //open bracket
                            operations.push(s);
                        } else if (s == Symbol.CBRACKET) { //close bracket
                            Symbol tmp = operations.pop();
                            while (!operations.isEmpty() && tmp != Symbol.OBRACKET) {
                                calculateOperation(tmp);
                                tmp = operations.pop();
                            }

                            if (tmp != Symbol.OBRACKET) {
                                throw new ParsingException("wrong");
                            }
                        }

                        break;
                    case SPACE:
                        break;
                    default:
                        break;
                }

                pos++;
            }

            //if we parse all expression, and stack isn't empty
            while (stack.size() != 1 || operations.size() > 0) {
                if (operations.isEmpty()) {
                    throw new ParsingException("wrong");
                } else {
                    Symbol s = operations.pop();
                    calculateOperation(s);
                }
            }

            return stack.pop();
        } finally {
            freeResource();
        }
    }

    private double getNumber(String expression) throws ParsingException {
        char c = expression.charAt(pos);
        if (c < '0' || c > '9') {
            freeResource();
            throw new ParsingException("wrong");
        }

        double res = 0.0;

        //find integer part of number
        while (c >= '0' && c <= '9') {
            res *= 10;
            res += c - '0';
            if (pos == expression.length() - 1) {
                break;
            }
            c = expression.charAt(++pos);
        }

        //find fractional part of number
        if (c == '.') {
            double pow = 0.1;
            if (pos >= expression.length() - 1) {
                freeResource();
                throw new ParsingException("wrong");
            }

            c = expression.charAt(++pos);
            while (c >= '0' && c <= '9') {
                res += (c - '0') * pow;
                pow *= 0.1;
                if (pos == expression.length() - 1) {
                    break;
                }
                c = expression.charAt(++pos);
            }
        }

        //correct pos
        if (c < '0' || c > '9') {
            pos--;
        }

        return res;
    }

    //execute operation
    private void calculateOperation(Symbol s) throws ParsingException {
        if (stack.size() < 1) {
            freeResource();
            throw new ParsingException("wrong");
        }

        double b;
        b = stack.pop();

        if (s == Symbol.UNOADD || s == Symbol.UNOSUB) {
            if (s == Symbol.UNOSUB) {
                stack.push(-b);
            } else {
                stack.push(b);
            }
        } else {
            if (stack.isEmpty()) {
                freeResource();
                throw new ParsingException("wrong");
            }
            double a = stack.pop();

            switch (s) {

                case ADD:
                    stack.push(a + b);
                    break;
                case SUB:
                    stack.push(a - b);
                    break;
                case MUL:
                    stack.push(a * b);
                    break;
                case DIV:
                    stack.push(a / b);
                    break;
                default:
                    freeResource();
                    throw new ParsingException("wrong");
            }
        }
    }

    private Symbol parseString(String expression) throws ParsingException {
        char c = expression.charAt(pos);
        if (c >= '0' && c <= '9') {
            return Symbol.DIGIT;
        }
        if (c == '+' || c == '-') {
            char oldc = c;
            int oldpos = pos;
            boolean uno = true;

            //try to understand is operation uno or not
            if (pos > 0) {
                c = expression.charAt(--pos);
                while (c == ' ' || c == '\n' || c == '\t') {
                    if (pos > 0) {
                        c = expression.charAt(--pos);
                    } else {
                        break;
                    }
                }
                if (c == ')' || c >= '0' && c <= '9') {
                    uno = false;
                }
            }

            //return answer
            pos = oldpos;
            if (uno) {
                if (oldc == '+') {
                    return Symbol.UNOADD;
                } else {
                    return Symbol.UNOSUB;
                }
            } else {
                if (oldc == '+') {
                    return Symbol.ADD;
                } else {
                    return Symbol.SUB;
                }
            }
        }
        if (c == '*') {
            return Symbol.MUL;
        }
        if (c == '/') {
            return Symbol.DIV;
        }
        if (c == '(') {
            return Symbol.OBRACKET;
        }
        if (c == ')') {
            return Symbol.CBRACKET;
        }
        if (c == ' ' || c == '\n' || c == '\t') {
            return Symbol.SPACE;
        }

        //if nothing of that
        freeResource();
        throw new ParsingException("wrong");
    }

    //push a single operation in operation stack ("operations")
    private void pushOperationInStack(Symbol s) throws ParsingException {
        if (operations.isEmpty()) {
            operations.push(s);
        } else {
            Symbol tmp = operations.lastElement();

            //while it's necessary, pop operation and execute it
            while ((getPriority(s) < getPriority(tmp)) && isRightAssociative(s) ||
                    (getPriority(s) <= getPriority(tmp)) && !isRightAssociative(s)) {

                operations.pop();
                calculateOperation(tmp);

                if (operations.isEmpty()) {
                    break;
                } else {
                    tmp = operations.lastElement();
                }
            }

            operations.push(s);
        }
    }

    private void freeResource() {
        stack.clear();
        operations.clear();
    }

    private enum Symbol { DIGIT, ADD, UNOADD, SUB, UNOSUB, MUL, DIV, OBRACKET, CBRACKET, SPACE }

    private enum SymbolType { OPERATOR, NUMBER, BRACKET, SPACE, NONE }
}
