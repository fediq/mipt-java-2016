package ru.mipt.java2016.homework.g596.kravets.task1;

import java.util.Stack;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

/**
 * @author Alena Kravets
 * @since 12.10.16
 */


public class MyCalculator implements Calculator {


    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null || expression.equals("")) {
            throw new ParsingException("Expression is null!");
        }
        return ToCalculate(GoToPolishNotation(expression));
    }

    private double ToCalculate(String expression)
            throws ParsingException { // Считаем по польской записи
        Stack<Double> NewStackResult = new Stack<>();
        String[] PartsOfTheStringResult = expression
                .split(" ");  // разделяем строку по пробелам, на нулевой позиции пустая строка
        if (PartsOfTheStringResult.length == 1) {
            throw new ParsingException("Invalid expression!");
        }
        for (int i = 1; i < PartsOfTheStringResult.length; i++) {
            String part = PartsOfTheStringResult[i];
            if (!IsOperator(part.charAt(0))) {  // Кладем в стек число
                NewStackResult.push(Double.parseDouble(
                        part)); // преобразовуем строку в действительное число и добаляем в стэк
            } else  // а если это оператор, то считаем два  числа и результат гоняем в стэк
                if (part.length() == 1 && NewStackResult.size() >= 2
                        && part.charAt(0) != '$') {  // проверка что это оператор
                    double first = NewStackResult.pop(); // снимаем с вершины стэка первое слагаемое
                    double second =
                            NewStackResult.pop(); // снимаем с вершины стэка второе слагаемое
                    NewStackResult.push(IntermediateCalculation(first, second, part.charAt(
                            0))); // Делаем промежуточный расчет и кладем результат в стэк
                } else if (part.length() == 1 && NewStackResult.size() >= 1
                        && part.charAt(0) == '$') { // Если унарный минус
                    NewStackResult.push(-NewStackResult.pop()); // преобразовуем
                } else {
                    throw new ParsingException("Invalid expression!");
                }
        }
        double result = NewStackResult.pop();
        if (!NewStackResult.empty()) { // В стэеке не должно быть элементов
            throw new ParsingException("Invalid expression!");
        }
        return result;
    }

    private String GoToPolishNotation(String expression) throws ParsingException {
        Stack<Character> StackOperations = new Stack<>();// Стек для хранения операторов
        StringBuilder StringResult = new StringBuilder(""); //Результующая строка
        boolean flag = true; // метка что последующий оператор унарный
        boolean beginningOfDoubleNumber = false;  // флаг для записи действительного числа
        int amounPoint = 0;  // проверка что дейтвительное число существует
        for (Character chr : expression.toCharArray()) {
            if (!CheckSymbolOfExpression(chr)) {  // если это вообще какой-то левый символ
                throw new ParsingException("Invalid expression!");
            }
            if (IsNumber(chr)) {  // если число - добавляем в итоговую строку
                flag = false;
                if (!beginningOfDoubleNumber) { // начало записи числа в строку
                    beginningOfDoubleNumber = true; // Отмечаем что запись числа в троку началось
                    amounPoint = 0;  // количество точек снова равно нулю
                    StringResult.append(" ").append(chr); //  // запись первой цифры
                } else { // Если продолжение числа , то просто записуем последующую цифру или точку
                    StringResult.append(chr);  // out = out + chr;
                    if (chr
                            == '.') { // Если это точка, то увиличиваем количество точек в данном числе
                        amounPoint++;
                        if (amounPoint >= 2) { // Проверка на что что точек не больше двух
                            throw new ParsingException("Invalid expression!");
                        }
                    }
                }

                continue; // Переходим к новой иттерации цикла
            }

            beginningOfDoubleNumber = false; // Cтавим метку что чило записали в строку
            if (IsOperator(chr)) { // проверка, что оператор
                if (flag) {  // проверка что унарный
                    if (chr.equals('-')) {  //кладем в стэк унарный минус, обозначаемый через $
                        StackOperations.push('$'); // Добавляем в стек & - унарный минус
                        flag = false;
                    } else if (chr.equals('+')) {
                        flag = false;
                    } else {
                        throw new ParsingException("Invalid expression!");
                    }
                } else {  // иначе то выталкиваем вершину стэка по приоритету
                    flag = true; // метка что последующий оператор унарный
                    while (!StackOperations
                            .empty()) { // Пока стэк операторов не пуст, выталкиваем из стэка всё символы, у которых приоритет больше данного и записываем в строку
                        Character PresentOperator =
                                StackOperations.pop(); // Снимаем с вершины стэка
                        if (CheckPriority(chr) <= CheckPriority(
                                PresentOperator)) { // сравниваем приоритет
                            StringResult.append(" ")
                                    .append(PresentOperator);  // //записуем в результуюую строку
                        } else {
                            StackOperations.push(PresentOperator); // Иначе добавляем в стэк
                            break; // выходим из циклв while..
                        }
                    }
                    StackOperations.push(chr);// кладем оператор в стэк
                }
            }
            if (chr.equals('(')) {  // если идет открыающиися скобка
                StackOperations.push(chr); // Кладем в стэк
                flag = true;
            } else if (chr.equals(')')) {  // если закрывающая скобка
                flag = false;
                boolean PresenceOfOpeningBrackets = false;
                while (!StackOperations
                        .empty()) { // то выталкиваем элементы из стека в итоговую строку
                    Character PresentOperator = StackOperations.pop();
                    if (PresentOperator.equals('(')) { // пока не найдем открывающую скобку
                        PresenceOfOpeningBrackets = true;
                        break;
                    } else {
                        StringResult.append(" ")
                                .append(PresentOperator);  // //записуем в результуюую строку
                    }
                }
                if (!PresenceOfOpeningBrackets) { // Eсли нарушен скобочный баланс, кидаем исключение
                    throw new ParsingException("Invalid expression!");
                }
            }
        }
        while (!StackOperations.empty()) { // Записываем элементы , которые остались  стэке
            Character PresentOperator = StackOperations.pop();
            if (PresentOperator.equals('&') || IsOperator(PresentOperator)) {
                StringResult.append(" ").append(PresentOperator);
            } else {
                throw new ParsingException("Invalid expression!");
            }
        }
        return StringResult.toString(); // возвращаем искомую строку
    }

    private boolean CheckSymbolOfExpression(char chr) throws ParsingException {
        return (IsNumber(chr) || IsOperator(chr) || chr == ' ' || chr == '\n' || chr == '\t'
                || chr == '(' || chr == ')');
    }

    private boolean IsNumber(char chr)
            throws ParsingException { //Проерка что символ принадлежит действительному числу
        return chr == '.' || chr == '0' || chr == '1' || chr == '2' || chr == '3' || chr == '4'
                || chr == '5' || chr == '6' || chr == '7' || chr == '8' || chr == '9';
    }

    private boolean IsOperator(char chr)
            throws ParsingException { // Проверка что символ есть оператором
        return (chr == '$' || chr == '+' || chr == '-' || chr == '/' || chr == '*');
    }

    private int CheckPriority(Character chr)
            throws ParsingException { // Проверка приоритета символа
        if (chr.equals('(') || chr.equals('(')) {
            return 0;
        }
        if (chr.equals('+') || chr.equals('-')) {
            return 1;
        }
        if (chr.equals('*') || chr.equals('/')) {
            return 2;
        }
        if (chr.equals('&')) {
            return 3;
        } else {
            throw new ParsingException("Invalid symbol");
        }
    }

    private double IntermediateCalculation(double first, double second, char operator)
            throws ParsingException {
        switch (operator) {
            case '+':
                return first + second;
            case '-':
                return second - first;
            case '*':
                return first * second;
            case '/':
                return second / first;
            default:
                throw new ParsingException("Invalid symbol!");
        }
    }
}
