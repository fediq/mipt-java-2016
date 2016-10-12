package ru.mipt.java2016.homework.g595.popovkin.task1;

import ru.mipt.java2016.homework.base.task1.*;

import java.util.*;
/**
 * Created by Howl on 11.10.2016.
 */
public class MyCalculator  implements ru.mipt.java2016.homework.base.task1.Calculator {
    //private Map<Character, Integer> priority;
    private static final Character[] SET_VALUES = new Character[]{ '(', ')', '*', '/', '+', '-' };
    private static final Set<Character> aloneSymbolLexems = new HashSet<Character>(Arrays.asList(SET_VALUES));
    private List<LexicalUnit> lexicalUnits;
    private static final String UNARY_MINUS = "M";

    // find last math "sign" outside any bracers, -1 if no such symbols, checks bracers balance
    private int get_opened_math_sign(String sign, int leftId, int rightId) throws ParsingException{
        //System.out.print(leftId);
        //System.out.println(rightId);
        int balance = 0;
        for(int i = rightId - 1; i >= leftId; --i){
            if(lexicalUnits.get(i).isCloseBracer()) ++balance;
            else if(lexicalUnits.get(i).isOpenBracer()) --balance;
            else if(lexicalUnits.get(i).isMathSign() && balance == 0 && sign.equals(lexicalUnits.get(i).value)){
                return i;
            }
            if(balance < 0) throw new ParsingException("wrong number of bracers");
        }
        if(balance != 0) throw new ParsingException("wrong number of bracers");
        return -1;
    }

    private double parse_and_calc(int leftId, int rightId) throws ParsingException{
        if(leftId == rightId) throw new ParsingException("stops on parsing empty expression");
        if(leftId == rightId - 1){
            if(lexicalUnits.get(leftId).isDouble()) return lexicalUnits.get(leftId).getDoubleValue();
            throw new ParsingException("stops on parsing not double one token expression");
        }
        int id;
        String operation = "err";
        if((id = get_opened_math_sign("+", leftId, rightId)) != -1){
            operation = "+";
        }else if((id = get_opened_math_sign("-", leftId, rightId)) != -1){
            operation = "-";
        }else if((id = get_opened_math_sign("*", leftId, rightId)) != -1){
            operation = "*";
        }else if((id = get_opened_math_sign("/", leftId, rightId)) != -1){
            operation = "/";
        }
        if(id != -1){
            double left_operand = parse_and_calc(leftId, id);
            double right_operand = parse_and_calc(id + 1, rightId);
            if(operation.equals("+")) return left_operand + right_operand;
            else if(operation.equals("-")) return left_operand - right_operand;
            else if(operation.equals("*")) return left_operand * right_operand;
            else return left_operand / right_operand;
        }
        if(lexicalUnits.get(leftId).isOpenBracer() && lexicalUnits.get(rightId - 1).isCloseBracer())
            return parse_and_calc(leftId + 1, rightId - 1);
        if(lexicalUnits.get(leftId).isMathSign() && lexicalUnits.get(leftId).value.equals(UNARY_MINUS)){
            return -parse_and_calc(leftId + 1, rightId);
        }
        throw new ParsingException("stops on parsing impossible expression");
    }

    private List<LexicalUnit> parse_to_lexical_units(String expression) throws ParsingException{
        List<LexicalUnit> answer = new ArrayList<LexicalUnit>();
        int rightPointerToExp;
        //System.out.print(aloneSymbolLexems.toString());
        for(int i = 0; i < expression.length(); i = rightPointerToExp){
            rightPointerToExp = i + 1;
            if(aloneSymbolLexems.contains(expression.charAt(i))){
                answer.add(new LexicalUnit(expression.substring(i, i + 1)));
                continue;
            }
            while(rightPointerToExp < expression.length() && !aloneSymbolLexems.contains(expression.charAt(rightPointerToExp))){
                ++rightPointerToExp;
            }
            //System.out.format("%d %d\n", i, rightPointerToExp);
            answer.add(new LexicalUnit(expression.substring(i, rightPointerToExp)));
        }
        return answer;
    }

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        expression = expression.replaceAll("\\s","");
        lexicalUnits = parse_to_lexical_units(expression);
        // finding unary minuses
        for(int i = 0; i < lexicalUnits.size(); ++i)
            if(lexicalUnits.get(i).isMathSign() && lexicalUnits.get(i).value.equals("-") &&
                    (i == 0 || lexicalUnits.get(i - 1).isMathSign() || lexicalUnits.get(i - 1).isOpenBracer()))
                lexicalUnits.get(i).value = UNARY_MINUS;
        //System.out.format("%s\n", lexicalUnits.toString());
        //for(int i = 0; i < lexicalUnits.size(); ++i)
        //    System.out.format("%s\n", lexicalUnits.get(i).value);
        return parse_and_calc(0, lexicalUnits.size());
    }

    public MyCalculator(){}
}