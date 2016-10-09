package ru.miptr.java2016.homework.g594.pyrkin.task1;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import com.sun.org.apache.xerces.internal.impl.xs.identity.Selector;
import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.regex.Pattern;

/**
 * 2-stack Calculator
 * Created by randan on 10/9/16.
 */
public class CalculatorImplementation implements Calculator{
    private boolean badSymbolsCheck(String expression){
        for(char symbol : expression.toCharArray())
            if(!Character.isDigit(symbol) && symbol != '.' && symbol != '(' && symbol != ')'
                && symbol != '+' && symbol != '-' && symbol != '*' && symbol !='/')
                return false;
        return true;
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
                    return false;
            }

            if(balance < 0)
                return false;

            previousSymbol = symbol;
        }
        return balance == 0;
    }

    private String removeExtraOperands(String expression){
        Pattern pattern = Pattern.compile("([\\d)])\\+*-(\\+*-\\+*-)*\\+*([\\d(])");
        Matcher matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("$1-$3");

        pattern = Pattern.compile("([\\d)])(\\+*-\\+*-)*\\+*--(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("$1+$4");

        pattern = Pattern.compile("([\\d)])(\\+*-\\+*-)*\\+(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("$1+$4");

        pattern = Pattern.compile("\\(\\+*-(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("(-$2");

        pattern = Pattern.compile("\\((\\+*-\\+*-)*\\+*--(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("($3");

        pattern = Pattern.compile("\\((\\+*-\\+*-)*\\+(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("($3");

        pattern = Pattern.compile("^\\+*-(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("-$2");

        pattern = Pattern.compile("^(\\+*-\\+*-)*\\+*--(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("$3");

        pattern = Pattern.compile("^(\\+*-\\+*-)*\\+(\\+*-\\+*-)*\\+*([\\d(])");
        matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("$3");

        return expression;
    }

    private boolean checkBadOperands(String expression){
        char [] expressionCharArray = expression.toCharArray();
        if(expressionCharArray[0] == '*' || expressionCharArray[0] == '/')
            return false;
        for(int i = 1; i < expressionCharArray.length; ++i)
            if(!Character.isDigit(expressionCharArray[i]) &&
               expressionCharArray[i] != '(' && expressionCharArray[i] != ')' &&
               expressionCharArray[i - 1] == expressionCharArray[i])
                return false;
        return true;
    }

    @Override
    public double calculate (String expression) throws ParsingException{
        if(expression == null)
            throw  new ParsingException("Null expression");
        expression = expression.replaceAll(" ", "");
        expression = expression.replaceAll("\n", "");
        if(expression.isEmpty())
            throw new ParsingException("Invalid expression");
        if(!badSymbolsCheck(expression))
            throw  new ParsingException("Invalid expression");
        if(!bracketsCheck(expression))
            throw  new ParsingException("Invalid expression");
        expression = removeExtraOperands(expression);


        return 0;
    }

    public static void main(String[] args) throws ParsingException{
        String s = "-++-+-46-+-+++-(-+23----+4688---+-46-(--68-46)-5)-56-(--+-5)";
        CalculatorImplementation calc = new CalculatorImplementation();
        calc.calculate(s);

    }
}


