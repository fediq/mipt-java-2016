package ru.mipt.java2016.homework.g595.rodin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Created by Дмитрий on 09.10.16.
 */
public class CStackCalculator implements Calculator
{
    private final String OPERATORS = "+-*/";
    private final String BRACKETS = "()";
    private final String SYMBOLS = "0123456789.";
    public double calculate (String expression) throws ParsingException
    {
        return 0;
    }


    private StringTokenizer prepareExpression(String expression)
    {
        expression = expression.replace(" ","").replace("(-","(0-");
        if(expression.charAt(0) == '-')
        {
            expression = "0" + expression;
        }
        StringTokenizer tokenList = new StringTokenizer(expression
                ,OPERATORS + BRACKETS,true);
        return tokenList;
    }

    private ArrayDeque< String >
        getPolishNotation(StringTokenizer tokenList) throws ParsingException
    {
        ArrayDeque< String > targetNotation = new ArrayDeque<>();
        Stack< String > stackOperators = new Stack<>();
        while( tokenList.hasMoreTokens())
        {
            String token = tokenList.nextToken();
            if(isNumber(token))
            {
                targetNotation.push(token);
                continue;
            }
            if(isOperator(token))
            {
                if(getPrecedence(stackOperators.peek()) >= getPrecedence(token))
                {
                    targetNotation.push(stackOperators.pop());
                }
                stackOperators.push(token);
                continue;
            }
            if(isOpenBracket(token))
            {
                stackOperators.push(token);
                continue;
            }
            if(isCloseBracket(token))
            {
                while(!stackOperators.empty() && !isCloseBracket(stackOperators.peek()))
                {
                    targetNotation.push(stackOperators.pop());
                }
                if(stackOperators.empty())
                    throw new ParsingException("Invalid Expression");
            }
        }
        return targetNotation;
    }


    private boolean isNumber (String token)
    {
        try
        {
            Double.parseDouble(token);
        } catch (Exception exception)
        {
            return false;
        }
        return true;
    }

    private boolean isOpenBracket(String token)
    {
        return token.equals("(");
    }

    private boolean isCloseBracket(String token)
    {
        return token.equals(")");
    }

    private boolean isOperator(String token)
    {
        return OPERATORS.contains(token);
    }

    private byte getPrecedence(String token)
    {
        if (token.equals("+") || token.equals("-"))
        {
            return 1;
        }
        return 2;
    }
}
