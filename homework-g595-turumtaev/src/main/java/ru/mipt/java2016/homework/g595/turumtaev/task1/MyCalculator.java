package ru.mipt.java2016.homework.g595.turumtaev.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;

/**
 * калькулятор
 *
 * @author Galim Turumtaev
 * @since 10.10.2016

 */

public class MyCalculator implements Calculator {
    public double calculate(String expression) throws ParsingException{
        if (expression == null) {
            throw new ParsingException("Expression is null");
        }
        expression = getPostfixLine(expression);
        return calculateValue(expression);
    }

    private boolean isDigit(char letter){
        return (letter=='0' || letter=='1' || letter=='2' || letter=='3' || letter=='4' ||
                letter=='5' || letter=='6' || letter=='7' || letter=='8' || letter=='9');
    }

    private boolean isOperator(char letter){
        return (letter=='*' || letter=='/' || letter =='+' || letter =='-');
    }

    private int getPriority(char letter) throws ParsingException{
        int result=-1;
        if(letter=='(' || letter==')') result= 0;
        else if(letter=='+' || letter=='-') result= 1;
        else if(letter=='*' || letter=='/') result= 2;
        else if(letter=='~') result= 3;
        if(result==-1){
            throw new ParsingException("priority error");
        }
        return result;
    }

    private boolean isSpace(char letter){
        return (letter==' ' || letter== '\n' || letter=='\t');
    }

    private String getPostfixLine(String expression) throws ParsingException{
        String resultExpression="";
        Stack<Character> operators=new Stack<>();
        boolean isUnary;
        isUnary=true;
        for(int i=0;i<expression.length();i++) {
            char letter=expression.charAt(i);
            if(isOperator(letter)){
                resultExpression+=' ';
                if (isUnary){//если оператор унарный
                    if (letter=='+') {
                        isUnary = false;
                    } else if (letter=='-') {
                        operators.push('~');
                        isUnary = false;
                    } else {
                        throw new ParsingException("Invalid expression");
                    }
                }
                else {
                    isUnary = true;
                    while (!operators.empty()) {
                        char current = operators.pop();
                        if (getPriority(letter) <= getPriority(current)) {
                            resultExpression+=' ';
                            resultExpression+=current;
                            resultExpression+=' ';
                        } else {
                            operators.push(current);
                            break;
                        }
                    }
                    operators.push(letter); // Помещаем оператор в стек
                }
            }
            else if(letter=='('){
                operators.push(letter);
                isUnary=true;
            }
            else if(letter==')'){

                while(!operators.isEmpty() && operators.peek()!='(') {
                    resultExpression += ' ';
                    resultExpression += operators.pop();
                    resultExpression += ' ';
                }
                if(operators.isEmpty()){
                    throw new ParsingException("skobe balance error");
                }
                operators.pop();
                isUnary=false;
            }
            else if(isDigit(letter) || letter=='.'){
                resultExpression+=letter;
                isUnary=false;
            }
            else if(isSpace(letter)){
                continue;
            }
            else{
                throw new ParsingException("invalid expression");
            }
        }
        while (!operators.empty()) { // Выталкиваем оставшиеся элементы из стека
            char letter = operators.pop();
            if (isOperator(letter) || letter=='~') {
                resultExpression+=' ';
                resultExpression+=letter;
                resultExpression+=' ';
            } else {
                throw new ParsingException("Invalid expression");
            }
        }
        return resultExpression;
    }

    private double calculateValue(String expression) throws ParsingException{
        Stack<Double> numbers= new Stack<>();
        for(int i=0;i<expression.length();i++){
            char letter=expression.charAt(i);
            if(isOperator(letter) || letter=='~'){
                if(letter=='~'){
                    if (numbers.empty()){
                        throw new ParsingException("invalid expression");
                    }
                    double number=numbers.pop();
                    numbers.push(-1 * number);
                }
                else {
                    if (numbers.size() < 2) {
                        throw new ParsingException("invalid expression");
                    }
                    double firstNumber = numbers.pop();
                    double secondNumber = numbers.pop();
                    if (letter == '-') {
                        numbers.push(secondNumber - firstNumber);
                    } else if (letter == '+') {
                        numbers.push(secondNumber + firstNumber);
                    } else if (letter == '/') {
                        numbers.push(secondNumber / firstNumber);
                    } else if (letter == '*') {
                        numbers.push(secondNumber * firstNumber);
                    }
                }
            }
            if(isDigit(letter)){
                boolean isFloatingNumber=false;
                double result=charToInt(letter);
                while(isDigit(expression.charAt(i+1))){
                    i++;
                    result=result*10+charToInt(expression.charAt(i));
                }
                if(expression.charAt(i+1)=='.'){
                    i++;
                    isFloatingNumber=true;
                }
                double fracktion=1;
                while(isDigit(expression.charAt(i+1))){
                    i++;
                    fracktion*=0.1;
                    result=result+fracktion*charToInt(expression.charAt(i));
                }
                if(expression.charAt(i+1)=='.') {
                    throw new ParsingException("invalid floating number");
                }
                numbers.push(result);
            }
        }
        if (numbers.size() == 1) {
            return numbers.pop();
        } else {
            throw new ParsingException("Invalid expression");
        }
    }

    private int charToInt(char letter) throws ParsingException{
        switch (letter) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                throw new ParsingException("Invalid symbol");
        }
    }
}
