package ru.mipt.java2016.homework.g595.gusarova.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;

public class MyCalculator implements Calculator {
    //преобразует строку в массив элементов типа simple_struct
    private ArrayList<simple_struct> parsing(String expression) throws ParsingException
    {
        expression = expression.replaceAll("[ \n\t]", "");
        ArrayList<simple_struct> exp = new ArrayList<>();
        for (int i = 0; i < expression.length(); i++)
        {
            if (Character.isDigit(expression.charAt(i))) {
                Boolean dot = false;
                Integer pos = i;
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) ||
                                expression.charAt(i) == '.' && !dot))
                {
                    if (expression.charAt(i) == '.')
                        dot = true;
                    i++;
                }
                exp.add(new simple_struct(false, false, Double.parseDouble(expression.substring(pos, i))));
                i--;
                continue;
            }
            switch (expression.charAt(i)) {
                case '(':
                    exp.add(new simple_struct(false, true, 1.0));
                    break;

                case ')':
                    exp.add(new simple_struct(false, true, -1.0));
                    break;

                case '+':
                    exp.add(new simple_struct(true, false, 1.0));
                    break;

                case '-':
                    exp.add(new simple_struct(true, false, 2.0));
                    break;

                case '*':
                    exp.add(new simple_struct(true, false, 3.0));
                    if (i + 1 < expression.length() && expression.charAt(i + 1) == '-')
                    {
                        exp.add(new simple_struct(false, true, 1.0));
                        exp.add(new simple_struct(true, false, 2.0));
                        exp.add(new simple_struct(false, false, 1.0));
                        exp.add(new simple_struct(false, true, -1.0));
                        exp.add(new simple_struct(true, false, 3.0));
                        i++;
                    }
                    break;

                case '/':
                    exp.add(new simple_struct(true, false, 4.0));
                    if (i + 1 < expression.length() && expression.charAt(i + 1) == '-')
                    {
                        exp.add(new simple_struct(false, true, 1.0));
                        exp.add(new simple_struct(true, false, 2.0));
                        exp.add(new simple_struct(false, false, 1.0));
                        exp.add(new simple_struct(false, true, -1.0));
                        exp.add(new simple_struct(true, false, 4.0));
                        i++;
                    }
                    break;

                default: throw new ParsingException("Unknown symbol");
            }

        }
        return exp;
    }

    //выделяет из строки открывающие скобки в массив brackets, заполняет pair_bracket позицией,
    //соответствующей открывающей скобке закрывающей скобки, и инициализирует массив result,
    //массив результатов вычисления выражения в скобках, также проставляет массив step - сколько + 1
    //открывающих скобок лежит между текущей открывающей и соответствующей ей закрывающей
    private void extraction_of_brackets(ArrayList<simple_struct> exp,
                                        ArrayList<Integer> brackets,
                                        ArrayList<Integer> pair_bracket,
                                        ArrayList<Integer> step,
                                        ArrayList<Double> result) throws ParsingException
    {
        ArrayList<Integer> temp = new ArrayList<>();
        Integer counter = 0;
        for (int i = 0; i < exp.size(); i++)
            if (exp.get(i).bracket) {
                if (exp.get(i).number == 1) {
                    brackets.add(i);
                    result.add(0.0);
                    temp.add(brackets.size() - 1);
                    pair_bracket.add(0);
                    counter++;
                    step.add(counter);
                }
                else {
                    if (temp.isEmpty())
                        throw new ParsingException("Problem with brackets");
                    pair_bracket.set(temp.get(temp.size() - 1), i);
                    step.set(temp.get(temp.size() - 1), counter - step.get(temp.get(temp.size() - 1)) + 1);
                    temp.remove(temp.size() - 1);
                }
            }
        if (temp.size() != 0)
            throw new ParsingException("Problem with brackets");
    }

    //проводит вычисления * и / в выражении, не содержащем скобки
    //pointer - указатель на начало блока из чисел, *, /
    //result - результат вычисления блока из чисел, *, /, хранится только в начале блока
    private void calculate_mul_div(ArrayList<simple_struct> loc,
                                   ArrayList<Integer> pointer,
                                   ArrayList<Double> result) throws ParsingException {
        for (int i = 0; i < loc.size(); i++)
        {
            pointer.add(i);
            result.add(loc.get(i).number);
        }
        for (int i = 0; i < loc.size(); i++)
        {
            if (loc.get(i).operator && loc.get(i).number > 2)
            {
                if (i == 0 || !loc.get(i - 1).isNumber() || i == loc.size() - 1 || !loc.get(i + 1).isNumber())
                    throw new ParsingException("Problems with operators");
                pointer.set(i + 1, pointer.get(i - 1));
                if (loc.get(i).number == 3)
                    result.set(pointer.get(i - 1), result.get(pointer.get(i - 1)) * result.get(i + 1));
                else {
                    result.set(pointer.get(i - 1), result.get(pointer.get(i - 1)) / result.get(i + 1));
                }
            }
        }
    }

    //проводит вычисление + и - в выражении без скобок, с уже посчитаными связками *, /
    private Double calculate_add_subtrac(ArrayList<simple_struct> loc,
                                         ArrayList<Double> result) throws  ParsingException
    {
        Double answer = 0.0;
        if (loc.get(0).isNumber())
            answer = result.get(0);
        for (int i = 0; i < loc.size(); i++)
            if (loc.get(i).operator && loc.get(i).number <= 2)
            {
                if (i == loc.size() - 1 || !loc.get(i + 1).isNumber())
                    throw new ParsingException("Problem with operators");
                if (i == 0) {
                    if (loc.get(i).number == 1)
                        throw new ParsingException("Problem with operators");
                    else
                        answer = (-1) * result.get(i + 1);
                }
                else {
                    if (!loc.get(i + 1).isNumber())
                        throw new ParsingException("Problem with operators");
                    if (loc.get(i).number == 1)
                        answer = answer + result.get(i + 1);
                    else
                        answer = answer - result.get(i + 1);
                }


            }
        return answer;
    }


    @Override
    public double calculate(String expression) throws ParsingException
    {
        if (expression == null)
            throw new ParsingException("String is empty");
        ArrayList<simple_struct> exp;
        exp = parsing(expression);
        if (exp.isEmpty())
            throw new ParsingException("String is empty");
        ArrayList<Integer> brackets = new ArrayList<>();
        ArrayList<Integer> pair_bracket = new ArrayList<>();
        ArrayList<Integer> step = new ArrayList<>();
        ArrayList<Double> result = new ArrayList<>();
        extraction_of_brackets(exp, brackets, pair_bracket, step, result);

        Integer j;
        for (int i = brackets.size() - 1; i >= 0; i--) {
            if (i + 1 == pair_bracket.get(i))
                throw new ParsingException("String is empty");
            ArrayList<simple_struct> loc = new ArrayList<>();
            j = brackets.get(i) + 1;
            Integer counter = 1;
            while (j < pair_bracket.get(i))
            {
                if (!exp.get(j).bracket) {
                    loc.add(exp.get(j));
                    j++;
                }
                else {
                    loc.add(new simple_struct(false, false, result.get(i + counter)));
                    j = pair_bracket.get(i + counter) + 1;
                    counter += step.get(i  + counter);
                }
            }
            ArrayList<Integer> pointer = new ArrayList<>();
            ArrayList<Double> res = new ArrayList<>();
            calculate_mul_div(loc, pointer, res);
            Double answer = calculate_add_subtrac(loc, res);
            result.set(i, answer);
        }
        ArrayList<simple_struct> loc = new ArrayList<>();
        j = 0;
        Integer counter =0;
        while (j < exp.size())
        {
            if (!exp.get(j).bracket) {
                loc.add(exp.get(j));
                j++;
            }
            else {
                loc.add(new simple_struct(false, false, result.get(counter)));
                j = pair_bracket.get(counter) + 1;
                counter += step.get(counter);
            }
        }
        ArrayList<Integer> pointer = new ArrayList<>();
        ArrayList<Double> res = new ArrayList<>();
        calculate_mul_div(loc, pointer, res);
        return calculate_add_subtrac(loc, res);
    }

}
