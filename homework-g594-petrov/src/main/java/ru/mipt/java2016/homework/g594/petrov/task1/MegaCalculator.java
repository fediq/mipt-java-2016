package ru.mipt.java2016.homework.g594.petrov.task1;

/**
 * Created by Филипп on 10.10.2016.
 */

import com.sun.deploy.net.proxy.pac.PACFunctions;
import com.sun.org.apache.xerces.internal.impl.dv.xs.DoubleDV;
import com.sun.org.apache.xpath.internal.operations.Bool;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;


import java.util.*;

public class MegaCalculator implements Calculator
{
    private static final HashSet<Character> NUMERALS = new HashSet<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.'));
    private static final HashSet<Character> OPERATORS = new HashSet<>(Arrays.asList('+', '-', '*', '/'));

    private static Integer getPriority(Character c) throws ParsingException
    {
        if(c.equals('+') || c.equals('-'))
            return 1;
        if(c.equals('*') || c.equals('/'))
            return 2;
        if(c.equals('&'))
            return 3;
        if(c.equals('(') || c.equals(')'))
            return 0;
        throw new ParsingException("Invalid symbol");
    }

    @Override
    public double calculate(String expression) throws ParsingException
    {
        if(null == expression)
            throw new ParsingException("Expression is null");
        String postfixRecord = getPostfixNotation(expression);
        return calculatePostfixNotation(postfixRecord);
    }

    private String getPostfixNotation(String expression) throws ParsingException
    {
        boolean flag = true;
        Stack<Character> operatorStack  = new Stack<>();
        StringBuilder postfixNotation = new StringBuilder();
        for(Character c : expression.toCharArray())
        {
            if(Character.isWhitespace(c))
                continue;
            if(NUMERALS.contains(c))
            {
                flag = false;
                postfixNotation.append(c);
                continue;
            }
            if(OPERATORS.contains(c))
            {
                if(flag)
                {
                    if (c == '+')
                    {
                        flag = false;
                        postfixNotation.append(' ').append(' ');
                        continue;
                    }
                    if (c == '-')
                    {
                        flag = false;
                        while (!operatorStack.empty())
                        {
                            if(getPriority(operatorStack.lastElement()) >= 3)
                            {
                                postfixNotation.append(' ').append(operatorStack.lastElement()).append(' ');
                                operatorStack.pop();
                            }
                            else
                                break;
                        }
                        operatorStack.push('&');
                        postfixNotation.append(' ').append(' ');
                    }
                    else
                        throw new ParsingException("Invalid expression");
                }
                else
                {
                    flag = true;
                    while (!operatorStack.empty())
                    {
                        if(getPriority(operatorStack.lastElement()) >= getPriority(c))
                        {
                            postfixNotation.append(' ').append(operatorStack.lastElement()).append(' ');
                            operatorStack.pop();
                        }
                        else
                            break;
                    }
                    operatorStack.push(c);
                    postfixNotation.append(' ').append(' ');
                }
            }
            else
                if(c.equals('('))
                {
                    flag = true;
                    operatorStack.push('(');
                    postfixNotation.append(' ').append(' ');
                }
                else
                    if(c.equals(')'))
                    {
                        boolean isOpen = false;
                        flag = false;
                        postfixNotation.append(' ').append(' ');
                        while(!operatorStack.empty())
                        {
                            Character tmp = operatorStack.lastElement();
                            operatorStack.pop();
                            if(tmp.equals('('))
                            {
                                isOpen = true;
                                break;
                            }
                            else
                                postfixNotation.append(' ').append(tmp).append(' ');
                        }
                        if(!isOpen)
                            throw new ParsingException("Invalid expression");
                    }
                    else
                        throw new ParsingException("Invalid symbol");


        }
        while(!operatorStack.empty())
        {
            if(OPERATORS.contains(operatorStack.lastElement()) || operatorStack.lastElement().equals('&'))
            {
                postfixNotation.append(' ').append(operatorStack.lastElement()).append(' ');
                operatorStack.pop();
            }
            else
                throw new ParsingException("Invalid expression");
        }
        return postfixNotation.toString();
    }
    private double calculatePostfixNotation(String postfixNotation) throws ParsingException
    {
        Scanner scanner = new Scanner(postfixNotation);
        Stack<Double> numbers = new Stack<>();
        while(scanner.hasNext())
        {
            String pattern = scanner.next();
            if(pattern.length() == 1)
            {
                if (OPERATORS.contains(pattern.charAt(0)))
                {
                    if(numbers.size() >= 2)
                    {
                        Double arg1 = numbers.pop();
                        Double arg2 = numbers.pop();
                        try {
                            numbers.push(calculateOperator(arg2, arg1, pattern.charAt(0)));
                        } catch (ParsingException e) {
                            if(e.getMessage().equals("Infinity"))
                                return Double.POSITIVE_INFINITY;
                            if(e.getMessage().equals("Minus Infinity"))
                                return Double.NEGATIVE_INFINITY;
                        }
                    }
                    else
                        throw new ParsingException("Invalid expression");
                }
                else
                    if(pattern.charAt(0) == '&')
                    {
                        if(numbers.size() >= 1)
                        {
                            Double arg = numbers.pop();
                            numbers.push(-arg);
                        }
                        else
                            throw new ParsingException("Invalid expression");
                    }
                    else
                    {
                        if(NUMERALS.contains(pattern.charAt(0)))
                        {
                            Double tmp;
                            try {
                                tmp = Double.parseDouble(pattern);
                                numbers.push(tmp);
                            } catch(NumberFormatException e){
                                throw new ParsingException(e.getMessage(), e.getCause());
                            }
                        }
                        else
                            throw new ParsingException("Invalid symbol");
                    }
            }
            else
            {
                Double tmp;
                try {
                    tmp = Double.parseDouble(pattern);
                    numbers.push(tmp);
                } catch(NumberFormatException e){
                    throw new ParsingException(e.getMessage(), e.getCause());
                }
            }
        }
        if(numbers.size() == 1)
        {
            return numbers.lastElement();
        }
        else
            throw new ParsingException("Invalid expression");
    }

    private double calculateOperator(double a, double b, char c) throws ParsingException
    {
        if(c == '+')
            return a + b;
        if(c == '-')
            return a - b;
        if(c == '*')
            return a * b;
        if(c == '/')
        {
            return a / b;
        }
        throw new ParsingException("Invalid symbol");
    }
}
