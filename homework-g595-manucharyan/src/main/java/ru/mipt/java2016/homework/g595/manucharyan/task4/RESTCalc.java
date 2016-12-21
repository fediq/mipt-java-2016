package ru.mipt.java2016.homework.g595.manucharyan.task4;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * Пример реализации калькулятора средствами JEval.
 *
 * @author vanderwardan
 * @since 17.12.16
 */
public class RESTCalc implements Calculator {

    private Stack<Token> operations = new Stack<>();
    private Stack<Double> stack = new Stack<>(); // contains expression in postfix notation
    private int pos = 0; //position of current symbol in expression

    //names of predefined functions and their valencies
    private final Map<String, Integer> predefinedFunctions = new HashMap<>();
    //values of variables
    private Map<String, Double> variables = new HashMap<>();

    RESTCalc() {
        predefinedFunctions.put("sin", 1);
        predefinedFunctions.put("cos", 1);
        predefinedFunctions.put("tg", 1);
        predefinedFunctions.put("sqrt", 1);
        predefinedFunctions.put("pow", 2);
        predefinedFunctions.put("abs", 1);
        predefinedFunctions.put("sign", 1);
        predefinedFunctions.put("log", 1);
        predefinedFunctions.put("log2", 1);
        predefinedFunctions.put("rnd", 0);
        predefinedFunctions.put("max", 2);
        predefinedFunctions.put("min", 2);
    }

    private int getPriority(Symbol s) throws ParsingException {
        switch (s) {
            case FUNCTION:
                return 0;
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

    private static SymbolType getTokenType(Token t) {
        switch (t.getSymbol()) {
            case NUMBER:
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
            case VARIABLE:
                return SymbolType.VARIABLE;
            case FUNCTION:
                return SymbolType.FUNCTION;
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

            //parse expresion and execute it it at the same time
            //Really, why should we make reverse polish notation and only than calculate it?
            //We can do it both!
            while (pos < expression.length()) {
                Token t = parseString(expression); //current symbol

                switch (getTokenType(t)) {
                    case NUMBER:
                        stack.push(t.getValue());
                        break;
                    case OPERATOR:
                        pushOperationInStack(t);
                        break;
                    case FUNCTION:
                        operations.push(t);
                        break;
                    case BRACKET:
                        if (t.getSymbol() == Symbol.OBRACKET) { //open bracket
                            operations.push(t);
                        } else if (t.getSymbol() == Symbol.CBRACKET) { //close bracket
                            Token tmp = operations.pop();
                            while (!operations.isEmpty() && tmp.getSymbol() != Symbol.OBRACKET ||
                                    tmp.getSymbol() == Symbol.FUNCTION) {
                                calculateOperation(tmp);
                                if (tmp.getSymbol() == Symbol.FUNCTION) {
                                    break;
                                }
                                tmp = operations.pop();
                            }

                            if (tmp.getSymbol() != Symbol.OBRACKET && tmp.getSymbol() != Symbol.FUNCTION) {
                                throw new ParsingException("wrong");
                            }
                        }

                        break;
                    case VARIABLE:
                        if (!variables.containsKey(t.getName())) {
                            throw new ParsingException("no such variable");
                        }
                        stack.push(variables.get(t.getName()));
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
                    Token t = operations.pop();
                    calculateOperation(t);
                }
            }

            return stack.pop();
        } finally {
            freeResource();
        }
    }

    private double getNumber(String expression) throws ParsingException {
        char c = expression.charAt(pos);
        if (!Character.isDigit(c)) {
            freeResource();
            throw new ParsingException("wrong");
        }

        double res = 0.0;

        //find integer part of number
        while (Character.isDigit(c)) {
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
            while (Character.isDigit(c)) {
                res += (c - '0') * pow;
                pow *= 0.1;
                if (pos == expression.length() - 1) {
                    break;
                }
                c = expression.charAt(++pos);
            }
        }

        //correct pos
        if (!Character.isDigit(c)) {
            pos--;
        }

        return res;
    }

    private double calculateFunction(String name, double[] operands) {
        assert (predefinedFunctions.containsKey(name));
        switch (name) {
            case "sin":
                return Math.sin(operands[0]);
            case "cos":
                return Math.cos(operands[0]);
            case "tg":
                return Math.tan(operands[0]);
            case "sqrt":
                return Math.sqrt(operands[0]);
            case "pow":
                return Math.pow(operands[0], operands[1]);
            case "abs":
                return Math.abs(operands[0]);
            case "sign":
                return Math.signum(operands[0]);
            case "log":
                return Math.log(operands[0]);
            case "log2":
                return Math.log(operands[0]) / Math.log(2);
            case "rnd":
                return (new Random()).nextDouble();
            case "max":
                return Math.max(operands[0], operands[1]);
            case "min":
                return Math.min(operands[0], operands[1]);
            default:
                return 0.0;
        }
    }

    //return name of variable or function(in this case, without open bracket)
    private String getName(String expression) throws ParsingException {
        int oldpos = pos;
        String name = "";
        char c = expression.charAt(pos);

        assert (!Character.isDigit(c));

        while (Character.isLetter(c) || c == '_' || Character.isDigit(c)) {
            name += c;
            if (pos == expression.length() - 1) {
                return name;
            }
            c = expression.charAt(++pos);
        }
        pos--;
        return name;
    }

    //execute operation
    private void calculateOperation(Token t) throws ParsingException {
        if (stack.size() < t.getValency()) {
            freeResource();
            throw new ParsingException("wrong");
        }

        //get all operands
        double[] operands = new double[t.getValency()];

        for (int i = 0; i < t.getValency(); ++i) {
            operands[t.getValency() - i - 1] = stack.pop();
        }

        //calculate operation
        switch (t.getSymbol()) {
            case ADD:
                stack.push(operands[0] + operands[1]);
                break;
            case UNOADD:
                stack.push(operands[0]);
                break;
            case SUB:
                stack.push(operands[0] - operands[1]);
                break;
            case UNOSUB:
                stack.push(-operands[0]);
                break;
            case MUL:
                stack.push(operands[0] * operands[1]);
                break;
            case DIV:
                stack.push(operands[0] / operands[1]);
                break;
            case FUNCTION:
                stack.push(calculateFunction(t.getName(), operands));
                break;
            default:
                freeResource();
                throw new ParsingException("wrong");
        }
    }

    private Token parseString(String expression) throws ParsingException {
        char c = expression.charAt(pos);
        if (Character.isDigit(c)) {
            return new Token(Symbol.NUMBER, getNumber(expression));
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
                    return new Token(Symbol.UNOADD);
                } else {
                    return new Token(Symbol.UNOSUB);
                }
            } else {
                if (oldc == '+') {
                    return new Token(Symbol.ADD);
                } else {
                    return new Token(Symbol.SUB);
                }
            }
        }
        if (c == '*') {
            return new Token(Symbol.MUL);
        }
        if (c == '/') {
            return new Token(Symbol.DIV);
        }
        if (c == '(') {
            return new Token(Symbol.OBRACKET);
        }
        if (c == ')') {
            return new Token(Symbol.CBRACKET);
        }
        if (c == '_' || Character.isLetter(c)) {
            String name = getName(expression);
            if (pos < expression.length() - 1 && expression.charAt(++pos) == '(') {
                if (!predefinedFunctions.containsKey(name)) {
                    throw new ParsingException("no such a function");
                }
                return new Token(Symbol.FUNCTION, name, predefinedFunctions.get(name));
            } else {
                if (!variables.containsKey(name)) {
                    throw new ParsingException("no such a variable");
                }
                pos--;
                return new Token(Symbol.VARIABLE, name, predefinedFunctions.get(name));
            }
        }
        if (c == ' ' || c == '\n' || c == '\t' || c == ',') {
            return new Token(Symbol.SPACE);
        }

        //if nothing of that
        freeResource();
        throw new ParsingException("wrong");
    }

    //push a single operation in operation stack ("operations")
    private void pushOperationInStack(Token t) throws ParsingException {
        if (operations.isEmpty()) {
            operations.push(t);
        } else {
            Token tmp = operations.lastElement();

            //while it's necessary, pop operation and execute it
            while ((getPriority(t.getSymbol()) < getPriority(tmp.getSymbol())) && isRightAssociative(t.getSymbol()) ||
                    (getPriority(t.getSymbol()) <= getPriority(tmp.getSymbol())) && !isRightAssociative(t.getSymbol())) {

                operations.pop();
                calculateOperation(tmp);

                if (operations.isEmpty()) {
                    break;
                } else {
                    tmp = operations.lastElement();
                }
            }

            operations.push(t);
        }
    }

    private void freeResource() {
        stack.clear();
        operations.clear();

    }

    public enum Symbol {
        NUMBER, ADD, UNOADD, SUB, UNOSUB, MUL, DIV, OBRACKET,
        CBRACKET, VARIABLE, FUNCTION, SPACE, NONE
    }

    public enum SymbolType { OPERATOR, NUMBER, BRACKET, SPACE, VARIABLE, FUNCTION, NONE }
}
