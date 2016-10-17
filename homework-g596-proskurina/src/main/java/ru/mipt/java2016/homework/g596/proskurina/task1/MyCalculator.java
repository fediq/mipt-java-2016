package ru.mipt.java2016.homework.g596.proskurina.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayDeque;


import static java.lang.Double.parseDouble;

class MyCalculator implements Calculator {
    private static final String OPERATORS = "+_*/";
    private static final String DIGITS = "0123456789.";

    private Integer bracketsNum = 0;

    @Override
    public double calculate(String expression) throws ParsingException {
        return gettingValue(transformToPolishNotation(stringToParts(expression)));
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
        return new StringTokenizer(startingString, OPERATORS + '(' + ')', true);
    }

    private static final Map<String, Integer> OPERATOR_PRIORITY;

    static {
        Map<String, Integer> operPrior = new HashMap<>();
        operPrior.put("_", 2);
        operPrior.put("/", 1);
        operPrior.put("*", 1);
        operPrior.put("+", 0);
        OPERATOR_PRIORITY = Collections.unmodifiableMap(operPrior);
    }

    private ArrayDeque<String> transformToPolishNotation(StringTokenizer tokenizer) throws ParsingException {

        ArrayDeque<String> polishNotation = new ArrayDeque<>();

        ArrayDeque<String> stackOfOperators = new ArrayDeque<>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (isNumber(token)) {
                polishNotation.push(token);
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
                        polishNotation.push(stackOfOperators.peekFirst());
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
                    polishNotation.push(stackOfOperators.peekFirst());
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
            polishNotation.push(stackOfOperators.pop());
        }
        return polishNotation;
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
            if (DIGITS.indexOf(s.charAt(i)) == -1) {
                return false;
            }

            ++i;

        }
        return !(count > 1);
    }

    private boolean isOperator(String s) {
        return OPERATORS.contains(s);
    }


    private boolean isOpenBracket(String s) {
        return (s.equalsIgnoreCase("("));
    }

    private boolean isCloseBracket(String s) {
        return (s.equalsIgnoreCase(")"));
    }


    private double gettingValue(ArrayDeque<String> polishNotation) throws ParsingException {

        ArrayDeque<Double> stackOfValues = new ArrayDeque<>();
        while (!polishNotation.isEmpty()) {
            if (isNumber(polishNotation.peekLast())) {
                try {
                    stackOfValues.push(parseDouble(polishNotation.pollLast()));
                } catch (NumberFormatException error) {
                    throw new ParsingException("Incorrect input");
                }
                continue;
            }
            if (isUnaryMinus(polishNotation)) {
                polishNotation.pollLast();
                if (stackOfValues.isEmpty()) {
                    throw new ParsingException("Incorrect input");
                }
                stackOfValues.push(-1 * stackOfValues.pop());
                continue;
            }
            if (isOperator(polishNotation.peekLast())) {
                if (stackOfValues.size() >= 2) {
                    stackOfValues.push(calc(stackOfValues.pop(), stackOfValues.pop(), polishNotation.pollLast()));
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

    private boolean isUnaryMinus(ArrayDeque<String> polishNotation) {
        return polishNotation.peekLast().equalsIgnoreCase("_");
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