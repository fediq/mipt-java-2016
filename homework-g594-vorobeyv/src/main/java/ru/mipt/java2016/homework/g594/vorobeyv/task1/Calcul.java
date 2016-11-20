package ru.mipt.java2016.homework.g594.vorobeyv.task1;

/**
 * Created by Morell on 12.10.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import java.util.ArrayList;
import java.util.Stack;


public class Calcul implements Calculator {

    // Виды символов в parsed ArrayList.
    private boolean isOp(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isBreket(char c) {
        return c == '(' || c == ')';
    }

    // Если это +|- => то унарная, *,/=> false

    private boolean unaryOp(char c) {

        return c == '-';
    }

    private boolean decimal(char c) {
        return c == ' ' || c == '\n' || c == '\t';
    }

    private class Token {

    }

    private class Op extends Token {
        private char oper;
        private boolean unary;
        private byte priority;

        private Op(char oper) {
            this.oper = oper;
        }
    }

    private class Num extends Token {
        private double num;

        private Num(double num) {
            this.num = num;
        }
    }

    private class Brackets extends Token {
        private char br;
        private boolean type;

        private Brackets(char br, boolean type) {
            this.br = br;
            this.type = type;
        }
    }

    public double calculate(String expression) throws ParsingException {
        // Пустая строка. => except
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        // Тут не пустая строка.
        StringBuilder expr = new StringBuilder();
        expr.append(expression);

        // Проверка строки.
        ArrayList<Token> parsed = parser(expr);
        if (parsed.size() != 0) {  // Подсчет.
            return evaluator(parsed);
        } else {
            throw new ParsingException("Illegal ecpression");  // Строка была из пробелов
        }
    }

    private ArrayList<Token> parser(StringBuilder expr) throws ParsingException {
        ArrayList<Token> parsed = new ArrayList<Token>();

        int i = 0;
        int bracketBalance = 0;
        boolean unary = true;
        while (i < expr.length()) {
            char cur = expr.charAt(i);
            if (decimal(cur)) {
                i++;
            } else if (Character.isDigit(cur)) {
                unary = false;
                StringBuilder curDouble = new StringBuilder();
                //   curDouble.append( cur );
                while (i < expr.length()
                        && Character.isDigit(expr.charAt(i))) {
                    curDouble.append(expr.charAt(i));
                    i++; // ATTENTION 123v - после цикла i будет на v.
                }
                if (i < expr.length()) {
                    // Если точка, то на текущий момент вид: 213.^^^
                    if (expr.charAt(i) == '.') {
                        curDouble.append('.');
                        // Если выраж.: ^^^213. => throw Exception (точка не нужна)
                        if (i == expr.length() - 1) {
                            throw new ParsingException("Illegal expression");
                        } else if (Character.isDigit(expr.charAt(i + 1))) { // Если ^^^213.D
                            // Перешли на D.
                            i++;
                            while (i < expr.length()
                                    && Character.isDigit(expr.charAt(i))) {
                                curDouble.append(expr.charAt(i));
                                i++; // ATTENTION 123v - после цикла i будет на v.
                            }
                        }
                    } else { // Добавить '.0' до double
                        curDouble.append(".0");
                    }
                } else {
                    curDouble.append(".0");
                } // Если число в конце строки => покием строку по while выше
                // Тут в curDouble набран наш double
                String strDouble = curDouble.toString();
                double newDouble = Double.parseDouble(strDouble); // ATTENTION проверить parse -> double
                Num newNum = new Num(newDouble);
                parsed.add(newNum);
            } else if (isBreket(cur)) { // Скобка.
                boolean open;
                if (cur == '(') {
                    unary = true;
                    open = true;
                    bracketBalance++;
                } else {
                    unary = false;
                    open = false;
                    bracketBalance--;
                }
                Brackets newBracket = new Brackets(cur, open);
                parsed.add(newBracket);
                i++;
            } else if (isOp(cur)) { // Оператор
                Op newOp = new Op(cur);
                if (unary && unaryOp(cur)) {
                    newOp.unary = true;
                    newOp.priority = 4;
                } else if (unary && !unaryOp(cur)) { // Текущая операция должна быть унарной, но cur =*|/
                    throw new ParsingException("Illegal expression");
                } else { // Бинарная операция
                    newOp.unary = false;
                    if (cur == '+' || cur == '-') {
                        newOp.priority = 1;
                    } else {
                        newOp.priority = 2; // * или /
                    }
                }
                parsed.add(newOp);
                // Cur операц. => след операц. унар
                unary = true;
                i++;
            } else { // Символы не арифм.выраж.
                throw new ParsingException("Illegal expression");
            }
        }

        if (bracketBalance != 0) {
            throw new ParsingException("Illegal expression");
        } else {
            return parsed;
        }
    }

    private void elemOp(Stack<Token> numeral, Op curOp) throws ParsingException {
        // Унарная.
        if (curOp.unary) {
            if (numeral.empty()) {
                throw new ParsingException("Illegal expression");
            }
            Num lNum = (Num) numeral.peek();
            numeral.pop();
            switch (curOp.oper) {
                case '-':
                    lNum.num *= -1;
                    break;
                default:
            }
            numeral.push(lNum);
        } else { // Бинарная.
            if (numeral.size() < 2) {
                throw new ParsingException("Illegal expression");
            }
            Num rNum = (Num) numeral.peek();
            numeral.pop();
            Num lNum = (Num) numeral.peek();
            numeral.pop();
            // Результат пишем в Lnum
            switch (curOp.oper) {
                case '+':
                    lNum.num = lNum.num + rNum.num;
                    numeral.push(lNum);
                    break;
                case '-':
                    lNum.num = lNum.num - rNum.num;
                    numeral.push(lNum);
                    break;
                case '*':
                    lNum.num = lNum.num * rNum.num;
                    numeral.push(lNum);
                    break;
                case '/':
                    // ATTENTION бесконечность
                    if (Double.isInfinite(lNum.num / rNum.num)) {
                        lNum.num /= rNum.num;
                    } else {
                        lNum.num = lNum.num / rNum.num;
                    }
                    numeral.push(lNum);
                    break;
                default:
            }
        }
    }

    private double evaluator(ArrayList<Token> parsed) throws ParsingException {
        Stack<Token> numeral = new Stack<>();
        Stack<Token> operand = new Stack<>();
        for (int i = 0; i < parsed.size(); i++) {
            Token cur = parsed.get(i);
            if (cur instanceof Num) {
                numeral.push(cur);
            } else if (cur instanceof Brackets) {
                Brackets curBracket = (Brackets) cur;
                if (curBracket.br == '(') {
                    operand.push(cur);
                } else {
                    // Случай Cur =')' и на вершине operand ')' => баланс не 0( проверяли в parser )
                    if (numeral.empty()) {
                        throw new ParsingException("Illegal expression");
                    } else {
                        // Между '(' и ')' только +,-,*,/
                        while (operand.peek() instanceof Op) {
                            elemOp(numeral, (Op) operand.peek());
                            operand.pop();
                        }
                        operand.pop();
                    }
                }
            } else if (cur instanceof Op) {
                Op curOp = (Op) cur;
                while (!operand.empty()
                        && operand.peek() instanceof Op
                        && (curOp.unary && ((Op) operand.peek()).priority > curOp.priority
                        || !curOp.unary && ((Op) operand.peek()).priority >= curOp.priority)) {
                    elemOp(numeral, ((Op) operand.peek()));
                    operand.pop();

                }
                operand.push(curOp);
            }
        }

        if (numeral.empty()) {
            throw new ParsingException("Illegal expression");
        } else {
            while (!operand.empty()) {
                Op curOp = (Op) operand.peek();
                elemOp(numeral, curOp);
                operand.pop();
            }
            return ((Num) numeral.peek()).num;
        }
    }
}


