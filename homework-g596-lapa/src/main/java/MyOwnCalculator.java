import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by user on 16.10.2016.
 */
public class MyOwnCalculator implements Calculator {
    @Override
    public final double calculate(final String expression) throws ParsingException {
        return stackCalculator(getPostfixExpression(fragmentation(expression)));
    }

    private double stackCalculator(LinkedList<Element> expression) throws ParsingException {
        Stack<Double> numbers = new Stack<Double>();
        while (!expression.isEmpty()) {
            Element element = expression.removeFirst();
            if (element instanceof Operator) {
                Operator operator = (Operator) element;
                if (operator.getType() != '#') {
                    if (!numbers.isEmpty()) {
                        double num1 = numbers.pop();
                        if (!numbers.isEmpty()) {
                            double num2 = numbers.pop();
                            double result = getResultOfOperation(num2, num1, operator);
                            numbers.push(result);
                        } else {
                            throw new ParsingException("Invalid expression");
                        }

                    } else {
                        throw new ParsingException("Invalid expression");
                    }
                } else {
                    if (!numbers.isEmpty()) {
                        double number = -1 * numbers.pop();
                        numbers.push(number);
                    } else {
                        throw new ParsingException("Invalid expression");
                    }

                }
            } else {
                numbers.push(((Number) element).getValue());
            }
        }
        if (numbers.size() != 1) {
            throw new ParsingException("Invalid expression");
        }
        return numbers.peek();
    }

    private LinkedList<Element> getPostfixExpression(LinkedList<Element> expression) {
        LinkedList<Element> postfixExpression = new LinkedList<>();
        Stack<Element> elements = new Stack<>();
        while (!expression.isEmpty()) {
            Element element = expression.removeFirst();
            if (element instanceof Operator) {
                Operator operator = (Operator) element;
                while (!elements.isEmpty() && elements.peek() instanceof Operator
                    && ((Operator) elements.peek()).getPriority() >= operator.getPriority()) {
                    postfixExpression.add(elements.pop());
                }
                elements.push(operator);
            } else {
                if (element instanceof Number) {
                    postfixExpression.add(element);
                } else {
                    if (element instanceof Bracket) {
                        Bracket bracket = (Bracket) element;
                        if (!bracket.opening()) {
                            while (!(elements.peek() instanceof Bracket)) {
                                postfixExpression.add(elements.pop());
                            }
                            elements.pop();
                        } else {
                            elements.push(bracket);
                        }
                    }
                }
            }
        }
        while (!elements.isEmpty()) {
            postfixExpression.add(elements.pop());
        }
        return postfixExpression;
    }

    private LinkedList<Element> fragmentation(String expression) throws ParsingException {
        LinkedList<Element> fragmentedExpression = new LinkedList<>();
        int balanceOfBracket = 0;
        if (expression == null || expression.length() == 0) {
            throw new ParsingException("Invalid expression");
        }
        char character;
        for (int i = 0; i != expression.length(); ++i) {
            character = expression.charAt(i);
            if (Character.isWhitespace(character)) {
                continue;
            } else {
                if (Character.isDigit(character)) {
                    StringBuilder number = new StringBuilder();
                    number.append(character);
                    boolean dot = false;
                    while (i != expression.length() - 1) {
                        char next = expression.charAt(i + 1);
                        if (next == '.' && !dot) {
                            dot = true;
                        } else {
                            if (!Character.isDigit(next)) {
                                break;
                            }
                        }
                        ++i;
                        number.append(next);
                    }
                    Number num = new Number(Double.parseDouble(number.toString()));
                    fragmentedExpression.add(num);
                    number.delete(0, number.length() - 1);
                } else {
                    if (character == '+' || character == '-' || character == '*' || character == '/') {
                        if (i == 0) {
                            if (character == '-') {
                                character = '#';
                            }
                        } else {
                            Element prev = fragmentedExpression.getLast();
                            if (character == '-') {
                                if ((prev instanceof Bracket) && ((Bracket) prev).opening()) {
                                    character = '#';
                                } else {
                                    if ((prev instanceof Operator)
                                            && (((Operator) prev).getType() == '/'
                                            || ((Operator) prev).getType() == '*')) {
                                        character = '#';
                                    }
                                }
                            }
                            if ((prev instanceof Operator) && !(character == '#')) {
                                throw new ParsingException("Invalid expression");
                            }
                        }
                        Operator op = new Operator(character);
                        fragmentedExpression.add(op);
                    } else {
                        if (character == '(') {
                            balanceOfBracket++;
                            Bracket bracket = new Bracket(character);
                            fragmentedExpression.add(bracket);
                        } else {
                            if (character == ')') {
                                if (i > 0) {
                                    Element prev = fragmentedExpression.getLast();
                                    if (prev instanceof Operator && ((Operator) prev).getType() == '(') {
                                        throw new ParsingException("Invalid expression");
                                    }
                                }
                                Bracket bracket = new Bracket(character);
                                fragmentedExpression.add(bracket);
                                --balanceOfBracket;
                                if (balanceOfBracket < 0) {
                                    throw new ParsingException("Invalid expression");
                                }
                            } else {
                                throw new ParsingException("Invalid expression");
                            }
                        }
                    }
                }
            }

        }

        if (balanceOfBracket > 0) {
            throw new ParsingException("Invalid expression");
        }
        // во fragmentedExpression должно быть хотя бы одно число
        for (Element element : fragmentedExpression) {
            if (element instanceof Number) {
                return fragmentedExpression;
            }
        }
        // если в выражении нет чисел
        throw new ParsingException("Invalid expression");

    }

    private double getResultOfOperation(double a, double b, Operator op) throws ParsingException {
        double result;
        switch (op.getType()) {
            case '+':
                result = a + b;
                break;
            case '-':
                result = a - b;
                break;
            case '*':
                result = a * b;
                break;
            case '/':
                result = a / b;
                break;
            default:
                throw new ParsingException("Invalid symbol");
        }
        return result;
    }

    private abstract class Element {
    }

    private class Number extends Element {

        Number(double val) {
            value = val;
        }

        public double getValue() {
            return value;
        }

        private double value;

    }

    private class Bracket extends Element {

        Bracket(char br) {
            type = br != ')';
        }

        public boolean opening() {
            return type;
        }

        private boolean type;

    }

    private class Operator extends Element {

        Operator(char op) {
            if (op == '+') {
                priority = 0;
            }
            if (op == '-') {
                priority = 0;
            }
            if (op == '*') {
                priority = 1;
            }
            if (op == '/') {
                priority = 1;
            }
            if (op == '#') {
                priority = 2;
            }
            type = op;
        }

        public int getPriority() {
            return priority;
        }

        public char getType() {
            return type;
        }

        private int priority;

        private char type;
    }

}