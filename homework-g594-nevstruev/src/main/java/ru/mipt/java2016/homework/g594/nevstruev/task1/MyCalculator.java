package ru.mipt.java2016.homework.g594.nevstruev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Objects;

/**
 * Created by Владислав on 11.10.2016.
 */
public class MyCalculator implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null pointer");
        }
        expression = expression.replaceAll("\n", "").replaceAll(" ", "").replaceAll("\t", "");
        int balance = 0;
        for (int i = 0; i < expression.length(); ++i) {
            char symbol = expression.charAt(i);
            if (!((symbol >= '0' && symbol <= '9')
                    || symbol == '(' || symbol == ')' || symbol == '-' || symbol == '+' || symbol == '/' ||
                    symbol == '*' || symbol == '.')) {
                throw new ParsingException("Incorrect symbol");
            }
            if (symbol == '(') {
                ++balance;
            }
            if (symbol == ')') {
                --balance;
            }
            if (balance < 0) {
                throw new ParsingException("Incorrect bracket sequence");
            }
            char lastSymbol;
            if (i > 0) {
                lastSymbol = expression.charAt(i - 1);
            } else {
                continue;
            }
            if (incorrectString(lastSymbol, symbol)) {
                throw new ParsingException("Incorrect");
            }
        }
        if (balance != 0) {
            throw new ParsingException("Incorrect bracket sequence");
        }
        return getInt(expression);
    }

    private boolean incorrectString(char last, char next) {
        //В этом условном операторе я проверяю, что никакие две операции не идут подряд.
        //Утверждается, что если предыдущий символ есть операция, и следующий тоже, то у нас есть две операции
        //подряд, что недопустимо.
        if ((last == '+' || last == '-' || last == '*' || last == '/' || last == '.') &&
                (next == '+' || next == '.' || next == '*' || next == '/')) {
            return true;
        }
        //Здесь я проверяю, что после открывающией скобки не идёт никакой операции, кроме, может быть, унарного
        //минуса. Я предполагаю, что унарный плюс - запрещённая операция
        if (last == '(' && (next == '+' || next == '*' || next == '/' || next == '.')) {
            return true;
        }
        //Тут проверяется, что перед закрывающей скобкой также не идёт никакой операции.
        //Если такая операция есть, то выражение некорректно.
        if (next == ')' && 
                (last == '+' || last == '-' || last == '*' || last == '/' || last == '.')) {
            return true;
        }
        //Я утверждаю, что перед открывающей скобкой должна обязательно идти операция, т.е.
        //цифра или точка недопустимы.
        //То, что перед открывающей скобкой идёт закрывающая я проверяю в последней проверке.
        if (next == '(' && ((last >= '0' && last <= '9') || last == '.')) {
            return true;
        }
        //Тут аналогично проверяется, что после закрывающей идёт какая-либо операция
        //Так обернуть последнюю проверку в просто return мне предложила IDEA, и я последовал её совету.
        return last == ')' && ((next >= '0' && next <= '9') || next == '.' || next == '(');
    }

    private double getInt(String expression) throws ParsingException {
        int balance = 0;
        boolean isPlus = false;
        double sum = 0.0;
        int lastAct = 0;
        if (Objects.equals(expression, "")) {
            throw new ParsingException("Empty string");
        }
        for (int i = 0; i < expression.length(); ++i) {
            if (i == 0 && expression.charAt(i) == '-') {
                continue;
            }
            if (expression.charAt(i) == '(') {
                ++balance;
            }
            if (expression.charAt(i) == ')') {
                --balance;
            }
            if (balance != 0) {
                continue;
            }
            if (expression.charAt(i) == '+') {
                sum += getInt(expression.substring(lastAct, i));
                lastAct = i + 1;
                isPlus = true;
            }
            if (expression.charAt(i) == '-' && i > 0 &&
                    (expression.charAt(i - 1) >= '0' && expression.charAt(i - 1) <= '9')) {
                sum += getInt(expression.substring(lastAct, i));
                lastAct = i;
                isPlus = true;
            }
        }
        if (isPlus) {
            return sum + getInt(expression.substring(lastAct, expression.length()));
        }
        boolean isMult = false;
        double res = 1.0;
        char lastMult = '*';
        for (int i = 0; i < expression.length(); ++i) {
            if (i == 0 && expression.charAt(i) == '-') {
                continue;
            }
            if (expression.charAt(i) == '(') {
                ++balance;
            }
            if (expression.charAt(i) == ')') {
                --balance;
            }
            if (balance != 0) {
                continue;
            }
            if (expression.charAt(i) == '*') {
                if (lastMult == '*') {
                    res *= getInt(expression.substring(lastAct, i));
                } else {
                    res /= getInt(expression.substring(lastAct, i));
                }
                lastAct = i + 1;
                lastMult = '*';
                isMult = true;
            }
            if (expression.charAt(i) == '/') {
                if (lastMult == '*') {
                    res *= getInt(expression.substring(lastAct, i));
                } else {
                    res /= getInt(expression.substring(lastAct, i));
                }
                lastAct = i + 1;
                lastMult = '/';
                isMult = true;
            }
        }
        if (isMult) {
            if (lastMult == '*') {
                res *= getInt(expression.substring(lastAct, expression.length()));
            } else {
                res /= getInt(expression.substring(lastAct, expression.length()));
            }
            return res;
        }
        if (expression.charAt(0) == '(') {
            return getInt(expression.substring(1, expression.length() - 1));
        }
        if (expression.charAt(0) == '-') {
            return -1.0 * getInt(expression.substring(1, expression.length()));
        }
        int cntPoint = 0;
        for (int i = 0; i < expression.length(); ++i) {
            char ch = expression.charAt(i);
            if (!(Character.isDigit(ch) || ch == '.' || ch == '-')) {
                throw new ParsingException("Incorrect");
            }
            if (ch == '.') {
                ++cntPoint;
            }
        }
        if (cntPoint > 1) {
            throw new ParsingException("Number with more one points");
        }
        return Double.parseDouble(expression);
    }
}
