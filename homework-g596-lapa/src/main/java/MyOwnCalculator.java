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
        LinkedList<Element> postfixExpression = new LinkedList<Element>();
        Stack<Element> elements = new Stack<Element>();
        while (!expression.isEmpty()) {
            Element element = expression.removeFirst();
            if (element instanceof Operator) {
                Operator operator = (Operator) element;
                while (!elements.isEmpty() && elements.peek() instanceof Operator) {
                    Operator op = (Operator) elements.peek();
                    if (op.getPriority() >= operator.getPriority()) {
                        postfixExpression.add(elements.pop());
                    }
                }
                elements.push(operator);
            } else {
                if (element instanceof Number) {
                    Number number = (Number) element;
                    postfixExpression.add(number);
                } else { 
                    if (element instanceof Bracket) {
                        Bracket bracket = (Bracket) element;
                        if (!bracket.opening()) {
                            while (!(elements.peek() instanceof Bracket)) {
                                Bracket br = (Bracket) elements.pop();
                                postfixExpression.add(br);
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
        LinkedList<Element> fragmentedException = new LinkedList<>();
        boolean dot;
        int balanceOfBracket = 0;
        if (expression == null || expression.length() == 0) {
            throw new ParsingException("Invalid expression");
        }

        char c;
        for (int i = 0; i != expression.length(); ++i) {
            c = expression.charAt(i);
            if (Character.isWhitespace(c)) continue;
            else {
                if (Character.isDigit(c)) {
                    StringBuilder number = new StringBuilder();
                    number.append(c);
                    dot = false;
                    char next;
                    while (i < expression.length() - 1) {
                        next = expression.charAt(i + 1);
                        if (next != '.' || dot) {
                            if (!Character.isDigit(next)) {
                                break;
                            }
                        } else {
                            dot = true;
                        }
                        ++i;
                        number.append(next);
                    }
                    Number num = new Number(Double.parseDouble(number.toString()));
                    fragmentedException.add(num);
                } else {
                    if (c == '+' || c == '-' || c == '*' || c == '/') {
                        if (i == 0) {
                            if (c == '-')
                                c = '#';
                        } else {
                            Element prev = fragmentedException.getLast();
                            if (c == '-') {
                                if ((prev instanceof Bracket) && ((Bracket) prev).opening()) {
                                    c = '#';
                                } else {
                                    if ((prev instanceof Operator) &&
                                            (((Operator) prev).getType() == '/' || ((Operator) prev).getType() == '*')) {
                                        c = '#';
                                    }
                                }

                            }
                            if ((prev instanceof Operator) && !(c == '#')) {
                                throw new ParsingException("Invalid expression");
                            }
                        }
                    } else {
                        if (c == '(') {
                            balanceOfBracket++;
                            Bracket bracket = new Bracket(c);
                            fragmentedException.add(bracket);
                        } else {
                            if (c == ')') {
                                if (i > 0) {
                                    Element prev = fragmentedException.getLast();
                                    if (prev instanceof Operator && ((Operator) prev).getType() == '(') {
                                        throw new ParsingException("Invalid expression");
                                    }
                                }
                                Bracket bracket = new Bracket(c);
                                fragmentedException.add(bracket);
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

        for (Element element : fragmentedException) {
            if (element instanceof Number) {
                return fragmentedException;
            }
        }

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
            if (br == ')') {
                type = false;
            } else {
                type = true;
            }
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