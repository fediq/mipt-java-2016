package ru.mipt.java2016.homework.g597.dmitrieva.task4;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g597.dmitrieva.task4.Function;

import java.util.*;

/**
 * Created by macbook on 10.10.16.
 */

public class StringCalculator implements Calculator {


    private final Set<Character> SYMBOLS =
            new TreeSet<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'));
    private final static Set<String> OPERATORS = new TreeSet<>(Arrays.asList("+", "-", "*", "/"));
    private static Map<String, Integer> PREDEFINED_FUNCTIONS;

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

    public TreeMap<String, Function> ALLFUNCTIONS = new TreeMap<>();


    @Override
    public double calculate(String expression)
            throws ParsingException {
        if (expression == null) {
            throw new ParsingException("The string doesn't exist");
        }
        return calculateReversedPolish(toReversedPolish(expression));
    }

    public double calculateWithFunctions(String expression, TreeMap<String, Function> functions)
            throws ParsingException {
        if (expression == null) {
            throw new ParsingException("The string doesn't exist");
        }
        ALLFUNCTIONS = functions;
        return calculateReversedPolish(toReversedPolish(expression));
    }

    // Возвращает приоритет операции
    private int getPriority(String operator) throws ParsingException {
        if (operator.equals("(") || operator.equals(")")) {
            return 0;
        }
        if (operator.equals("+") || operator.equals("-")) {
            return 1;
        }
        if (operator.equals("*")|| operator.equals("/")) {
            return 2;
        }
        if (operator.equals("&")) {
            return 3;
        }
        if (ALLFUNCTIONS.containsKey(operator)) {
            return 3;
        }
        throw new ParsingException("Invalid symbol");
    }

    // Возвращает ассоциативность операции
    String getAssociativity(String operator) throws ParsingException {
        if(OPERATORS.contains(operator)) {
            return "left";
        }
        if (ALLFUNCTIONS.keySet().contains(operator)) {
            return "right";
        }
        else {
            throw new ParsingException("Do not know such function or operator");
        }
    }

    // Переводит инфиксную запись в постфиксную.
    private String toReversedPolish(String expression) throws ParsingException {
        boolean isUnaryOperation = true;
        StringBuilder postfixLine =
                new StringBuilder(); // Арифметическое выражение в обратной нотации.
        Stack<String> stack = new Stack<>(); // Стек операторов.
        stack.push("(");
        if (expression.length() == 0) {
            throw new ParsingException("The line is empty");
        }
        for (int i = 0; i < expression.length(); i++) {
            StringBuilder currentSymbol = new StringBuilder();
            currentSymbol.append(expression.charAt(i));

            StringBuilder functionName = new StringBuilder();

            // Если пробельный символ, то игнориурем.
            if ((currentSymbol.charAt(0) == ' ') || currentSymbol.equals("\t")|| currentSymbol.equals("\n")) {
                postfixLine.append(' ');
                continue;
            }
            //Если символ является цифрой или точкой, то добавляем его к выходной строке.
            if (SYMBOLS.contains(currentSymbol.charAt(0))) {
                postfixLine.append(currentSymbol);
                isUnaryOperation = false;
            } else if (currentSymbol.charAt(0) == '(') {
                // Если символ является открывающей скобкой, помещаем его в стек.
                stack.push(currentSymbol.toString());
                postfixLine.append(' ').append(' ');
                isUnaryOperation = true;

                //Если символ является оператором
            } else {
                if (OPERATORS.contains(currentSymbol.toString())) {
                    // Если это унарный минус
                    if (isUnaryOperation) {
                        if (currentSymbol.equals("-")) {
                            while (!stack.empty()) {
                                if (getPriority(currentSymbol.toString()) < getPriority(stack.lastElement())) {
                                    postfixLine.append(' ').append(stack.pop()).append(' ');
                                } else {
                                    break;
                                }
                            }
                            stack.push("&");
                            postfixLine.append(' ').append(' ');
                            isUnaryOperation = false;
                        } else {
                            throw new ParsingException("Invalid expression");
                        }
                    } else { // если это бинарный оператор
                        isUnaryOperation = true;
                        //то пока приоритет этого оператора меньше или равен приоритету оператора,
                        // находящегося на вершине стека, выталкиваем верхний элементы стека в выходную строку.
                        while (!stack.empty()) {
                            if (getPriority(currentSymbol.toString()) <= getPriority(stack.lastElement())) {
                                postfixLine.append(' ').append(stack.pop()).append(' ');
                            } else {
                                break;
                            }
                        }
                        postfixLine.append(' ').append(' ');
                        stack.push(currentSymbol.toString());
                    }
                    // если встретили букву, то хотим считывать имя функции, пока можем
                } else if (Character.isLetter(currentSymbol.charAt(0))) {
                    functionName.append(currentSymbol);
                    ++i;
                    currentSymbol = new StringBuilder();
                    currentSymbol.append(expression.charAt(i));
                    // !!!! здесь еще надо что-нибудь прикрутить на случай, если i больше длины expression
                    while (Character.isLetterOrDigit(currentSymbol.charAt(0)) && i < expression.length()) {
                        functionName.append(currentSymbol);
                        ++i;
                        currentSymbol = new StringBuilder();
                        currentSymbol.append(expression.charAt(i));
                    }
                    if (i >= expression.length()) {
                        throw new ParsingException("Out of range of expression");
                    }
                    // следующим за именем функции символом должна быть открывающая скобка, иначе -- некорретное выражение
                    if (currentSymbol.charAt(0) == '(') {
                        stack.push(currentSymbol.toString());
                        postfixLine.append(' ').append(' ');
                        isUnaryOperation = true;
                    } else {
                        throw new ParsingException("Invalid expression, couldn't find its arguments");
                    }

                    if (!ALLFUNCTIONS.containsKey(functionName.toString())) {
                        throw new ParsingException("Do not have such function");
                    }
                    //пока приоритет этого оператора меньше приоритета оператора,
                    // находящегося на вершине стека, выталкиваем верхний элементы стека в выходную строку.
                    while (!stack.empty()) {
                        if (getPriority(functionName.toString()) < getPriority(stack.lastElement())) {
                            postfixLine.append(' ').append(stack.pop()).append(' ');
                        } else {
                            break;
                        }
                    }
                    postfixLine.append(' ').append(' ');
                    stack.push(functionName.toString());
                } else if (currentSymbol.charAt(0) == ')') {
                    // Если символ является закрывающей скобкой: до тех пор, пока верхним элементом
                    // стека не станет открывающая скобка,выталкиваем элементы из стека в выходную строку.
                    isUnaryOperation = false;
                    while (!stack.empty() && !(stack.lastElement().equals("("))) {
                        postfixLine.append(' ');
                        postfixLine.append(stack.pop()).append(' ');
                    }
                    // Если в стеке не осталось открывающейся скобки
                    // то в выражении не согласованы скобки.
                    if (stack.empty()) {
                        throw new ParsingException("Invalid expression");
                    }
                    stack.pop(); // Убираем из стека соответствующую открывающую скобку.
                    postfixLine.append(' ').append(' ');
                } else {
                    throw new ParsingException("Invalid symbol");
                }
            }
        }
        // Когда входная строка закончилась, выталкиваем все символы из стека в выходную строку.
        while (!(stack.lastElement().equals("(")) && !stack.empty()) {
            postfixLine.append(' ');
            postfixLine.append(stack.lastElement()).append(' ');
            stack.pop();
        }
        postfixLine.append(' ');
        // Если в конце стек остался пуст, то в выражении не согласованы скобки
        // (ибо в начале мы в стек пихали одну открывающую скобку, которая должна была остаться)
        if (stack.empty()) {
            throw new ParsingException("Invalid expression");
        }
        stack.pop(); // Удалим скобку, добавленную в самом начале, если все хорошо.
        return postfixLine.toString();
    }

    //Считает значение элементарного выражения.
    private Double countAtomicOperation(Character operation, Double a, Double b)
            throws ParsingException {
        switch (operation) {
            case '+':
                return a + b;
            case '-':
                return b - a;
            case '*':
                return a * b;
            case '/':
                return b / a;
            default:
                throw new ParsingException("Invalid symbol");
        }
    }

    // Вычисление выражения в постфиксной записи.
    private double calculateReversedPolish(String postfixLine) throws ParsingException {
        Stack<Double> stack = new Stack<>(); // Стек операторов.
        List<String> tokens = Arrays.asList(postfixLine.split(" "));
        //StringBuilder oneNumber = new StringBuilder(""); // Для считывания числа из постфиксной строки.
        //for (int i = 0; i < tokens;)

        for (int i = 0; i < tokens.size(); i++) {
            //String currentSymbol =  postfixLine.charAt(i);
            //StringBuilder currentSymbol = new StringBuilder()
            // Здесь мы, собственно, парсим входную строку
            String currentString = tokens.get(i);
            if (currentString.isEmpty()) {
                continue;
            }
            if (SYMBOLS.contains(currentString.charAt(0))) {
                stack.push(Double.parseDouble(currentString));
                continue;
            }
            if (PREDEFINED_FUNCTIONS.containsKey(currentString)) {
                // тут куча if'ов и вызов математический функций из библиотеки
                continue;
            }

            if (ALLFUNCTIONS.containsKey(currentString)) {
                Function currentFunction = ALLFUNCTIONS.get(currentString);
                String expressionForFunction = currentFunction.getExpression();
                List<String> argumentOfFunction = currentFunction.getArguments();
                int amountOfArguments = argumentOfFunction.size();
                // проходимся по списку аргументов и делаем replace all, вместо параметров подставляем чиселски со стека
                // потом делаем calculate от полученного expressiobForFunction
                for (int j = amountOfArguments - 1; j >= 0; j--) {
                    String argument = argumentOfFunction.get(j);
                    Double valueOfArgument = stack.pop();
                    expressionForFunction = expressionForFunction.replaceAll(argument, valueOfArgument.toString());
                }
                Double resultForFunction = calculate(expressionForFunction);
                stack.push(resultForFunction);
            }

            if (currentString.charAt(0) == '&') {
                Double a;
                a = stack.pop();
                stack.push(-1 * a);
            }
            if (OPERATORS.contains(currentString)) {
                Double a;
                Double b;
                a = stack.pop();
                b = stack.pop();
                stack.push(countAtomicOperation(currentString.charAt(0), a, b));
            }

            /*
            if (SYMBOLS.contains(currentSymbol)) {
                oneNumber.append(currentSymbol);
            } else {
                if (i > 0 && currentSymbol.charAt(0) == ' ' && SYMBOLS.contains(postfixLine.charAt(i - 1))) {
                    try {
                        stack.push(Double.parseDouble(oneNumber.toString()));
                    } catch (NumberFormatException e) {
                        throw new ParsingException("Bad number");
                    }
                    oneNumber.delete(0, oneNumber.length());
                } else {
                    if (currentSymbol.charAt(0) == '&') {
                        Double a;
                        a = stack.pop();
                        stack.push(-1 * a);
                    }
                    if (OPERATORS.contains(currentSymbol)) {
                        Double a;
                        Double b;
                        a = stack.pop();
                        b = stack.pop();
                        stack.push(countAtomicOperation(currentSymbol.charAt(0), a, b));
                    }
                }
            } */
        }
        // В конце в стеке должен был остаться один элемент, который является ответом.
        if (stack.size() == 1) {
            return stack.lastElement();
        } else {
            // Если нет, то случилось что-то плохое
            throw new ParsingException("Invalid expression");
        }
    }
}