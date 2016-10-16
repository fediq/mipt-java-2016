package ru.mipt.java2016.homework.g596.stepanova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


@SuppressWarnings("WeakerAccess")
public class MyCalc implements Calculator {
    private static final Set<Character> DOUBLE_DIGITS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.')));


    private static final int LEFT_ASSOC = 0;
    private static final int RIGHT_ASSOC = 1;


    private static final Map<String, int[]> OPERATORS = new HashMap<>();

    static {
        // Map<"token", []{приоритет, ассоциативность}>
        OPERATORS.put("+", new int[] {0, LEFT_ASSOC});
        OPERATORS.put("-", new int[] {0, LEFT_ASSOC});
        OPERATORS.put("*", new int[] {5, LEFT_ASSOC});
        OPERATORS.put("/", new int[] {5, LEFT_ASSOC});
    }


    private static boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }


    private static boolean isAssociative(String token, int type) {
        return OPERATORS.get(token)[1] == type;
    }


    private int cmpPrecedence(String token1, String token2) {
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }


    private String[] infixToRPN(String[] inputTokens) throws ParsingException {
        ArrayList<String> out = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : inputTokens) {
            if (isOperator(token)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC)
                            && cmpPrecedence(token, stack.peek()) <= 0) || (
                            isAssociative(token, RIGHT_ASSOC)
                                    && cmpPrecedence(token, stack.peek()) < 0)) {
                        out.add(stack.pop());
                        continue;
                    }

                    break;
                }

                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                boolean lastElemIsOpeningBracket = false;
                while (!stack.empty() && !stack.peek().equals("(")) {
                    String elem = stack.pop();
                    lastElemIsOpeningBracket = elem.equals("(");
                    out.add(elem);
                }
                if (stack.empty() && !lastElemIsOpeningBracket) {
                    throw new ParsingException("Missing \'(\'");
                }

                stack.pop();
            } else {
                out.add(token);
            }
        }

        while (!stack.empty()) {
            if (stack.peek().equals("(")) {
                throw new ParsingException("Missing \')\'");
            }

            out.add(stack.pop());
        }

        if (out.isEmpty()) {
            throw new ParsingException("Only brackets");
        }

        String[] output = new String[out.size()];
        return out.toArray(output);
    }


    private double rpnToDouble(String[] tokens) {
        Stack<String> stack = new Stack<>();

        for (String token : tokens) {
            if (!isOperator(token)) {
                stack.push(token);
            } else {
                Double d2 = Double.valueOf(stack.pop());
                Double d1 = Double.valueOf(stack.pop());

                Double result = token.compareTo("+") == 0 ?
                        d1 + d2 :
                        token.compareTo("-") == 0 ?
                                d1 - d2 :
                                token.compareTo("*") == 0 ? d1 * d2 : d1 / d2;

                stack.push(String.valueOf(result));
            }
        }

        return Double.valueOf(stack.pop());
    }


    private String[] parser(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }

        expression = expression.trim();

        if (expression.equals("")) {
            throw new ParsingException("Empty expression");
        }

        StringBuilder builder = new StringBuilder(expression);

        if (builder.length() == 1) {
            if (!DOUBLE_DIGITS.contains(builder.charAt(0))) {
                throw new ParsingException("Single character isn't a number");
            }
        }

        // Если первый символ унарный минус,
        // то заменяем его на "0 -"
        if (builder.charAt(0) == '-') {
            builder.insert(0, '0');
        }

        int i = 0;
        int dotsInDouble = 0;
        while (i < builder.length() - 1) {
            char curCh = builder.charAt(i);
            char nextCh = builder.charAt(i + 1);

            // Ищем следующий символ, не учитывая пробела
            int spaces = 0;
            while (nextCh == ' ' || nextCh == '\t' || nextCh == '\n') {
                spaces++;
                nextCh = builder.charAt(i + 1);
                i++;
            }
            // Если между 2 числами нет оператора,
            // то кидаем исключение
            // (проверка только последней и первой цифр в числах,
            // т.к. далее в программе проверяется наличие некорректных символов)
            if (DOUBLE_DIGITS.contains(curCh) && DOUBLE_DIGITS.contains(nextCh) && spaces != 0) {
                throw new ParsingException("Too many numbers");
            }
            // Если есть пробелы, удалим
            if (spaces > 0) {
                builder.delete(i - spaces + 1, i);
                i -= spaces;
            }

            // Если есть унарный минус перед знаком деления или умножения,
            // то приписываем его к числу
            if ((curCh == '/' || curCh == '*') && nextCh == '-') {
                builder.insert(i + 1, ' ');
                i += 3;
                continue;
            }

            // Если знак умножения или деления идет после открывающей скобки,
            // то кидаем исключение
            if (Character.toString(curCh).equals("(") && (Character.toString(nextCh).equals("*")
                    || Character.toString(nextCh).equals("/"))) {
                throw new ParsingException("Wrong operation after opening bracket");
            }

            // Если подряд идут две операции, то кидаем исключение
            if (OPERATORS.containsKey(Character.toString(curCh)) && OPERATORS
                    .containsKey(Character.toString(nextCh))) {
                throw new ParsingException("Too many operators");
            }

            // Считаем кол-во точек в double,
            // если рассматриваются символы числа
            if (curCh == '.') {
                dotsInDouble++;
            }

            // Если текущий символ оператор, последняя цифра числа
            // или одна из скобок, то разделяем пробелом от следующего символа
            if (isOperator(Character.toString(curCh)) || (DOUBLE_DIGITS.contains(curCh)
                    && !DOUBLE_DIGITS.contains(nextCh)) || curCh == '(' || curCh == ')') {
                builder.insert(i + 1, ' ');
                i++;
                dotsInDouble = 0;
            } else if (!DOUBLE_DIGITS.contains(curCh) || dotsInDouble > 1) {
                throw new ParsingException("Wrong symbol");
            }
            // Иначе, если текущий символ не цифра double или
            // если кол-во точек в double превысило одну,
            // то текущий символ некорректный, кидаем исключение

            i++;
        }

        // Разбиение строки на числа, операторы и скобки,
        // предварительно заменив унарный минус перед открывающей скобкой
        // на "( 0 -"
        return builder.toString().replaceAll("\\( -", "\\( 0 -").split(" ");
    }

    @Override
    public double calculate(String expression) throws ParsingException {

        String[] input = parser(expression);
        String[] output = infixToRPN(input);

        return rpnToDouble(output);
    }
}
