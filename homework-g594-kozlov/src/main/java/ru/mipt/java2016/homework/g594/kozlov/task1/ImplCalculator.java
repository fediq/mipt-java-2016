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
        if (expression == null) {
            throw new ParsingException("Null");
        }
        String work = expression.replaceAll("\\s", ""); //delete space symbols
        if (work.isEmpty()) {
            throw new ParsingException("Empty String");
        }
        for (int i = 0; i < work.length(); ++i) { //check extra symbols
            char c = work.charAt(i);
            if (!((('0' <= c) && (c <= '9')) || (c == '+') || (c == '-')
                    || (c == '*') || (c == '/') || (c == '(') || (c == ')') || (c == '.'))) {
                throw new ParsingException("Extra symbols");
            }
        }
        String workSt = work.replaceAll("[+]-", "-").replaceAll("[-]-", "+")
                .replaceAll("[(]-", "(!").replaceAll("/-", "/!").replaceAll("[*]-", "*!");
                //replace unary minuses with !
        if (workSt.charAt(0) == '-') {
            return -calcSubstr(workSt, 1, workSt.length());
        }
        return calcSubstr(workSt, 0, workSt.length());
    }

    //do action from string at index between too values
    private double combine(char operator, double val1, double val2) throws ParsingException {
        switch (operator) {
            case '*': return val1 * val2;
            case '/': return val1 / val2;
            case '+': return val1 + val2;
            case '-': return val1 - val2;
            default: throw new ParsingException("Parsing failed");
        }
    }

    //parsing substring to double, minding unary minus before value
    private double getVal(String string) throws ParsingException {
        if (string.length() == 0) {
            throw new ParsingException("Empty substring");
        }
        double value;
        int sign = 1;
        int index = 0;
        //checking unary minus
        if (string.charAt(index) == '!') {
            index++;
            sign = -1;
        }
        try {
            value = Double.parseDouble(string.substring(index));
        } catch (NumberFormatException excepted) {
            throw new ParsingException(excepted.getMessage());
        }
        return sign * value;
    }

    //function to calculate substring, that consists only of values and *,/
    private double forwardCalc(String string) throws ParsingException {
        if (string.length() == 0) {
            throw new ParsingException("Empty Substr");
        }
        double result = 1;
        double value;
        int index = 0;
        int to = string.length();
        int nextMult;
        int nextDiv;
        //we will go through string parsing one value and action before it
        while (index < to) {
            //searching index of next action
            nextMult = string.indexOf('*', index);
            nextDiv = string.indexOf('/', index);
            if (nextMult == -1 || (nextMult > nextDiv && nextDiv != -1)) {
                nextMult = nextDiv;
            }
            if (nextMult == -1) { //if there is no action, parse till the end
                nextMult = to;
            }
            value = getVal(string.substring(index, nextMult));
            if (index == 0) { //if it is first value without action before
                result = value;
            } else {
                result = combine(string.charAt(index - 1), result, value);
            }
            index = nextMult + 1; //move to the next value
        }
        return result;
    }

    private double calcSubstr(String string, int from, int to) throws ParsingException {
        int nextBracket = string.indexOf('(', from);
        int nextBracketClose = string.indexOf(')', from);
        double bracketRes;
        double prevRes = 1;
        int index = from;

        if (nextBracket == -1 || nextBracket >= to) { //if we have no brackets, we can do it easier
            return noBracketCalc(string.substring(from, to));
        }
        if (nextBracketClose < nextBracket) {
            throw new ParsingException("Bad bracket balance");
        }
        //check, if we had a + or - before first bracket
        int nextAdd = max(string.lastIndexOf('+', nextBracket),
                string.lastIndexOf('-', nextBracket));
        /*while we no + or -, we have multiplication of varios inbrackets expressions and values
         *we will calculate value before bracket and multiply it with bracket (or divide)
         * like a()b()c + d, we will calculate a()b() in while, then return a()b()c + calcSubstr(d)
         * */
        while (nextAdd < index && nextBracket < to) {
            //calculating inbracket expression, minding sign
            int rightBracket = findPairBracket(string, nextBracket, to);
            bracketRes = calcSubstr(string, nextBracket + 1, rightBracket);
            if (nextBracket > index && string.charAt(nextBracket - 1) == '!') {
                nextBracket--;
                bracketRes = -bracketRes;
            }
            if (nextBracket > index) { //if there is smth before bracket
                if (index == from) { //if it is start of substr
                    prevRes = forwardCalc(string.substring(index, nextBracket - 1));
                } else {
                    prevRes = combine(string.charAt(nextBracket - 1), prevRes,
                            forwardCalc(string.substring(index, nextBracket - 1)));
                }
                index = nextBracket;
            }

            if (index == from) { //combine previous result with inbracket value
                prevRes = bracketRes;
            } else {
                prevRes = combine(string.charAt(index - 1), prevRes, bracketRes);
            }

            index = rightBracket + 2; //cause invariant is starting with value
            if (index > to) {
                return prevRes; //string ended
            }
            if (index == to) {
                throw new ParsingException("Too much actions");
            }
                //if string ends with function symbol

            nextBracket = string.indexOf('(', index);
            nextAdd = max(string.lastIndexOf('+', nextBracket),
                    string.lastIndexOf('-', nextBracket));
            if (nextBracket == -1 || nextBracket > to) { //if there is no more brackets
                nextAdd = string.indexOf('+', index - 1);
                int nextMin = string.indexOf('-', index - 1);
                if (nextAdd == -1 || (nextMin != -1 && nextMin < nextAdd)) {
                    nextAdd = nextMin;
                }
                break;
            }

            if (string.charAt(index - 1) == '+' || string.charAt(index - 1) == '-') {
                break;  //if our multiply sequence ended right after bracket, but we index = rightBracket + 2
            }
        }

        if (index == from) { //if there is + or - before first bracket
            return combine(string.charAt(nextAdd), noBracketCalc(string.substring(from, nextAdd)),
                    calcSubstr(string, nextAdd + 1, to));
        }

        //if we exited while and there is no more + or -
        if (nextAdd < index && (nextBracket == -1 || nextBracket >= to)) {
            return combine(string.charAt(index - 1), prevRes, forwardCalc(string.substring(index, to)));
        }
        //if there is + or -, but not right after bracket
        if (string.charAt(index - 1) == '*' || string.charAt(index - 1) == '/') {
            prevRes = combine(string.charAt(index - 1), prevRes, forwardCalc(string.substring(index, nextAdd)));
        }

        return combine(string.charAt(nextAdd), prevRes, calcSubstr(string, nextAdd + 1, to));
    }

    //find right pair bracket for given
    private int findPairBracket(String string, int leftBracket, int to) throws ParsingException {
        int balance = 1;
        int index = leftBracket + 1;
        while (balance != 0 && index < to) { //going through the string and calculating balance
            if (string.charAt(index) == '(') {
                balance++;
            }
            if (string.charAt(index) == ')') {
                balance--;
            }
            index++;
        }
        if (balance != 0) {
            throw new ParsingException("Bad bracket balance");
        }
        return index - 1;
    }

    //calculate substring without brackets
    private double noBracketCalc(String string) throws ParsingException {
        if (string.length() == 0) {
            throw new ParsingException("Empty Substr");
        }
        int to = string.length();
        //searching last + or - in substring
        int lastAdd = max(string.lastIndexOf('+', to - 1),
                string.lastIndexOf('-', to - 1));

        if (lastAdd <= 0) { //if there is no + or -
            return forwardCalc(string.substring(0, to));
        }

        double sufRes = forwardCalc(string.substring(lastAdd + 1, to));
        double prefRes = noBracketCalc(string.substring(0, lastAdd));

        return combine(string.charAt(lastAdd), prefRes, sufRes);
    }
}
