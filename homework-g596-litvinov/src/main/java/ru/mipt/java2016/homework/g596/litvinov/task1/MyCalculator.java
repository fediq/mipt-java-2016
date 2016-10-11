package ru.mipt.java2016.homework.g596.litvinov.task1;

/**
 * Created by stanislav on 29.09.16.
 */

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

public class MyCalculator implements Calculator {

    int pos = -1;
    char ch, prevCh= 'Q';
    int bracesCount  = 0;
    boolean isFirstBrace = true;
    boolean isFinalBrace = false;



    
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        expression = "(" + expression + ")";
        try {
            Stack<Double> operands = new Stack<Double>();
            Stack<Character> functions = new Stack<Character>();
            nextChar(expression);
            while(ch != '\0') {
                eatSpace(expression);
                if(isFunc(prevCh) && isFunc(ch) && prevCh != ')' &&
                        (ch == '+' || ch == '-'))
                    if (prevCh == '/' || prevCh == '*'){
                        prevCh = ch;
                        nextChar(expression);
                        double num = scanOperand(expression);
                        if (prevCh == '-')
                            num *= -1;
                        operands.push(num);
                    }
                    else
                              operands.push(0.0);
                if (isOperand(ch)) {
                    prevCh = ch;
                    double num = scanOperand(expression);
                    operands.push(num);
                }
                if (isFunc(ch)) {
                    if (ch == ')' && !isFinalBrace)
                        bracesCount--;
                    if(ch =='('){
                        if(isFirstBrace)
                            isFirstBrace =false;
                        else
                            bracesCount++;
                    }

                    if(bracesCount < 0)
                        throw new ParsingException("Invalid num of braces");
                    if (functions.empty())
                        functions.push(ch);
                    else if (ch == ')') {
                        while (!functions.empty() && functions.peek() != '(')
                            popFunction(operands, functions);
                        functions.pop();
                    }
                    else {
                        while (canPop(ch, functions) && operands.size() > 1)
                            popFunction(operands, functions);
                        functions.push(ch);
                    }
                    prevCh = ch;
                    nextChar(expression);
                }
                else if (!Character.isSpaceChar(ch))
                    throw new ParsingException("invalid expression");
            }
            if(operands.size() != 1 || functions.size() > 0)
                throw new ParsingException("Invalid expression");
            return operands.pop();
        }
        catch(ParsingException e){
            throw new ParsingException("Invalid expression", e.getCause());
        }
    }

//    private Object getToken(String expression){
//        eatSpace(expression);
//        if (pos == expression.length())
//                return null;
//            if(Character.isDigit(expression.charAt(pos)))
//                return scanOperand(expression);
//            else
//                return expression.charAt(pos++)
//    }
    private void popFunction(Stack<Double> Operands, Stack<Character> Functions) throws ParsingException{
        double b = Operands.pop();
        double a = Operands.pop();
        switch (Functions.pop()){
            case '+':
                Operands.push(a+b);
                break;
            case '-':
                    Operands.push(a-b);
                break;
            case '*':
                Operands.push(a*b);
                break;
            case '/':
                    Operands.push(a/b);
                break;
        }

    }
    private boolean canPop(char op1, Stack<Character> func)throws ParsingException{
               int p1 = priority(op1);
               int p2 = priority(func.peek());
               return p1 >= 0 && p2 >= 0 && p1 >= p2;
    }
    private int priority(char c) throws ParsingException{
        switch (c) {
            case '*':
            case '/':
                return 1;

            case '-':
            case '+':
                return 2;

            case '(':
                return -1;
            case ')':
                return 0;
            default:
                throw new ParsingException("Invalid operation");
        }
    }

    private double scanOperand(String expression) throws ParsingException{
        String operand = "";
        do{
            operand += ch;
            nextChar(expression);
            if(Character.isLetter(ch))
                throw new ParsingException("Invalid arguments");
        } while (!Character.isSpaceChar(ch) && !isFunc(ch));
        return Double.parseDouble(operand);
    }
    private boolean isOperand(char ch){
        if(Character.isDigit(ch))
            return true;
        return false;
    }
    private boolean isFunc(char ch){
           if(ch == '+' || ch == '-' || ch =='*' || ch == '/' || ch == '%' || ch == '(' || ch ==')')
               return true;
        return false;
    }
    private void eatSpace(String expression){
        while (Character.isWhitespace(ch)  && ch != 'x')
            nextChar(expression);
    }
    private void nextChar(String expression){
        ch = (++pos < expression.length()) ? expression.charAt(pos) : '\0';
        if(pos == expression.length() - 1) isFinalBrace = true;
    }


}
