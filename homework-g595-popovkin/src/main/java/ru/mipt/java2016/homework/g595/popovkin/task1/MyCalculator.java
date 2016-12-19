package ru.mipt.java2016.homework.g595.popovkin.task1;

import ru.mipt.java2016.homework.base.task1.*;

import javax.naming.LimitExceededException;
import java.util.*;

/**
 * Created by Howl on 11.10.2016.
 */

public class MyCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {
    //private Map<Character, Integer> priority;
    private static final Character[] SET_VALUES = new Character[]{'(', ')', '*', '/', '+', '-', ',' };
    private static final Set<Character> ALONE_SYMBOL_LEXEMS = new HashSet<Character>(Arrays.asList(SET_VALUES));
    private List<LexicalUnit> lexicalUnits;
    private static final String UNARY_MINUS = "M";

    // find last math "sign" outside any bracers, -1 if no such symbols, checks bracers balance
    private int getOpenedMathSign(String sign, int leftId, int rightId) throws ParsingException {
        //System.out.print(leftId);
        //System.out.println(rightId);
        int balance = 0;
        for (int i = rightId - 1; i >= leftId; --i) {
            if (lexicalUnits.get(i).isCloseBracer()) {
                ++balance;
            } else if (lexicalUnits.get(i).isOpenBracer()) {
                --balance;
            } else if (lexicalUnits.get(i).isMathSign() && balance == 0
                    && sign.equals(lexicalUnits.get(i).getValue())) {
                return i;
            }
            //System.out.println(balance);
            if (balance < 0) {
                throw new ParsingException("wrong number of bracers");
            }
        }
        if (balance != 0) {
            throw new ParsingException("wrong number of bracers");
        }
        return -1;
    }

    private double parceAndCalc(int leftId, int rightId) throws ParsingException {
        if (leftId == rightId) {
            throw new ParsingException("stops on parsing empty expression");
        }
        if (leftId == rightId - 1) {
            if (lexicalUnits.get(leftId).isDouble()) {
                return lexicalUnits.get(leftId).getDoubleValue();
            }
            throw new ParsingException("stops on parsing not double one token expression");
        }
        int id = -1;
        // staying "err" means, what something has broken
        String operation = "err";
        if (id == -1) {
            id = getOpenedMathSign("+", leftId, rightId);
            if (id != -1) {
                operation = "+";
            }
        }
        if (id == -1) {
            id = getOpenedMathSign("-", leftId, rightId);
            if (id != -1) {
                operation = "-";
            }
        }
        if (id == -1) {
            id = getOpenedMathSign("*", leftId, rightId);
            if (id != -1) {
                operation = "*";
            }
        }
        if (id == -1) {
            id = getOpenedMathSign("/", leftId, rightId);
            if (id != -1) {
                operation = "/";
            }
        }
        if (id == -1) {
            //System.out.println("!");
            LexicalUnit unit = lexicalUnits.get(leftId);
            LexicalUnit open = lexicalUnits.get(leftId + 1);
            LexicalUnit close = lexicalUnits.get(rightId - 1);
            if (unit.isFunc() && open.isOpenBracer() && close.isCloseBracer()) {
                //int argc = 0;
                List<Double> argv = new ArrayList<>();
                int commaScaner = leftId + 2;
                int lastPoint = leftId + 2;
                int balance = 0;
                while (commaScaner < rightId - 1) {
                    if (lexicalUnits.get(commaScaner).isOpenBracer()) ++balance;
                    else if (lexicalUnits.get(commaScaner).isCloseBracer()) --balance;
                    else if (lexicalUnits.get(commaScaner).isComma() && balance == 0) {
                        //System.out.print(lastPoint);
                        //System.out.println(commaScaner);
                        argv.add(parceAndCalc(lastPoint, commaScaner));
                        lastPoint = commaScaner + 1;
                    }
                    ++commaScaner;
                }
                if (lastPoint != commaScaner)
                    argv.add(parceAndCalc(lastPoint, commaScaner));
                return unit.eval(argv);
            }
        }
        if (id != -1) {
            double leftOperand = parceAndCalc(leftId, id);
            double rightOperand = parceAndCalc(id + 1, rightId);
            if (operation.equals("+")) {
                return leftOperand + rightOperand;
            } else if (operation.equals("-")) {
                return leftOperand - rightOperand;
            } else if (operation.equals("*")) {
                return leftOperand * rightOperand;
            } else {
                return leftOperand / rightOperand;
            }
        }
        if (lexicalUnits.get(leftId).isOpenBracer() && lexicalUnits.get(rightId - 1).isCloseBracer()) {
            return parceAndCalc(leftId + 1, rightId - 1);
        }
        if (lexicalUnits.get(leftId).isMathSign() && lexicalUnits.get(leftId).getValue().equals(UNARY_MINUS)) {
            return -parceAndCalc(leftId + 1, rightId);
        }
        throw new ParsingException("stops on parsing impossible expression");
    }

    private List<LexicalUnit> parseToLexicalUnits(String expression) throws ParsingException {
        List<LexicalUnit> answer = new ArrayList<LexicalUnit>();
        int rightPointerToExp;
        //System.out.print(ALONE_SYMBOL_LEXEMS.toString());
        for (int i = 0; i < expression.length(); i = rightPointerToExp) {
            rightPointerToExp = i + 1;
            if (ALONE_SYMBOL_LEXEMS.contains(expression.charAt(i))) {
                answer.add(new LexicalUnit(expression.substring(i, i + 1)));
                continue;
            }
            while (rightPointerToExp < expression.length() &&
                    !ALONE_SYMBOL_LEXEMS.contains(expression.charAt(rightPointerToExp))) {
                ++rightPointerToExp;
            }
            //System.out.format("%d %d\n", i, rightPointerToExp);
            answer.add(new LexicalUnit(expression.substring(i, rightPointerToExp)));
        }
        return answer;
    }

    public double calculate(String expression) throws ParsingException {
        //System.out.println(expression);
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        expression = expression.replaceAll("\\s", "");
        lexicalUnits = parseToLexicalUnits(expression);
        // finding unary minuses
        for (int i = 0; i < lexicalUnits.size(); ++i) {
            if (lexicalUnits.get(i).isMathSign() && lexicalUnits.get(i).getValue().equals("-") &&
                    (i == 0 || lexicalUnits.get(i - 1).isMathSign() || lexicalUnits.get(i - 1).isOpenBracer())) {
                lexicalUnits.get(i).setValue(UNARY_MINUS);
            }
        }
        //System.out.format("%s\n", lexicalUnits.toString());
        //for(int i = 0; i < lexicalUnits.size(); ++i)
        //    System.out.format("%s\n", lexicalUnits.get(i).value);
        return parceAndCalc(0, lexicalUnits.size());
    }

    public MyCalculator() { }
}