package ru.mipt.java2016.homework.g595.gusarova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.gusarova.task1.SimpleStruct;

import java.util.ArrayList;

public class MyCalculator implements Calculator {
    //преобразует строку в массив элементов типа SimpleStruct
    private ArrayList<SimpleStruct> parsing(String expression) throws ParsingException {
        expression = expression.replaceAll("[ \n\t]", "");
        ArrayList<SimpleStruct> exp = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++) {
            if (Character.isDigit(expression.charAt(i))) {
                Boolean dot = false;
                Integer pos = i;
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) ||
                                expression.charAt(i) == '.' && !dot)) {
                    if (expression.charAt(i) == '.') {
                        dot = true;
                    }
                    i++;
                }
                exp.add(new SimpleStruct(false, false, Double.parseDouble(expression.substring(pos, i))));
                i--;
                continue;
            }
            switch (expression.charAt(i)) {
                case '(':
                    exp.add(new SimpleStruct(false, true, 1.0));
                    break;

                case ')':
                    exp.add(new SimpleStruct(false, true, -1.0));
                    break;

                case '+':
                    exp.add(new SimpleStruct(true, false, 1.0));
                    break;

                case '-':
                    exp.add(new SimpleStruct(true, false, 2.0));
                    break;

                case '*':
                    exp.add(new SimpleStruct(true, false, 3.0));
                    if (i + 1 < expression.length() && expression.charAt(i + 1) == '-') {
                        exp.add(new SimpleStruct(false, true, 1.0));
                        exp.add(new SimpleStruct(true, false, 2.0));
                        exp.add(new SimpleStruct(false, false, 1.0));
                        exp.add(new SimpleStruct(false, true, -1.0));
                        exp.add(new SimpleStruct(true, false, 3.0));
                        i++;
                    }
                    break;

                case '/':
                    exp.add(new SimpleStruct(true, false, 4.0));
                    if (i + 1 < expression.length() && expression.charAt(i + 1) == '-') {
                        exp.add(new SimpleStruct(false, true, 1.0));
                        exp.add(new SimpleStruct(true, false, 2.0));
                        exp.add(new SimpleStruct(false, false, 1.0));
                        exp.add(new SimpleStruct(false, true, -1.0));
                        exp.add(new SimpleStruct(true, false, 4.0));
                        i++;
                    }
                    break;

                default:
                    throw new ParsingException("Unknown symbol");
            }

        }
        return exp;
    }

    //выделяет из строки открывающие скобки в массив brackets, заполняет pair_bracket позицией,
    //соответствующей открывающей скобке закрывающей скобки, и инициализирует массив result,
    //массив результатов вычисления выражения в скобках, также проставляет массив step - сколько + 1
    //открывающих скобок лежит между текущей открывающей и соответствующей ей закрывающей
    private void extractionOfBrackets(ArrayList<SimpleStruct> exp,
                                      ArrayList<Integer> brackets,
                                      ArrayList<Integer> pairBracket,
                                      ArrayList<Integer> step,
                                      ArrayList<Double> result) throws ParsingException {
        ArrayList<Integer> temp = new ArrayList<>();
        Integer counter = 0;
        for (int i = 0; i < exp.size(); i++) {
            if (exp.get(i).isBracket()) {
                if (exp.get(i).getNumber() == 1) {
                    brackets.add(i);
                    result.add(0.0);
                    temp.add(brackets.size() - 1);
                    pairBracket.add(0);
                    counter++;
                    step.add(counter);
                } else {
                    if (temp.isEmpty()) {
                        throw new ParsingException("Problem with brackets");
                    }
                    pairBracket.set(temp.get(temp.size() - 1), i);
                    step.set(temp.get(temp.size() - 1), counter - step.get(temp.get(temp.size() - 1)) + 1);
                    temp.remove(temp.size() - 1);
                }
            }
        }
        if (temp.size() != 0) {
            throw new ParsingException("Problem with brackets");
        }
    }

    //проводит вычисления * и / в выражении, не содержащем скобки
    //pointer - указатель на начало блока из чисел, *, /
    //result - результат вычисления блока из чисел, *, /, хранится только в начале блока
    private void calculateMulDiv(ArrayList<SimpleStruct> loc,
                                 ArrayList<Integer> pointer,
                                 ArrayList<Double> result) throws ParsingException {
        for (int i = 0; i < loc.size(); i++) {
            pointer.add(i);
            result.add(loc.get(i).getNumber());
        }
        for (int i = 0; i < loc.size(); i++) {
            if (loc.get(i).isOperator() && loc.get(i).getNumber() > 2) {
                if (i == 0 || !loc.get(i - 1).isNumber() || i == loc.size() - 1 || !loc.get(i + 1).isNumber()) {
                    throw new ParsingException("Problems with operators");
                }
                pointer.set(i + 1, pointer.get(i - 1));
                if (loc.get(i).getNumber() == 3) {
                    result.set(pointer.get(i - 1), result.get(pointer.get(i - 1)) * result.get(i + 1));
                } else {
                    result.set(pointer.get(i - 1), result.get(pointer.get(i - 1)) / result.get(i + 1));
                }
            }
        }
    }

    //проводит вычисление + и - в выражении без скобок, с уже посчитаными связками *, /
    private Double calculateAddSubtrac(ArrayList<SimpleStruct> loc,
                                       ArrayList<Double> result) throws ParsingException {
        Double answer = 0.0;
        if (loc.get(0).isNumber()) {
            answer = result.get(0);
        }
        for (int i = 0; i < loc.size(); i++) {
            if (loc.get(i).isOperator() && loc.get(i).getNumber() <= 2) {
                if (i == loc.size() - 1 || !loc.get(i + 1).isNumber()) {
                    throw new ParsingException("Problem with operators");
                }
                if (i == 0) {
                    if (loc.get(i).getNumber() == 1) {
                        throw new ParsingException("Problem with operators");
                    } else {
                        answer = (-1) * result.get(i + 1);
                    }
                } else {
                    if (!loc.get(i + 1).isNumber()) {
                        throw new ParsingException("Problem with operators");
                    }
                    if (loc.get(i).getNumber() == 1) {
                        answer = answer + result.get(i + 1);
                    } else {
                        answer = answer - result.get(i + 1);
                    }
                }

            }
        }
        return answer;
    }


    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("String is empty");
        }
        ArrayList<SimpleStruct> exp;
        exp = parsing(expression);
        if (exp.isEmpty()) {
            throw new ParsingException("String is empty");
        }
        ArrayList<Integer> brackets = new ArrayList<>();
        ArrayList<Integer> pairBracket = new ArrayList<>();
        ArrayList<Integer> step = new ArrayList<>();
        ArrayList<Double> result = new ArrayList<>();
        extractionOfBrackets(exp, brackets, pairBracket, step, result);

        Integer j;
        for (int i = brackets.size() - 1; i >= 0; i--) {
            if (i + 1 == pairBracket.get(i)) {
                throw new ParsingException("String is empty");
            }
            ArrayList<SimpleStruct> loc = new ArrayList<>();
            j = brackets.get(i) + 1;
            Integer counter = 1;
            while (j < pairBracket.get(i)) {
                if (!exp.get(j).isBracket()) {
                    loc.add(exp.get(j));
                    j++;
                } else {
                    loc.add(new SimpleStruct(false, false, result.get(i + counter)));
                    j = pairBracket.get(i + counter) + 1;
                    counter += step.get(i + counter);
                }
            }
            ArrayList<Integer> pointer = new ArrayList<>();
            ArrayList<Double> res = new ArrayList<>();
            calculateMulDiv(loc, pointer, res);
            Double answer = calculateAddSubtrac(loc, res);
            result.set(i, answer);
        }
        ArrayList<SimpleStruct> loc = new ArrayList<>();
        j = 0;
        Integer counter = 0;
        while (j < exp.size()) {
            if (!exp.get(j).isBracket()) {
                loc.add(exp.get(j));
                j++;
            } else {
                loc.add(new SimpleStruct(false, false, result.get(counter)));
                j = pairBracket.get(counter) + 1;
                counter += step.get(counter);
            }
        }
        ArrayList<Integer> pointer = new ArrayList<>();
        ArrayList<Double> res = new ArrayList<>();
        calculateMulDiv(loc, pointer, res);
        return calculateAddSubtrac(loc, res);
    }

}
