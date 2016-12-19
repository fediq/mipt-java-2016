package ru.mipt.java2016.homework.g597.moiseev.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

public class Function {
    static final Random RANDOM = new Random();
    private static final Map<String, Integer> PREDEFINED_FUNCTIONS;

    static {
        PREDEFINED_FUNCTIONS = new HashMap<>();
        PREDEFINED_FUNCTIONS.put("sin", 1);
        PREDEFINED_FUNCTIONS.put("cos", 1);
        PREDEFINED_FUNCTIONS.put("tg", 1);
        PREDEFINED_FUNCTIONS.put("sqrt", 1);
        PREDEFINED_FUNCTIONS.put("pow", 2);
        PREDEFINED_FUNCTIONS.put("abs", 1);
        PREDEFINED_FUNCTIONS.put("sign", 1);
        PREDEFINED_FUNCTIONS.put("log", 1);
        PREDEFINED_FUNCTIONS.put("log2", 1);
        PREDEFINED_FUNCTIONS.put("rnd", 0);
        PREDEFINED_FUNCTIONS.put("max", 2);
        PREDEFINED_FUNCTIONS.put("min", 2);
    }

    private List<String> arguments = new ArrayList<>();
    private String name;
    private String expression;
    private int valency;

    public Function(String name, List<String> arguments, String expression) {
        this.name = name;
        this.arguments = arguments;
        this.expression = expression;
        valency = arguments.size();
    }

    private Function(String name, int valency) {
        this.name = name;
        this.valency = valency;
        this.arguments = new ArrayList<>();
        for (int i = 0; i < valency; i++) {
            arguments.add("x" + Integer.toString(i));
        }
        this.expression = null;
    }

    public static String replaceFunctions(String expression, Map<String, Function> functions,
                                          PowerfulCalculator powerfulCalculator) throws ParsingException {
        StringBuilder result = new StringBuilder();

        Integer[] index = {0};
        for (; index[0] < expression.length(); index[0]++) {
            if (Character.isLetter(expression.charAt(index[0]))) {
                result.append(Function.replaceFunction(expression, index, functions, powerfulCalculator));
            } else {
                result.append(expression.charAt(index[0]));
            }
        }

        return new String(result);
    }

    public static String replaceFunction(String expression, Integer[] index, Map<String, Function> functions,
                                         PowerfulCalculator powerfulCalculator) throws ParsingException {
        StringBuilder name =  new StringBuilder();

        while (Character.isLetterOrDigit(expression.charAt(index[0]))) {
            name.append(expression.charAt(index[0]));
            index[0]++;
        }

        Function function;

        String stringName = new String(name);
        if (PREDEFINED_FUNCTIONS.containsKey(stringName)) {
            function = new Function(stringName, PREDEFINED_FUNCTIONS.get(stringName));
        } else {
            function = functions.get(new String(name));
            if (function == null) {
                throw new ParsingException("Function not found");
            }
        }

        while (Character.isSpaceChar(expression.charAt(index[0]))) {
            index[0]++;
        }

        if (!(expression.charAt(index[0]) == '(')) {
            throw new ParsingException("Illegal expression");
        }

        index[0]++;

        List<String> argumentsValues = new ArrayList<>();
        StringBuilder arg = new StringBuilder();
        int depth = 1;

        for (; index[0] < expression.length(); index[0]++) {
            char c = expression.charAt(index[0]);

            if (expression.charAt(index[0]) == ',' && depth == 1) {
                argumentsValues.add(powerfulCalculator.calculate(new String(arg), null, functions).toString());
                arg = new StringBuilder();
            } else {
                if (c == ')') {
                    depth--;
                    if (depth == 0) {
                        index[0]++;
                        argumentsValues.add(powerfulCalculator.calculate(new String(arg), null, functions).toString());
                        return function.calculate(argumentsValues, functions, powerfulCalculator).toString();
                    } else {
                        arg.append(c);
                    }
                } else {
                    arg.append(c);
                    if (c == '(') {
                        depth++;
                    }
                }
            }
        }

        throw new ParsingException("Illegal expression");
    }

    public static String replaceVariables(String expression, Map<String, Double> variables) throws ParsingException {
        StringBuilder result = new StringBuilder();
        StringBuilder currentLettersBlock = new StringBuilder();
        boolean flag = false;
        for (Character c : expression.toCharArray()) {
            if (flag) {
                if (c.equals('(')) {
                    result.append(currentLettersBlock);
                    currentLettersBlock = new StringBuilder();
                    result.append("(");
                    flag = false;
                } else if (Character.isSpaceChar(c)) {
                    continue;
                } else if (Character.isLetterOrDigit(c) || c == '_') {
                    currentLettersBlock.append(c);
                } else {
                    flag = false;
                    String variable = new String(currentLettersBlock);
                    currentLettersBlock = new StringBuilder();
                    if (variables.containsKey(variable)) {
                        result.append(variables.get(variable));
                    } else {
                        throw new ParsingException("Variable not found");
                    }
                    result.append(c);
                }
            } else {
                if (Character.isLetter(c)) {
                    flag = true;
                    currentLettersBlock = new StringBuilder();
                    currentLettersBlock.append(c);
                } else {
                    result.append(c);
                }
            }
        }

        if (currentLettersBlock.length() > 0) {
            String variable = new String(currentLettersBlock);
            if (variables.containsKey(variable)) {
                result.append(variables.get(variable));
            } else {
                throw new ParsingException("Variable not found");
            }
        }

        return new String(result);
    }

    public Double calculate(List<String> values, Map<String, Function> functions,
                            PowerfulCalculator powerfulCalculator) throws ParsingException {
        if (values.size() != valency) {
            throw new ParsingException("Invalid number of arguments");
        }

        Map<String, Double> calculatedArguments = new HashMap<>();

        for (int i = 0; i < arguments.size(); i++) {
            Double arg = powerfulCalculator.calculate(values.get(i), null, functions);
            calculatedArguments.put(arguments.get(i), arg);
        }

        if (PREDEFINED_FUNCTIONS.containsKey(name)) {
            switch (name) {
                case "sin":
                    return Math.sin(calculatedArguments.get(arguments.get(0)));
                case "cos":
                    return Math.cos(calculatedArguments.get(arguments.get(0)));
                case "tg":
                    return Math.tan(calculatedArguments.get(arguments.get(0)));
                case "sqrt":
                    return Math.sqrt(calculatedArguments.get(arguments.get(0)));
                case "pow":
                    return Math.pow(calculatedArguments.get(arguments.get(0)),
                            calculatedArguments.get(arguments.get(1)));
                case "abs":
                    return Math.abs(calculatedArguments.get(arguments.get(0)));
                case "sign":
                    return Math.signum(calculatedArguments.get(arguments.get(0)));
                case "log":
                    return Math.log(calculatedArguments.get(arguments.get(1))) /
                            Math.log(calculatedArguments.get(arguments.get(0)));
                case "log2":
                    return Math.log(calculatedArguments.get(arguments.get(1))) /
                            Math.log(2);
                case "rnd":
                    synchronized (RANDOM) {
                        return RANDOM.nextDouble();
                    }
                case "max":
                    return Math.max(calculatedArguments.get(arguments.get(0)),
                            calculatedArguments.get(arguments.get(1)));
                case "min":
                    return Math.min(calculatedArguments.get(arguments.get(0)),
                            calculatedArguments.get(arguments.get(1)));
                default:
                    break;
            }
        }

        return powerfulCalculator.calculate(expression, calculatedArguments, functions);
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getExpression() {
        return expression;
    }
}
