package ru.mipt.java2016.homework.g594.ishkhanyan.task1;


import com.sun.javafx.fxml.expression.Expression;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.util.Vector;
import java.util.Stack;


public class MyCalculator implements Calculator
{
    public MyCalculator(){};

    private boolean isDelim(char c){
        return c != ' ' ? false : true;
    }

    private boolean isOper(char c){
        return c == '+' || c == '-' || c == '*' || c =='/' ? true : false;
    }

    private boolean isDigit(char c){
        return (c>='0' && c<='9' || c=='.') ? true : false;
    }

    private int priority(char op){
        switch(op){
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            case 'm':
                return 3;
            case 'p':
                return 3;
            default: return -1;
        }
    }

    private double getVal(String str) throws ParsingException
    {
        double result = 0;
        int noDigit = 0;
        int pos = str.length();
        if(str.charAt(0)=='.' || str.charAt(str.length() - 1)=='.') {
            throw new ParsingException("Number error");
        }
        for(int i = 0; i < str.length(); ++i){
            if (str.charAt(i)=='.'){
                noDigit += 0;
                pos = i;
            }
        }
        if (noDigit > 1)
        {
            throw new ParsingException("Number error");
        }
        for(int i =0 ;i<str.length();++i){
            result+= Math.pow((str.charAt(i)-'0'),(pos-i));
        }
        return result;
    }

    private void do_oper(Stack<Double> numbers, char op){
        if(op=='m' || op == 'p') {
            double l = numbers.pop();
            switch (op) {
                case 'p':
                    numbers.push(l);
                    break;
                case 'm':
                    numbers.push(-l);

            }
        }
        else {
            double r = numbers.pop();
            double l = numbers.pop();
            switch(op){
                case '+':
                    numbers.push(r+l);
                    break;
                case '-':
                    numbers.push(l-r);
                    break;
                case '*':
                    numbers.push(r*l);
                    break;
                case '/':
                    numbers.push(l/r);
                    break;
            }
        }
    }

    public double calculate(String exp) throws ParsingException {
        boolean unar_possib = true;
        Stack<Double> numbers = new Stack<Double>();
        Stack<Character> oper = new Stack<Character>();
        for(int i = 0; i<exp.length(); ++i) {
            if (isDelim(exp.charAt(i))) {
                continue;
            }

            if(exp.charAt(i)=='('){
                oper.push(exp.charAt(i));
                unar_possib = true;
                continue;
            }

            if(exp.charAt(i)==')'){
                while (oper.peek() != '('){
                    do_oper(numbers, oper.pop());
                }
                oper.pop();
                unar_possib = false;
                continue;
            }

            if(isOper(exp.charAt(i))){
                char currentOp = exp.charAt(i);
                if(unar_possib){
                    switch(currentOp){
                        case '+':
                            currentOp = 'p';//unaryPlus
                            break;
                        case '-':
                            currentOp = 'm';//unaryMinus
                            break;
                        default:
                            throw new ParsingException("Illegal sequence");
                    }
                }
                while(!oper.empty() && (priority(oper.peek())>=priority(currentOp)
                       ||  (currentOp=='m' || currentOp=='p') && priority(oper.peek())>priority(currentOp)))
                {
                    do_oper(numbers, oper.pop());
                }
                oper.push(currentOp);
                unar_possib = false;
                continue;
            }
            if(isDigit(exp.charAt(i))){
                String num = new String();
                num += exp.charAt(i++);
                while(i < exp.length() && isDigit(exp.charAt(i)))
                {
                    num += exp.charAt(i++);
                }
                --i;
                numbers.push(getVal(num));
                unar_possib = false;
            }
        }
        while (!oper.empty()){
            do_oper(numbers,oper.pop());
        }
        return numbers.peek();
    }
}
