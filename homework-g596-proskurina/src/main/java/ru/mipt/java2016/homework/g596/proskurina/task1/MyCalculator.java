package ru.mipt.java2016.homework.g596.proskurina.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;


import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.util.*;
import java.util.zip.Deflater;

import static java.lang.Double.parseDouble;


class MyCalculator implements Calculator{

    String Operators = "+_*/";

    String Digits = "0123456789.";
    @Override
    public double calculate(String expression) throws ParsingException {
        System.out.println(expression);
        return GettingValue(TransformToPolishNatation(StringToParts(expression)));

    }



private StringTokenizer StringToParts(String startingString ) throws ParsingException {

    if (startingString == null) {
        throw new ParsingException("Null");
    }
    startingString = startingString.replaceAll("[\\s]","");

    if (startingString.isEmpty())

        throw new ParsingException("Incorrect input");

    if (startingString.charAt(0) == '-') {

        startingString = '0' + startingString;
    }

    startingString = startingString.replaceAll("\\(-","(_").replaceAll("/-", "/_")
            .replaceAll("-","+_");
    System.out.println(startingString);
    StringTokenizer tokenizer = new StringTokenizer(startingString, Operators + '(' + ')'  , true);
    return tokenizer;

}


    Map<String,Integer> OperatorPriority = new HashMap<String,Integer>();

    {
        OperatorPriority.put("_", 2);
        OperatorPriority.put("/", 1);
        OperatorPriority.put("*", 1);
        OperatorPriority.put("+", 0);
    }

    Integer BracketsNum = 0;


private ArrayDeque<String> TransformToPolishNatation(StringTokenizer tokenizer) throws ParsingException {

    ArrayDeque<String> q = new ArrayDeque<String>();

    ArrayDeque<String>  StackOfOperators = new ArrayDeque<String>();
    while (tokenizer.hasMoreTokens()) {
        String token = tokenizer.nextToken();
        System.out.println(q);
        System.out.println(StackOfOperators);
        System.out.println(token);
        if (isNumber(token)) {
            q.push(token);
            continue;
        }

        if (isOperator(token)) {
            if (StackOfOperators.size() > 4 + BracketsNum) {
                throw new ParsingException("Incorrect input");
            }
            if (StackOfOperators.isEmpty()) {
                StackOfOperators.push(token);
                continue;
            }

            String firstOperator = StackOfOperators.peekFirst();

            if (!isOperator(firstOperator)) {
                StackOfOperators.push(token);
                continue;
            }
            if (OperatorPriority.get(firstOperator) < OperatorPriority.get(token))
                StackOfOperators.push(token);
            else {
                while (!StackOfOperators.isEmpty()
                        && OperatorPriority.get(StackOfOperators.peekFirst()) >= OperatorPriority.get(token)) {
                    q.push(StackOfOperators.peekFirst());
                    StackOfOperators.pop();
                }
                StackOfOperators.push(token);
                }

            continue;
        }
        if (isOpenBracket(token)){
            ++BracketsNum;
            StackOfOperators.push(token);
            continue;
        }

        if (isCloseBracket(token)){
            --BracketsNum;
            if (BracketsNum < 0) {
                throw new ParsingException("Incorrect input");
            }
            while (!isOpenBracket(StackOfOperators.peekFirst())) {
                q.push(StackOfOperators.peekFirst());
                StackOfOperators.pop();
            }
            StackOfOperators.pop();
            continue;
        }
        throw new ParsingException("Bad token");
    }
    while (!StackOfOperators.isEmpty()) {
        if (StackOfOperators.peekFirst().equalsIgnoreCase("(")) {
            throw new ParsingException("Incorrect input");
        }
        q.push(StackOfOperators.pop());
    }
    System.out.println(q);
    return q;
}







    private boolean isNumber ( String s){
    if (s.isEmpty()) {
        return false;
    }
    int i=0;
    int count = 0;
    while (i < s.length()) {
        if (s.charAt(i) == '.') {
            ++count;
        }
         if (!Digits.contains(s.substring(i, i + 1))) {
             return false;
         }

         ++i;

     }
     if (count > 1) {
         return false;
     }
    return true;
    }

    private boolean isOperator (String s) {
        if (Operators.contains(s)) {
            return true;
        }
        return false;
    }


    private boolean isOpenBracket (String s) {
        if (s.equalsIgnoreCase("(")) {
            return true;
        }
        return false;
    }
    private boolean isCloseBracket (String s) {
        if (s.equalsIgnoreCase(")")) {
            return true;
        }
        return false;
    }


    private double GettingValue(ArrayDeque<String> q) throws ParsingException {

        ArrayDeque<Double>  StackOfValues = new ArrayDeque<Double>();
        while (!q.isEmpty()){
            System.out.println(q);
            if (isNumber(q.peekLast())) {
                try {
                    StackOfValues.push(parseDouble(q.pollLast()));
                } catch (NumberFormatException error) {
                    throw new ParsingException("Incorrect input");
                }
                continue;
            }
            if (q.peekLast().equalsIgnoreCase("_")){
                q.pollLast();
                if (StackOfValues.isEmpty())
                    throw new ParsingException("Incorrect input");
                StackOfValues.push(-1 * StackOfValues.pop());
                continue;
            }
            if (isOperator(q.peekLast())) {
                if (StackOfValues.size() >= 2) {
                    StackOfValues.push(Calc(StackOfValues.pop(), StackOfValues.pop(), q.pollLast()));
                } else {
                    throw new ParsingException("Incorrect input");
                }
            }
        }
        if (StackOfValues.size() != 1)
            throw new ParsingException("Incorrect input");
        return StackOfValues.pollLast();
    }


    Double Calc(Double a,Double b, String Operat){
        if (Operat.equalsIgnoreCase("+"))
            return a + b;
        if (Operat.equalsIgnoreCase("*"))
            return a * b;
        if (Operat.equalsIgnoreCase("/"))
            return b / a;
        return 0.0;
    }
}