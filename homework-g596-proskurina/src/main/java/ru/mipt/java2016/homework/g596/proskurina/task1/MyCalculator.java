package ru.mipt.java2016.homework.g596.proskurina.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

import static java.lang.Double.parseDouble;

class MyCalculator implements Calculator {
    private String operators = "+_*/";
    private String digits = "0123456789.";

    @Override
    public double calculate(String expression) throws ParsingException {
        return gettingValue(transformToPolishNatation(stringToParts(expression)));
    }

    private StringTokenizer stringToParts(String startingString) throws ParsingException {
        if (startingString == null) {
            throw new ParsingException("Null");
        }
        startingString = startingString.replaceAll("[\\s]", "");
        if (startingString.isEmpty()) {
            throw new ParsingException("Incorrect input");
        }
        if (startingString.charAt(0) == '-') {
            startingString = '0' + startingString;
        }
        startingString = startingString.replaceAll("\\(-", "(_").replaceAll("/-", "/_")
                .replaceAll("-", "+_");
        return new StringTokenizer(startingString, operators + '(' + ')', true);
    }

    private static final Map<String, Integer> OPERATOR_PRIORITY;

    static {
        Map<String, Integer> operPrior = new HashMap<String, Integer>();
        operPrior.put("_", 2);
        operPrior.put("/", 1);
        operPrior.put("*", 1);
        operPrior.put("+", 0);
        OPERATOR_PRIORITY = Collections.unmodifiableMap(operPrior);
    }

    private Integer bracketsNum = 0;

    private ArrayDeque<String> transformToPolishNatation(StringTokenizer tokenizer) throws ParsingException {

        ArrayDeque<String> polishNatation = new ArrayDeque<String>();

        ArrayDeque<String> stackOfOperators = new ArrayDeque<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (isNumber(token)) {
                polishNatation.push(token);
                continue;
            }

            if (isOperator(token)) {
                if (stackOfOperators.size() > 4 + bracketsNum) {
                    throw new ParsingException("Incorrect input");
                }
                if (stackOfOperators.isEmpty()) {
                    stackOfOperators.push(token);
                    continue;
                }

                String firstOperator = stackOfOperators.peekFirst();

                if (!isOperator(firstOperator)) {
                    stackOfOperators.push(token);
                    continue;
                }
                if (OPERATOR_PRIORITY.get(firstOperator) < OPERATOR_PRIORITY.get(token)) {
                    stackOfOperators.push(token);
                } else {
                    while (!stackOfOperators.isEmpty()
                            && OPERATOR_PRIORITY.get(stackOfOperators.peekFirst()) >= OPERATOR_PRIORITY.get(token)) {
                        polishNatation.push(stackOfOperators.peekFirst());
                        stackOfOperators.pop();
                    }
                    stackOfOperators.push(token);
                }

                continue;
            }
            if (isOpenBracket(token)) {
                ++bracketsNum;
                stackOfOperators.push(token);
                continue;
            }

            if (isCloseBracket(token)) {
                --bracketsNum;
                if (bracketsNum < 0) {
                    throw new ParsingException("Incorrect input");
                }
                while (!isOpenBracket(stackOfOperators.peekFirst())) {
                    polishNatation.push(stackOfOperators.peekFirst());
                    stackOfOperators.pop();
                }
                stackOfOperators.pop();
                continue;
            }
            throw new ParsingException("Bad token");
        }
        while (!stackOfOperators.isEmpty()) {
            if (stackOfOperators.peekFirst().equalsIgnoreCase("(")) {
                throw new ParsingException("Incorrect input");
            }
            polishNatation.push(stackOfOperators.pop());
        }
        return polishNatation;
    }


    private boolean isNumber(String s) {
        if (s.isEmpty()) {
            return false;
        }
        int i = 0;
        int count = 0;
        while (i < s.length()) {
            if (s.charAt(i) == '.') {
                ++count;
            }
            if (!digits.contains(s.substring(i, i + 1))) {
                return false;
            }

            ++i;

        }
        return !(count > 1);
    }

    private boolean isOperator(String s) {
        return operators.contains(s);
    }


    private boolean isOpenBracket(String s) {
        return (s.equalsIgnoreCase("("));
    }

    private boolean isCloseBracket(String s) {
        return (s.equalsIgnoreCase(")"));
    }


    private double gettingValue(ArrayDeque<String> polishNatation) throws ParsingException {

        ArrayDeque<Double> stackOfValues = new ArrayDeque<Double>();
        while (!polishNatation.isEmpty()) {
            if (isNumber(polishNatation.peekLast())) {
                try {
                    stackOfValues.push(parseDouble(polishNatation.pollLast()));
                } catch (NumberFormatException error) {
                    throw new ParsingException("Incorrect input");
                }
                continue;
            }
            if (polishNatation.peekLast().equalsIgnoreCase("_")) {
                polishNatation.pollLast();
                if (stackOfValues.isEmpty()) {
                    throw new ParsingException("Incorrect input");
                }
                stackOfValues.push(-1 * stackOfValues.pop());
                continue;
            }
            if (isOperator(polishNatation.peekLast())) {
                if (stackOfValues.size() >= 2) {
                    stackOfValues.push(calc(stackOfValues.pop(), stackOfValues.pop(), polishNatation.pollLast()));
                } else {
                    throw new ParsingException("Incorrect input");
                }
            }
        }
        if (stackOfValues.size() != 1) {
            throw new ParsingException("Incorrect input");
        }
        return stackOfValues.pollLast();
    }


    private Double calc(Double a, Double b, String operat) {
        if (operat.equalsIgnoreCase("+")) {
            return a + b;
        }
        if (operat.equalsIgnoreCase("*")) {
            return a * b;
        }
        if (operat.equalsIgnoreCase("/")) {
            return b / a;
        }
        return 0.0;
    }
}