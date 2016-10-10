package ru.miptr.java2016.homework.g594.pyrkin.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.Stack;

/**
 * 2-stack Calculator
 * Created by randan on 10/9/16.
 */
public class CalculatorImplementation implements Calculator{
    private Stack<Double> numbers = new Stack<>();
    private Stack<Character> symbols = new Stack<>();
    private double currentInteger;
    private double currentDecimal;
    private boolean point;
    private boolean number;

    private boolean badSymbolsCheck(String expression){
        for(char symbol : expression.toCharArray())
            if(!Character.isDigit(symbol) && symbol != '.' && symbol != '(' && symbol != ')'
                && symbol != '+' && symbol != '-' && symbol != '*' && symbol !='/')
                return true;
        return false;
    }

    private boolean bracketsCheck(String expression){
        int balance = 0;
        char previousSymbol = '#';
        for(char symbol : expression.toCharArray()){
            if(symbol == '(')
                ++balance;
            else if(symbol == ')') {
                --balance;
                if(previousSymbol == '(')
                    return true;
            }

            if(balance < 0)
                return true;

            previousSymbol = symbol;
        }
        return balance != 0;
    }

    private int getPriority(char operand){
        if(operand == '+' || operand == '-')
            return 1;
        if(operand == '*' || operand == '/')
            return 2;
        if(operand == 'p' || operand == 'n')
            return 3;
        return 0;
    }

    private void addNumber(){
        numbers.push(currentInteger + currentDecimal);
        currentInteger = 0;
        currentDecimal = 0;
        number = false;
        point = false;
    }

    private void addOperand(char operand, char previousSymbol){
        if(symbols.empty() || getPriority(operand) != 1){
            symbols.push(operand);
            return;
        }

        if(previousSymbol == '+' || previousSymbol == '-' ||
           previousSymbol == 'p' || previousSymbol =='n'){
            if(operand == '-') {
                symbols.pop();
                switch (previousSymbol) {
                    case '+':
                        symbols.push('-');
                        break;
                    case '-':
                        symbols.push('+');
                        break;
                    case 'p':
                        symbols.push('n');
                        break;
                    case 'n':
                        symbols.push('p');
                }
            }
        }else
            symbols.push(operand);
    }


    private void makeOperation(char operand){
        double lastNumber = numbers.pop();
        double previousNumber = 0;
        if(getPriority(operand) < 3)
            previousNumber = numbers.pop();
        switch (operand) {
            case '+':
                numbers.push(previousNumber + lastNumber);
                break;
            case '-':
                numbers.push(previousNumber - lastNumber);
                break;
            case '*':
                numbers.push(previousNumber * lastNumber);
                break;
            case '/':
                numbers.push(previousNumber / lastNumber);
                break;
            case 'p':
                numbers.push(lastNumber);
                break;
            case 'n':
                numbers.push(-lastNumber);
        }
    }

    private void expandBrackets(){
        while (symbols.peek() != '(')
            makeOperation(symbols.pop());
        symbols.pop();
    }

    private void expandStack(char operand){
        while(!symbols.empty() &&
              getPriority(symbols.peek()) >= getPriority(operand))
            makeOperation(symbols.pop());
    }

    private char declSymbol(char symbol, char previousSymbol){
        if(symbol != '+' && symbol != '-')
            return symbol;
        if(!Character.isDigit(previousSymbol) && previousSymbol != ')'){
            if(symbol == '+')
                return 'p';
            return 'n';
        }
        return symbol;
    }

    private double getResult(String expression) throws ParsingException{
        currentInteger = 0;
        currentDecimal = 0;
        point = false;
        number = false;
        char previousSymbol = 'x';
        for(char symbol : expression.toCharArray()){
            if(Character.isDigit(symbol)) {
                number = true;
                if (point)
                    currentDecimal += (double) Character.getNumericValue(symbol) / 10;
                else
                    currentInteger = currentInteger * 10 + Character.getNumericValue(symbol);
            }else if(symbol == '.') {
                if (point || !Character.isDigit(previousSymbol))
                    throw new ParsingException("Invalid expression");
                point = true;
            }else {
                if(number)
                    addNumber();
                if(symbol == '(')
                    symbols.push(symbol);
                else if (symbol == ')')
                    expandBrackets();
                else {
                    symbol = declSymbol(symbol, previousSymbol);
                    expandStack(symbol);
                    addOperand(symbol, previousSymbol);
                }
            }
            previousSymbol = symbol;
        }
        if(number)
            numbers.push(currentInteger + currentDecimal);
        expandStack('#');
        return numbers.peek();
    }

    @Override
    public double calculate (String expression) throws ParsingException{
        if(expression == null)
            throw  new ParsingException("Null expression");
        expression = expression.replaceAll("[\\s]", "");
        if(expression.isEmpty())
            throw new ParsingException("Invalid expression");
        if(badSymbolsCheck(expression))
            throw  new ParsingException("Invalid expression");
        if(bracketsCheck(expression))
            throw  new ParsingException("Invalid expression");
      //  System.out.print(expression + "\n");
        return getResult(expression);
    }

//    public static void main(String[] args) throws ParsingException{
//        String s = " (6.0  ) + \t( - 4) * (  0.0 +\n 5/2)";
//        CalculatorImplementation calc = new CalculatorImplementation();
//        System.out.print(calc.calculate(s));
//    }
}


