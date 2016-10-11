package ru.mipt.java2016.homework.g594.ishkhanyan.task1;


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;


class MyCalculator implements Calculator
{
    MyCalculator(){}

    private boolean isDelim(char c){ //check that the symbol is delimiter
        return c == ' ' || c == '\n' || c == '\t';
    }

    private boolean isOper(char c){ // check that the symbol is operator
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private boolean isDigit(char c){ //check that the symbol is digit or point
        return (c >= '0' && c <= '9' || c == '.');
    }

    private int priority(char op){ // return priority of operation
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

    private double getVal(String str) throws ParsingException // convert string >> double
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

    private void doOper(Stack<Double> numbers, char op){ //do 1 operation from stack
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
        int numberAfterOp = 0; //how many numbers were after last operation
        int brackbalnce = 0;
        if(exp==null || exp.length()==0)
            throw new ParsingException("empty expression");
        Stack<Double> numbers = new Stack<>();
        Stack<Character> oper = new Stack<>();
        for(int i = 0; i<exp.length(); ++i) {
            if (isDelim(exp.charAt(i))) {
                continue;
            }

            if(exp.charAt(i)=='('){
                oper.push(exp.charAt(i));
                numberAfterOp=0;
                unar_possib = true;
                ++brackbalnce;
                continue;
            }

            if(exp.charAt(i)==')'){
                if (numberAfterOp == 0) // check that operation is not last meaning symbol in brackets
                    throw new ParsingException("illegal expression");
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
                }else if(numberAfterOp == 0) //check that we have not two unary operations
                {
                    throw new ParsingException("double operation");
                }
                while(!oper.empty() && !numbers.empty() && (priority(oper.peek())>=priority(currentOp)
                       ||  (currentOp=='m' || currentOp=='p') && priority(oper.peek())>priority(currentOp)))
                {
                    doOper(numbers, oper.pop());
                }
                oper.push(currentOp);
                switch (currentOp){
                    case '+':
                        unar_possib = false;
                        break;
                    case '-':
                        unar_possib = false;
                        break;
                    case 'p':
                        unar_possib = false;
                        break;
                    case 'm':
                        unar_possib = false;
                        break;

                    case '*':
                        unar_possib = true;
                        break;
                    case '/':
                        unar_possib = true;
                        break;
                }
                numberAfterOp = 0;
                continue;
            }
            if(isDigit(exp.charAt(i))){
                String num = "";
                num += exp.charAt(i++);
                while(i < exp.length() && isDigit(exp.charAt(i)))
                {
                    num += exp.charAt(i++);
                }
                --i;
                numbers.push(getVal(num));
                unar_possib = false;
                numberAfterOp = 1;
                continue;
            }
            throw new ParsingException("Illegal symbol");// if we did not meet familiar symbol
        }

        if(brackbalnce!=0) throw new ParsingException("balance error");//checking bracket balance
        if(numbers.size()!=1) throw new ParsingException("illegal expression");//check illegal expression
        while (!oper.empty()){
            doOper(numbers,oper.pop());
        }
        return numbers.peek();
    }
}
