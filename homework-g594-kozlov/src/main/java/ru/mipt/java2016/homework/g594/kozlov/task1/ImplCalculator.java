package ru.mipt.java2016.homework.g594.kozlov.task1;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;

import static java.lang.Integer.max;

/**
 * Created by Anatoly on 09.10.2016.
 */
public class ImplCalculator implements Calculator {
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) throw new ParsingException("Null");

        String work = expression.replaceAll("[\\s]","");

        if (work.isEmpty()) throw new ParsingException("Empty String");

        for (int i = 0; i < work.length(); ++i){
            char c = work.charAt(i);
            if (!((('0' <= c) && (c <= '9')) || (c == '+') || (c == '-')
                    || (c == '*') || (c == '/') || (c == '(') || (c == ')') || (c == '.')))
                throw new ParsingException("Extra symbols");
        }

        String workSt = work.replaceAll("[+]-", "-").replaceAll("[-]-", "+")
                .replaceAll("[(]-","(!").replaceAll("/-", "/!");//.replaceAll("[*]-","*!");


        return calcSubstr(workSt, 0, workSt.length());
    }

    private double calcSubstr(String string, int from, int to) throws ParsingException {
        int nextBracket = string.indexOf('(', from);
        int nextBracketClose = string.indexOf(')', from);
        double bracketRes, prevRes = 1;
        int index = from;
        System.out.println(string.substring(from, to));

        if (nextBracket == -1 || nextBracket >= to)
            return noBracketCalc(string, from, to);
        if (nextBracketClose < nextBracket)
            throw new ParsingException("Bad bracket balance");

        int nextAdd = max(string.lastIndexOf('+', nextBracket),
                string.lastIndexOf('-', nextBracket));

        while (nextAdd < index && nextBracket < to) {
            int rightBracket = findPairBracket(string, nextBracket, to);
            bracketRes = calcSubstr(string, nextBracket + 1, rightBracket);
            if (nextBracket > index && string.charAt(nextBracket - 1) == '!'){
                nextBracket--;
                bracketRes = -bracketRes;
            }
            if (nextBracket > index){
                if (string.charAt(nextBracket - 1) == '*')
                    bracketRes = bracketRes * forwardCalc(string, index, nextBracket - 1);
                if (string.charAt(nextBracket - 1) == '/')
                    bracketRes = bracketRes / forwardCalc(string, index, nextBracket - 1);
            }

            if (index == from)
                prevRes = bracketRes;
            else {
                if (string.charAt(index - 1) == '*')
                    prevRes = bracketRes * prevRes;
                if (string.charAt(nextBracket - 1) == '/')
                    prevRes = prevRes / bracketRes;
            }
            index = rightBracket + 2;
            if (index > to) return prevRes;
            if (index == to) throw new ParsingException("Too much actions");
            nextBracket = string.indexOf('(', index);
            if (nextBracket == -1 || nextBracket > to){
                nextAdd = max(string.lastIndexOf('+', to - 1),
                        string.lastIndexOf('-', to - 1));
                break;
            }
            nextAdd = max(string.lastIndexOf('+', nextBracket),
                    string.lastIndexOf('-', nextBracket));
            if (string.charAt(index - 1) == '+' || string.charAt(index - 1) == '-')
                break;
        }

        if (index == from){
            if (string.charAt(nextAdd) == '+')
                return noBracketCalc(string, from, nextAdd) + calcSubstr(string, nextAdd + 1, to);
            if (string.charAt(nextAdd) == '-')
                return noBracketCalc(string, from, nextAdd) - calcSubstr(string, nextAdd + 1, to);
        }

        if (nextAdd < index && (nextBracket == -1 || nextBracket >= to)){
            if (string.charAt(index - 1) == '*')
                return prevRes * forwardCalc(string, index, to);
            if (string.charAt(index - 1) == '/')
                return prevRes / forwardCalc(string, index, to);
        }

        if (string.charAt(index - 1) == '*')
            prevRes = prevRes * forwardCalc(string, index, nextAdd);
        if (string.charAt(index - 1) == '/')
            prevRes = prevRes / forwardCalc(string, index, nextAdd);

        if (string.charAt(nextAdd) == '+')
            return prevRes + calcSubstr(string, nextAdd + 1, to);
        if (string.charAt(nextAdd) == '-')
            return prevRes  - calcSubstr(string, nextAdd + 1, to);
        return 0;
    }


    private int findPairBracket(String string, int leftBracket, int to) throws ParsingException {
        int balance = 1;
        int index = leftBracket + 1;
        while (balance != 0 && index < to){
            if (string.charAt(index) == '(')
                balance++;
            if (string.charAt(index) == ')')
                balance--;
            index++;
        }
        if (balance != 0)
            throw new ParsingException("Bad bracket balance");
        return index - 1;
    }


    private double noBracketCalc(String string, int from, int to) throws ParsingException {
        if (to - from < 1)
            throw new ParsingException("Empty Substr");

        System.out.println(string.substring(from, to));

        int firstAdd = max(string.lastIndexOf('+', to - 1),
                string.lastIndexOf('-', to - 1) );

        if (firstAdd <= from)
            return forwardCalc(string, from, to);

        double sufRes = forwardCalc(string, firstAdd + 1, to);
        double prefRes = noBracketCalc(string, from, firstAdd);

        if (string.charAt(firstAdd) == '+')
            return prefRes + sufRes;
        else
            return prefRes - sufRes;
    }

    private double bracketCalc(String string, int from, int to) throws ParsingException {
        if (to - from < 1)
            throw new ParsingException("Empty Substr");


        return 0;
    }

    private double forwardCalc(String string, int from, int to) throws ParsingException {
        if (to - from < 1)
            throw new ParsingException("Empty Substr");
        int sign = 1;
        if (string.charAt(from) == '!'){
            sign = -1;
            ++from;
        }
        int nextMult = string.indexOf('*', from);
        int nextDiv = string.indexOf('/', from);
        if (nextDiv == to - 1 || nextMult == to - 1 || nextDiv == from || nextMult == from)
            throw new ParsingException("Too much actions");

        System.out.println(string.substring(from, to));

        if ((nextDiv >= to || nextDiv == -1) && (nextMult >= to || nextMult == -1)) {
            return sign*Double.parseDouble(string.substring(from, to));
        }

        if (nextDiv >= to || nextDiv == -1){
            return sign*Double.parseDouble(string.substring(from, nextMult))
                    *forwardCalc(string, nextMult + 1, to);
        }

        if (nextMult >= to || nextMult == -1){
            return sign*Double.parseDouble(string.substring(from, nextDiv))/forwardCalc(string, nextDiv + 1, to);
        }

        if (nextMult < nextDiv){
            return sign*Double.parseDouble(string.substring(from, nextMult))
                    *forwardCalc(string, nextMult + 1, to);
        } else {
            return sign*Double.parseDouble(string.substring(from, nextDiv))
                    /forwardCalc(string, nextDiv + 1, to);
        }
    }
}
