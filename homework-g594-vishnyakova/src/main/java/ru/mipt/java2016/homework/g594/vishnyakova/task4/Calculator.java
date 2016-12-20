package ru.mipt.java2016.homework.g594.vishnyakova.task4;

/**
 * Created by Nina on 16.12.16.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.Character.*;

public class Calculator {

    private static final Logger LOG = LoggerFactory.getLogger(Calculator.class);

    public static final Calculator INSTANCE = new Calculator();

    private double calc(HashMap<String, String> vars, String expression) throws ParsingException {
        LinkedList<Character> operations = new LinkedList<Character>();
        LinkedList<Double> numbers = new LinkedList<Double>();
        expression = expression.replaceAll("\\s", "");
        if (expression == null || haveSplitedNumbers(expression) || expression.equals("")) {
            throw new ParsingException("Bad expressions");
        }
        expression = "(" + expression + ")";
        operations.clear();
        numbers.clear();
        double lastNumber = 0;
        Boolean readNumber = false;
        Boolean hadDot = false;
        double afterDot = 1;
        Boolean hadSomeAfterDot = false;
        Boolean canBeUnary = true;
        boolean readVar = false;
        String var = "";
        boolean needJump = false;
        int jump = -1;
        for (int i = 0; i < expression.length(); ++i) {
            if (needJump) {
                if (i < jump) {
                    continue;
                }
                needJump = false;
            }
            afterDot /= 10;
            char cur = expression.charAt(i);
            if (isDigOrDot(cur)) {
                canBeUnary = false;
                if (isDigit(cur)) {
                    readNumber = true;
                    if (hadDot) {
                        hadSomeAfterDot = true;
                        lastNumber += afterDot * digit(cur, 10);
                        continue;
                    }
                    lastNumber = lastNumber * 10 + digit(cur, 10);
                    continue;
                }
                if (isLetter(cur)) {
                    readVar = true;
                    var += cur;
                    continue;
                }
                if (hadDot || !readNumber) {
                    throw new ParsingException("Bad dots in a number");
                }
                hadDot = true;
                afterDot = 1;
                continue;
            }
            if (readNumber || !isAvaliableSymbol(cur)) {
                if ((hadDot && !hadSomeAfterDot) || !isAvaliableSymbol(cur)) {
                    throw new ParsingException("Number like '1.'");
                }
                numbers.push(lastNumber);
                lastNumber = 0;
                hadDot = false;
                readNumber = false;
            }
            if (readVar) {
                if (!vars.containsKey(var)) {
                    if (var.equals("rnd")) {
                        int pos = i;
                        if (expression.charAt(pos) != '(' || pos + 1 >= expression.length()
                                || expression.charAt(pos + 1) != ')') {
                            throw new ParsingException("No such variable or func");
                        }
                        numbers.push(Math.random());
                        var = "";
                        readVar = false;
                        needJump = true;
                        jump = pos + 2;
                        continue;
                    }
                    if (oneParam(var)) {
                        StringBuilder sb = new StringBuilder();
                        int pos = parseIfOne(expression, i, sb);
                        numbers.push(countFunc(var, calc(vars, sb.toString())));
                        var = "";
                        readVar = false;
                        needJump = true;
                        jump = pos;
                        continue;
                    }
                    if (twoParam(var)) {
                        StringBuilder sb1 = new StringBuilder();
                        StringBuilder sb2 = new StringBuilder();
                        int pos = parseIfTwo(expression, i, sb1, sb2);
                        String one = sb1.toString();
                        String two = sb2.toString();
                        LOG.debug(one + " " + two);
                        numbers.push(countFunc(var, calc(vars, one), calc(vars, two)));
                        var = "";
                        readVar = false;
                        needJump = true;
                        jump = pos;
                        continue;
                    }
                    throw new ParsingException("Unknown variable or function");
                }
                numbers.push(Double.parseDouble(vars.get(var)));
                var = "";
                readVar = false;
            }
            if (cur == '(') {
                operations.push(cur);
                canBeUnary = true;
                continue;
            }
            if (cur == ')') {
                char back = '^';
                while (operations.size() != 0) {
                    back = operations.pop();
                    if (back == '(') {
                        break;
                    }
                    doOperation(back, numbers);
                }
                canBeUnary = false;
                if (back != '(') {
                    LOG.debug(Integer.toString(i));
                    throw new ParsingException("Broken brace balance");
                }
                continue;
            }
            if (canBeUnary && canUnary(cur)) {
                cur = makeUnary(cur);
            }
            while (operations.size() != 0) {
                char back = operations.pop();
                if (getPriority(back) >= getPriority(cur)) {
                    doOperation(back, numbers);
                } else {
                    operations.push(back);
                    break;
                }
            }
            operations.push(cur);
            canBeUnary = true;
        }
        if (numbers.size() != 1 || operations.size() != 0) {
            throw new ParsingException("Wrong input");
        }
        double answer = numbers.pop();
        LOG.debug(Double.toString(answer) + " " + expression);
        return answer;
    }

    public double calculate(HashMap<String, String> vars, String expression) throws ParsingException {
        return calc(vars, expression);
    }

    private void doOperation(char c, LinkedList<Double> numbers) throws ParsingException {
        if (isUnary(c)) {
            if (numbers.size() < 1) {
                throw new ParsingException("Not enough operands for unary operation");
            }
            double x = numbers.pop();
            if (c == '@') {
                numbers.push(-x);
            }
            return;
        }

        if (numbers.size() < 2) {
            throw new ParsingException("Not enough operands for operation");
        }
        double x = numbers.pop();
        double y = numbers.pop();
        if (c == '+') {
            numbers.push(y + x);
        }
        if (c == '-') {
            numbers.push(y - x);
        }
        if (c == '*') {
            numbers.push(y * x);
        }
        if (c == '/') {
            numbers.push(y / x);
        }
    }

    private Boolean oneParam(String s) {
        return (s.equals("sin") || s.equals("cos") || s.equals("tg") || s.equals("sqrt") ||
                s.equals("abs") || s.equals("sign"));
    }

    private Boolean twoParam(String s) {
        return (s.equals("pow") || s.equals("log") || s.equals("max") || s.equals("min"));
    }

    private Double countFunc(String f, Double x, Double y) {
        if (f.equals("pow")) {
            return Math.pow(x, y);
        }
        if (f.equals("log")) {
            return Math.log(x) / Math.log(y);
        }
        if (f.equals("max")) {
            return Math.max(x, y);
        }
        if (f.equals("min")) {
            return Math.min(x, y);
        }
        return 0.0;
    }

    private Double countFunc(String f, Double x) {
        if (f.equals("sin")) {
            return Math.sin(x);
        }
        if (f.equals("cos")) {
            return Math.cos(x);
        }
        if (f.equals("tg")) {
            return Math.tan(x);
        }
        if (f.equals("sqrt")) {
            return Math.sqrt(x);
        }
        if (f.equals("abs")) {
            return Math.abs(x);
        }
        if (f.equals("sign")) {
            return Math.signum(x);
        }
        return 0.0;
    }

    int parseIfOne(String expression, int i, StringBuilder sb) throws ParsingException {
        int pos = i;
        if (expression.charAt(pos) != '(') {
            throw new ParsingException("No variable/funct");
        }
        int newBalance = 1;
        pos += 1;
        while (newBalance != 0 && pos < expression.length()) {
            if (expression.charAt(pos) == '(') {
                newBalance += 1;
            }
            if (expression.charAt(pos) == ')') {
                newBalance -= 1;
            }
            if (newBalance != 0) {
                sb.append(expression.charAt(pos));
            }
            pos += 1;
        }
        if (pos == expression.length() && newBalance != 0) {
            throw new ParsingException("Broken balance");
        }
        return pos;
    }

    int parseIfTwo(String expression, int i, StringBuilder sb1, StringBuilder sb2) throws ParsingException {
        boolean secondAlready = false;
        int pos = i;
        if (expression.charAt(pos) != '(') {
            throw new ParsingException("No variable/funct");
        }
        int newBalance = 1;
        pos += 1;
        while (newBalance != 0 && pos < expression.length()) {
            if (expression.charAt(pos) == '(') {
                newBalance += 1;
            }
            if (expression.charAt(pos) == ')') {
                newBalance -= 1;
            }
            if (newBalance != 0) {
                if (newBalance == 1 && expression.charAt(pos) == ',') {
                    secondAlready = true;
                } else {
                    if (!secondAlready) {
                        sb1.append(expression.charAt(pos));
                    } else {
                        sb2.append(expression.charAt(pos));
                    }
                }
            }
            pos += 1;
        }
        if (pos == expression.length() && newBalance != 0) {
            throw new ParsingException("Broken balance");
        }
        return pos;
    }

    private Boolean isAvaliableSymbol(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')');
    }

    private Boolean isUnary(char c) {
        return (c == '&' || c == '@'); // + -
    }

    private Boolean canUnary(char c) {
        return (c == '+' || c == '-');
    }

    char makeUnary(char c) {
        if (c == '+') {
            return '&';
        }
        return '@';
    }

    private Boolean isDigOrDot(char c) {
        return isDigit(c) || c == '.' || isLetter(c);
    }

    private int getPriority(char c) {
        if (isUnary(c)) {
            return 2;
        }
        if (c == '+' || c == '-') {
            return 0;
        }
        if (c == '*' || c == '/') {
            return 1;
        }
        return -1;
    }

    private Boolean haveSplitedNumbers(String expression) {
        Character lastMeaning = '^';
        for (int i = 0; i < expression.length(); ++i) {
            if (isDigOrDot(lastMeaning) && isDigOrDot(expression.charAt(i))
                    && !isDigOrDot(expression.charAt(i - 1))) {
                return true;
            }
            if (!isWhitespace(expression.charAt(i))) {
                lastMeaning = expression.charAt(i);
            }
        }
        return false;
    }
}