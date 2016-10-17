package ru.mipt.java2016.homework.g595.turumtaev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * калькулятор
 *
 * @author Galim Turumtaev
 * @since 10.10.2016
 */

public class MyCalculator implements Calculator {
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        StringBuilder polishExpression; //StringBuider удобнее, потому что я постоянно добавляю в конец строки
        polishExpression = getPostfixLine(expression); //получим польскую нотацию
        return calculateValue(polishExpression); //посчитаем значение
    }

    private boolean isOperator(char letter) { //оператор ли?
        return (letter == '*' || letter == '/' || letter == '+' || letter == '-');
    }

    private int getPriority(char letter) throws ParsingException { //у операций есть приоритет
        int result = -1;
        if (letter == '(' || letter == ')') {
            result = 0;
        } else if (letter == '+' || letter == '-') {
            result = 1;
        } else if (letter == '*' || letter == '/') {
            result = 2;
        } else if (letter == '~') {
            result = 3;
        }
        if (result == -1) {
            throw new ParsingException("priority error");
        }
        return result;
    }

    private boolean isSpace(char letter) {
        return (letter == ' ' || letter == '\n' || letter == '\t');
    } //на эти знаки не обращаем внимания

    private StringBuilder getPostfixLine(String expression) throws ParsingException {
        StringBuilder resultExpression = new StringBuilder(); //StringBuilder удобнее
        Stack<Character> operators = new Stack<>(); //стек для операторов
        boolean isUnary; //является ли оператор унарным
        isUnary = true; //если операция в самом начале строки, то она унарна
        for (int i = 0; i < expression.length(); i++) {
            char letter = expression.charAt(i); // берем очередной знак
            if (isOperator(letter)) { //если он оператор
                resultExpression.append(' '); // добавим вместо оператора пробел, чтобы числа не склеились
                if (isUnary) { //если оператор унарный
                    if (letter == '+') { //если унарный плюс, то просто убираем его
                        isUnary = false;
                    } else if (letter == '-') { //если унарный минус, то:
                        operators.push('~'); // специальный знак, который обозначает унарный минус
                        isUnary = false;
                    } else { //других унарных операторов наш калькулятор не понимает
                        throw new ParsingException("Invalid expression");
                    }
                } else {
                    isUnary = true; //после оператора следующий оператор унарный
                    while (!operators.empty()) { //пропихнем в строку все операторы большего приоритета
                        char current = operators.pop();
                        if (getPriority(letter) <= getPriority(current)) {
                            resultExpression.append(' ');
                            resultExpression.append(current);
                            resultExpression.append(' ');
                        } else {
                            operators.push(current);
                            break;
                        }
                    }
                    operators.push(letter); // Помещаем оператор в стек
                }
            } else if (letter == '(') { //скобку просто помещаем в стек
                operators.push(letter);
                isUnary = true;
            } else if (letter == ')') { //если закрывающая скобка, то ищем открывающую

                while (!operators.isEmpty() && operators.peek() != '(') {
                    resultExpression.append(' ');
                    resultExpression.append(operators.pop());
                    resultExpression.append(' ');
                }
                if (operators.isEmpty()) {
                    throw new ParsingException("skobe balance error"); //если не нашли, то нарушен скобочный баланс
                }
                operators.pop(); //вытащим и открывающую
                isUnary = false; //после скобки оператор бинарный
            } else if (Character.isDigit(letter) || letter == '.') { //числа просто печатаем в строку
                resultExpression.append(letter);
                isUnary = false;
            } else if (isSpace(letter)) { //не реагируем на пробелы и enter
                continue;
            } else {
                throw new ParsingException("invalid expression"); //недопустимый знак
            }
        }
        while (!operators.empty()) { // Выталкиваем оставшиеся элементы из стека
            char letter = operators.pop();
            if (isOperator(letter) || letter == '~') {
                resultExpression.append(' ');
                resultExpression.append(letter);
                resultExpression.append(' ');
            } else {
                throw new ParsingException("Invalid expression");
            }
        }
        return resultExpression;
    }

    private double calculateValue(StringBuilder expression) throws ParsingException {
        Stack<Double> numbers = new Stack<>(); // стек для промежуточных результатов
        for (int i = 0; i < expression.length(); i++) {
            char letter = expression.charAt(i); //очередной знак
            if (isOperator(letter) || letter == '~') { //если оператор
                if (letter == '~') { //если унарный минус
                    if (numbers.empty()) {
                        throw new ParsingException("invalid expression");
                    }
                    double number = numbers.pop();
                    numbers.push(-1 * number);
                } else {
                    if (numbers.size() < 2) { //для бинарного оператора нужно хотя бы 2 операнда
                        throw new ParsingException("invalid expression");
                    }
                    double firstNumber = numbers.pop(); //первый операнд
                    double secondNumber = numbers.pop(); //второй операнд
                    if (letter == '-') { //далее проделываем операцию
                        numbers.push(secondNumber - firstNumber);
                    } else if (letter == '+') {
                        numbers.push(secondNumber + firstNumber);
                    } else if (letter == '/') {
                        numbers.push(secondNumber / firstNumber);
                    } else if (letter == '*') {
                        numbers.push(secondNumber * firstNumber);
                    }
                }
            }
            if (Character.isDigit(letter)) { //если цифра
                boolean isFloatingNumber = false; //встретили ли точку в записи числа
                double result = charToInt(letter); //из символа получим цифру
                while (Character.isDigit(expression.charAt(i + 1))) { //если следующий символ тоже цифра
                    i++;
                    result = result * 10 + charToInt(expression.charAt(i));
                }
                if (expression.charAt(i + 1) == '.') { //если точка, переходим к дробной части
                    i++;
                    isFloatingNumber = true;
                }
                double fracktion = 1; //на каком мы разряде
                while (Character.isDigit(expression.charAt(i + 1))) { //пока встречаем цифры
                    i++;
                    fracktion *= 0.1;
                    result = result + fracktion * charToInt(expression.charAt(i));
                }
                if (expression.charAt(i + 1) == '.') { //слишком много точек
                    throw new ParsingException("invalid floating number");
                }
                numbers.push(result); //в стек
            }
        }
        if (numbers.size() == 1) { //в конце в стеке должен остаться только конечный ответ
            return numbers.pop();
        } else {
            throw new ParsingException("Invalid expression"); //операторов оказалось слишком мало или слишком много
        }
    }

    private int charToInt(char letter) throws ParsingException {
        String str = String.valueOf(letter); //мне сказали использовать (2), но оно принимает только строки :\
        Integer digit = Integer.parseInt(str); //(2), тут ьмираньше была простыня из switch -ей
        return digit;
    }
}
