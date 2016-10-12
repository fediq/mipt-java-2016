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
    private final String OPERATORS = "+-*/_";
    private final String BRACKETS = "()";
    private final String SYMBOLS = "0123456789.";

    private ArrayDeque< String > targetNotation = new ArrayDeque<>();
    private ArrayDeque< Integer > targetValence = new ArrayDeque<>();

    public double calculate (String expression) throws ParsingException
    {
        return calculations(expression);
    }

    private double calculations(String expression) throws ParsingException
    {
        getPolishNotation(prepareExpression(expression));
        Stack< Double > calculationsStack = new Stack<>();
        while(!targetNotation.isEmpty())
        {
            String token = targetNotation.remove();
            Integer tokenValence = targetValence.remove();
            if(tokenValence == 0)
            {
                calculationsStack.push(Double.parseDouble(token));
                continue;
            }
            if(tokenValence == 1)
            {
                Double item = calculationsStack.pop();
                calculationsStack.push(-1*item);
            }
            if(tokenValence == 2)
            {
                if(calculationsStack.size() < 2)
                {
                    throw new ParsingException("Invalid Expression");
                }
                Double rightOperand = calculationsStack.pop();
                Double leftOperand = calculationsStack.pop();
                calculationsStack.push(Operate(leftOperand,rightOperand,token));
            }

        }
        if(calculationsStack.size() != 1)
        {
            throw new ParsingException("Invalid Expression");
        }
        return calculationsStack.pop();
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

    private void getPolishNotation(StringTokenizer tokenList) throws ParsingException
    {

        Stack< String > stackOperators = new Stack<>();
        String prevToken = "";
        while( tokenList.hasMoreTokens())
        {
            String token = tokenList.nextToken();
            if(isNumber(token))
            {
                targetNotation.push(token);
                targetValence.push(getValence(token));
            }
            if(isOperator(token))
            {
                if(isOperator(prevToken) && isOperatorMinus(token))
                {
                    token = "_";
                }
                if(getPrecedence(stackOperators.peek()) >= getPrecedence(token))
                {
                    targetValence.push(getValence(stackOperators.peek()));
                    targetNotation.push(stackOperators.pop());
                }
                stackOperators.push(token);
            }
            if(isOpenBracket(token))
            {
                stackOperators.push(token);
            }
            if(isCloseBracket(token))
            {
                while(!stackOperators.empty() && !isOpenBracket(stackOperators.peek()))
                {
                    targetValence.push(getValence(stackOperators.peek()));
                    targetNotation.push(stackOperators.pop());
                }
                if(stackOperators.empty())
                {
                    throw new ParsingException("Invalid Expression");
                } else
                {
                    stackOperators.pop();
                }
            }
            prevToken = token;
        }
        while(!stackOperators.empty())
        {
            if(isOpenBracket(stackOperators.peek()))
            {
                throw new ParsingException("Invalid Expression");
            }
            targetValence.push(getValence(stackOperators.peek()));
            targetNotation.push(stackOperators.peek());
        }
    }

    private Double Operate(Double leftOperand,Double rightOperand,String Operator)
    {
        if(Operator.equals("+"))
        {
            leftOperand = leftOperand + rightOperand;
        }
        if(Operator.equals("-"))
        {
            leftOperand =  leftOperand - rightOperand;
        }
        if(Operator.equals("*"))
        {
            leftOperand = leftOperand * rightOperand;
        }
        if(Operator.equals("/"))
        {
            leftOperand =  leftOperand / rightOperand;
        }
        return leftOperand;
    }

    private boolean isNumber (String token)
    {
        for(int i = 0;i<token.length();++i)
        {
            if(!SYMBOLS.contains(String.valueOf(token.charAt(i))))
            {
                return false;
            }
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

    private boolean isOperatorMinus (String token)
    {
        return token.equals("-");
    }

    private byte getPrecedence(String token)
    {
        if (token.equals("+") || token.equals("-"))
        {
            return 1;
        }
        if(token.equals("_"))
        {
            return 3;
        }
        return 2;
    }
    private int getValence(String token)
    {
        if(isNumber(token))
        {
            return 0;
        }
        if(token.equals("_"))
        {
            return 1;
        }
        return 2;
    }

}
