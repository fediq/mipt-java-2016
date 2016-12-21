package ru.mipt.java2016.homework.g597.kirilenko.task4;


import javafx.util.Pair;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static java.lang.Math.*;


public class MyCalculator implements Calculator {
    private Stack<Double> numbers;
    private Stack<Character> operations;

    private Map<String, String> variablesExpressions = new HashMap();
    private Map<String, Pair<ArrayList<String>, String>> functions = new HashMap();

    private void loadDataFromDao(String username) {
        Pair<Map<String, String>, Map<String, javafx.util.Pair<ArrayList<String>, String>>> res = BillingDao.getInstance().loadData(username);
        variablesExpressions = res.getKey();
        functions = res.getValue();
    }

    private double calculateStandardExpression(String functionName, ArrayList<Double> arguments)
        throws ParsingException {
        if (functionName.equals("sin")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return sin(arguments.get(0));
        }
        if (functionName.equals("cos")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return cos(arguments.get(0));
        }
        if (functionName.equals("tg")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return tan(arguments.get(0));
        }
        if (functionName.equals("sqrt")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return sqrt(arguments.get(0));
        }
        if (functionName.equals("abs")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return abs(arguments.get(0));
        }
        if (functionName.equals("max")) {
            if (arguments.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return max(arguments.get(0), arguments.get(1));
        }
        if (functionName.equals("min")) {
            if (arguments.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return min(arguments.get(0), arguments.get(1));
        }
        if (functionName.equals("pow")) {
            if (arguments.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return pow(arguments.get(0), arguments.get(1));
        }
        if (functionName.equals("log")) {
            if (arguments.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return log(arguments.get(0)) / log(arguments.get(1));
        }
        if (functionName.equals("log2")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return log(arguments.get(0)) / log(2);
        }
        if (functionName.equals("rnd")) {
            if (arguments.size() != 0) {
                throw new ParsingException("Incorrect expression");
            }
            return random();
        }
        if (functionName.equals("sign")) {
            if (arguments.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return signum(arguments.get(0));
        }
        throw new ParsingException("Not standard expression.");
    }

    private boolean isStandardFunction(String functionName) {
        return functionName.equals("sin") ||
                functionName.equals("cos") ||
                functionName.equals("tg") ||
                functionName.equals("sqrt") ||
                functionName.equals("pow") ||
                functionName.equals("abs") ||
                functionName.equals("sign") ||
                functionName.equals("log") ||
                functionName.equals("log2") ||
                functionName.equals("rnd") ||
                functionName.equals("max") ||
                functionName.equals("min");

    }

    public String getVariableExpression(String username, String variable) {
        loadDataFromDao(String username)
        if (!variablesExpressions.keySet().contains(variable)) {
            return null;
        }
        return variablesExpressions.get(variable);
    }

    public void setVariableExpression(String username, String variable, String expression) {
        loadDataFromDao(String username)
        variablesExpressions.put(variable, expression);
    }

    public void deleteVariable(String username, String variable) {
        loadDataFromDao(String username)
        variablesExpressions.remove(variable);
    }

    public ArrayList<String> getAllVariables(String username) {
        loadDataFromDao(String username)
        return new ArrayList<>(variablesExpressions.keySet());
    }

    public Pair<ArrayList<String>, String> getFunctionInfo(String username, String functionName) {
        loadDataFromDao(String username)
        return functions.get(functionName);
    }

    public boolean setFunction(String username, String functionName, ArrayList<String> arguments, String expression) {
        loadDataFromDao(String username)
        if (isStandardFunction(functionName)) {
            return false;
        }
        functions.put(functionName, new Pair<>(arguments, expression));
        return true;
    }

    public boolean deleteFunction(String username, String functionName) {
        loadDataFromDao(String username)
        if (isStandardFunction(functionName)) {
            return false;
        }
        functions.remove(functionName);
        return true;
    }

    public ArrayList<String> getAllFunctions(String username) {
        loadDataFromDao(String username)
        return new ArrayList<>(functions.keySet());
    }

    private boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    private boolean isLatinSymbol(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private boolean isUnderscore(char c) {
        return c == '_';
    }

    private boolean isOpeningBracket(char c) {
        return c == '(';
    }


    private Pair<Integer, Integer> getValueLexem(String expression, Integer startIndex) {
        Integer firstIndex = startIndex;
        while (firstIndex < expression.length() && !isUnderscore(expression.charAt(firstIndex)) &&
                !isLatinSymbol(expression.charAt(firstIndex))) {
            firstIndex += 1;
        }
        if (firstIndex >= expression.length()) {
            return null;
        }
        Integer secondIndex = firstIndex;
        while (secondIndex < expression.length() && (isUnderscore(expression.charAt(secondIndex)) ||
                isLatinSymbol(expression.charAt(secondIndex)) || isDigit(expression.charAt(secondIndex)))) {
            secondIndex += 1;
        }
        if (secondIndex == expression.length() || expression.charAt(secondIndex) != '(') {
            return new Pair<>(firstIndex, secondIndex);
        } else {
            return getValueLexem(expression, secondIndex + 1);
        }
    }

    private Pair<Pair<String, Pair<Integer, Integer>>, ArrayList<String>> getFunctionLexem(String expression) {
        Integer firstIndex = 0;
        while (firstIndex < expression.length() && !isUnderscore(expression.charAt(firstIndex)) &&
                !isLatinSymbol(expression.charAt(firstIndex))) {
            firstIndex += 1;
        }
        if (firstIndex >= expression.length()) {
            return null;
        }
        Integer secondIndex = firstIndex;
        while (secondIndex < expression.length() && (isUnderscore(expression.charAt(secondIndex)) ||
                isLatinSymbol(expression.charAt(secondIndex)) || isDigit(expression.charAt(secondIndex)))) {
            secondIndex += 1;
        }
        String name = expression.substring(firstIndex, secondIndex);
        Pair<ArrayList<String>, Integer> arg = getArguments(expression, secondIndex);
        ArrayList<String> arguments = arg.getKey();
        Integer lastIndex = arg.getValue();
        Pair<Integer, Integer> indices = new Pair<>(firstIndex, lastIndex);
        return new Pair<>(new Pair<>(name, indices), arguments);
    }

    private Pair<ArrayList<String>, Integer> getArguments(String expression, Integer startIndex) {
        ArrayList<String> arguments = new ArrayList<>();
        Integer lastIndex = 0;
        Integer balance = 1;
        Integer currentIndex = startIndex + 1;
        String currentArgument = "";
        while (balance != 0) { //выражение уже проверено на корректность -> exception не выбрасываем
            if (expression.charAt(currentIndex) == '(') {
                balance += 1;
                currentArgument += expression.charAt(currentIndex);
                currentIndex++;
            } else if (expression.charAt(currentIndex) == ')') {
                balance -= 1;
                currentArgument += expression.charAt(currentIndex);
                currentIndex++;
            } else if (balance == 1 && expression.charAt(currentIndex) == ',') {
                arguments.add(currentArgument);
                currentArgument = "";
                currentIndex++;
            } else {
                currentArgument += expression.charAt(currentIndex);
                currentIndex++;
            }
        }
        arguments.add(currentArgument.substring(0, currentArgument.length() - 1));
        return new Pair<>(arguments, currentIndex);
    }

    private Pair<Integer, Integer> findArgument(String expression, String argument) {
        int lastIndex = expression.indexOf(argument, 0);
        if (lastIndex == -1) {
            return null;
        }
        return new Pair<>(lastIndex, lastIndex + argument.length());
    }

    private Double evaluateFunction(String expression, ArrayList<String> argumentsList,
                                    ArrayList<Double> arguments) throws ParsingException {
        for (int i = 0; i < argumentsList.size(); i++) {
            String argument = argumentsList.get(i);
            Pair<Integer, Integer> argCoord = findArgument(expression, argument);
            while (argCoord != null) {
                expression = expression.substring(0, argCoord.getKey()) + arguments.get(i).toString() +
                        expression.substring(argCoord.getValue(), expression.length());
                argCoord = findArgument(expression, argument);
            }
        }
        try {
            return evaluateExpression(expression);
        } catch (ParsingException e) {
            throw e;
        }
    }



    public double evaluateExpression(String username, String expression) throws ParsingException {
        loadDataFromDao(String username)
        expression = deleteSpaces(expression);


        try {
            Pair<Integer, Integer> valueLexem = getValueLexem(expression, 0);
            while (valueLexem != null) {
                Integer start = valueLexem.getKey();
                Integer end = valueLexem.getValue();
                String valueName = expression.substring(start, end);
                String valExpr = getVariableExpression(valueName);
                Double result = evaluateExpression(valExpr);
                expression = expression.substring(0, start) + result.toString() +
                        expression.substring(end, expression.length());
                valueLexem = getValueLexem(expression, 0);
            }
            Pair<Pair<String, Pair<Integer, Integer>>, ArrayList<String>> functionLexem =
                    getFunctionLexem(expression);
            while (functionLexem != null) {
                String functionName = functionLexem.getKey().getKey();
                Integer start = functionLexem.getKey().getValue().getKey();
                Integer end = functionLexem.getKey().getValue().getValue();
                ArrayList<String> arguments = functionLexem.getValue();
                ArrayList<Double> calculatedArguments = new ArrayList<>();
                for (int i = 0; i < arguments.size(); i++) {
                    calculatedArguments.add(evaluateExpression(arguments.get(i)));
                }
                Double result = Double.NaN;
                if (isStandardFunction(functionName)) {
                    result = calculateStandardExpression(functionName, calculatedArguments);
                } else {
                    Pair<ArrayList<String>, String> func = getFunctionInfo(functionName);
                    result = evaluateFunction(func.getValue(), func.getKey(), calculatedArguments);
                }
                expression = expression.substring(0, start) + result.toString() +
                        expression.substring(end, expression.length());
                functionLexem = getFunctionLexem(expression);
            }
            return calculate(expression);
        } catch (ParsingException p) {
            throw p;
        }
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        numbers = new Stack<>();
        operations = new Stack<>();
        try {
            if (expression == null) {
                throw new ParsingException("Incorrect expression");
            }
            if (!checkForConsequentNumbers(expression)) {
                throw new ParsingException("Incorrect expression");
            }
            if (!checkIncorrectExpression(expression)) {
                throw new ParsingException("Incorrect expression");
            }
            return toRPH(expression);
        } finally {
            numbers = null;
            operations = null;
        }

    }

    private boolean checkForConsequentNumbers(String expres) {
        //между любыми двумя числами должен стоять оператор (тесты вида 1 2, 1(2)
        boolean opBetween = true;
        boolean notString = true;
        for (int i = 0; i < expres.length(); i++) {
            char c = expres.charAt(i);
            if (c == '*' || c == '/' || c == '+' || c == '-') {
                opBetween = true;
                notString = true;
            } else if (Character.isDigit(c) || c == '.') {
                if (notString && !opBetween) {
                    return false;
                }
                notString = false;
                opBetween = false;
            } else {
                notString = true;
            }
        }
        return true;
    }

    private int priority(char c) {
        if (c == '+' || c == '-') {
            return 1;
        } else if (c == '*' || c == '/') {
            return 2;
        } else if (c == 'M') {
            return 3;
        } else {
            return -1;
        }
    }

    private void calculationOperator(char c) {
        if (c == 'M') {
            double a = numbers.pop();
            numbers.push(-a);
            return;
        }
        double a = numbers.pop();
        double b = numbers.pop();
        if (c == '+') {
            numbers.push(a + b);
        } else if (c == '-') {
            numbers.push(b - a);
        } else if (c == '*') {
            numbers.push(b * a);
        } else if (c == '/') {
            numbers.push(b / a);
        }
    }

    private double toRPH(String expression) throws ParsingException {
        boolean isUnary = true; //перед унарным минусом стоит либо операция, либо (
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c == '(') {
                isUnary = true;
                operations.push(c);
            } else if (c == ')') {
                //вычиляем значение в скобках
                while (operations.peek() != '(') {
                    calculationOperator(operations.peek());
                    operations.pop();
                }
                isUnary = false;
                //после ')' не может быть унарного минуса
                operations.pop();

            } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (isUnary && c == '-') {
                    c = 'M';
                }
                //сначала выполняем операции с большим приоритетом
                while (!operations.isEmpty() && ((c != 'M' &&
                        priority(operations.peek()) >= priority(c)) || (c == 'M'
                        && priority(operations.peek()) > priority(c)))) {
                    calculationOperator(operations.peek());
                    operations.pop();
                }
                operations.push(c);
                isUnary = true;
            } else {
                String operand = "";
                //находим десятичное число и добавляем его в вектор чисел
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i))
                                || expression.charAt(i) == '.')) {
                    operand += expression.charAt(i);
                    i++;
                }
                i--;
                numbers.push(Double.parseDouble(operand));
                isUnary = false;
                //после числа не может стоять унарый минус
            }
        }
        //выполняем оставшиеся операции над получившимися числами из numbers
        while (!operations.isEmpty()) {
            calculationOperator(operations.peek());
            operations.pop();
        }
        if (numbers.size() != 1) {
            throw new ParsingException("Invalid expression.");
        }
        return numbers.peek();
    }

    private String deleteSpaces(String expression) {
        String expres = "";
        for (int i = 0; i < expression.length(); ++i) {
            if (expression.charAt(i) != ' ' && expression.charAt(i) != '\t' && expression.charAt(i) != '\n') {
                expres += Character.toString(expression.charAt(i));
            }
        }
        return expres;
    }

    private boolean checkIncorrectExpression(String expres) {
        int bracketResult = 0;
        //выражение непусто
        //на первом месте не стоят бинарные операции
        //на последнем месте либо цифра, либо ')'
        if (expres.length() == 0 || expres.charAt(0) == '*'
                || expres.charAt(0) == '/' || expres.charAt(0) == '+'
                || !(Character.isDigit(expres.charAt(expres.length() - 1))
                || expres.charAt(expres.length() - 1) == ')')) {
            return false;
        }
        for (int i = 0; i < expres.length(); ++i) {
            if (expres.charAt(i) == '(') {
                bracketResult += 1;
            }
            if (expres.charAt(i) == ')') {
                bracketResult -= 1;
            }
            //после оператора не стоит бинарный оператор(то есть не *, /, +)
            if (expres.charAt(i) == '-' || expres.charAt(i) == '+'
                    || expres.charAt(i) == '/' || expres.charAt(i) == '*') {
                if (i + 1 >= expres.length() || expres.charAt(i + 1) == '+'
                        || expres.charAt(i + 1) == '/' || expres.charAt(i + 1) == '*') {
                    return false;
                }
            }
            //проверка на некорректные символы
            if (!(Character.isDigit(expres.charAt(i)) || expres.charAt(i) == '.'
                    || expres.charAt(i) == '(' || expres.charAt(i) == ')'
                    || expres.charAt(i) == '+' || expres.charAt(i) == '-' ||
                    expres.charAt(i) == '*' || expres.charAt(i) == '/')) {
                return false;
            }
            //проверка на неотрицательный скобочный итог
            if (bracketResult < 0) {
                return false;
            }
            //*, /, + не являются бинарными операторами, то есть они не могут стоять после '('
            //также пустые скобки считаются некорретным выражением
            if (expres.charAt(i) == '(') {
                if (i + 1 >= expres.length() || (expres.charAt(i + 1) == '+'
                        || expres.charAt(i + 1) == '*' ||
                        expres.charAt(i + 1) == '/' || expres.charAt(i + 1) == ')')) {
                    return false;
                }
            }
        }
        if (bracketResult != 0) {
            return false;
        }
        //проверка на корректность десятичного выражения(в каждом числе не больше одной '.')
        int dot = 0;
        int i = 0;
        while (i < expres.length() && dot < 2) {
            if (expres.charAt(i) == '+' || expres.charAt(i) == '-'
                    || expres.charAt(i) == '/' || expres.charAt(i) == '*') {
                dot  = 0;
            }
            if (expres.charAt(i) == '.') {
                dot += 1;
            }
            i++;
        }
        if (dot >= 2) {
            return false;
        }
        return true;

    }
}
