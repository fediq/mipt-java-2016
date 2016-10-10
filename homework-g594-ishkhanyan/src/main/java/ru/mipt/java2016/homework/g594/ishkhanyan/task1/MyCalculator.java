package ru.mipt.java2016.homework.g594.ishkhanyan.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


public class MyCalculator implements Calculator
{
    public MyCalculator(){};

    private boolean isDelim(char c){
        return c == ' '|| c=='\n' || c=='\t' ? true : false;
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

    public double getVal(String str) throws ParsingException
    {
        double result = 0;
        int points = 0;
        int pos = str.length();
        if(str.charAt(0)=='.' || str.charAt(str.length() - 1)=='.') {
            throw new ParsingException("Number error");
        }
        for(int i = 0; i < str.length(); ++i){
            if (str.charAt(i)=='.'){
                ++points;
                pos = i;
            }
        }
        if (points > 1)
        {
            throw new ParsingException("Number error");
        }
        for(int i =0 ;i<str.length();++i){
            if(pos > i){
                result+= (str.charAt(i)-'0')*Math.pow(10,(pos-i-1));
            }
            if(pos < i){
                result+= (str.charAt(i)-'0')*Math.pow(10,(pos-i));
            }
        }
        return result;
    }

    private void doOper(Stack<Double> numbers, char op){
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
        boolean unar_possib = true;//wait unary operation
        int bracket = 0;
        int brackbalnce = 0;
        if(exp==null || exp.length()==0)
            throw new ParsingException("empty expression");
        Stack<Double> numbers = new Stack<Double>();
        Stack<Character> oper = new Stack<Character>();
        for(int i = 0; i<exp.length(); ++i) {
            if (isDelim(exp.charAt(i))) {
                continue;
            }

            if(exp.charAt(i)=='('){
                oper.push(exp.charAt(i));
                bracket=i;
                unar_possib = true;
                ++brackbalnce;
                continue;
            }

            if(exp.charAt(i)==')'){
                if (bracket-i==1)
                    throw new ParsingException("empty brackets");
                if (brackbalnce == 0)                       //checking bracket balance
                    throw new ParsingException("balance error");
                while (oper.peek() != '('){
                    doOper(numbers, oper.pop());
                }
                oper.pop();
                unar_possib = false;
                --brackbalnce;
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
                while(!oper.empty() && !numbers.empty() && (priority(oper.peek())>=priority(currentOp)
                       ||  (currentOp=='m' || currentOp=='p') && priority(oper.peek())>priority(currentOp)))
                {
                    doOper(numbers, oper.pop());
                }
                oper.push(currentOp);
                unar_possib = true;
                /*switch (currentOp){
                    case '+':
                        unar_possib = false;
                        break;
                    case '-':
                        unar_possib = false;
                        break;
                    case '*':
                        unar_possib = true;
                        break;
                    case '/':
                        unar_possib = true;
                        break;
                }*/
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
                continue;
            }
            throw new ParsingException("Illegal symbol");
        }

        if(brackbalnce!=0) throw new ParsingException("balance error");//checking bracket balance
        if(numbers.empty()) throw new ParsingException("empty expression");//example:"     "
        while (!oper.empty()){
            doOper(numbers,oper.pop());
        }
        return numbers.peek();
    }
}
