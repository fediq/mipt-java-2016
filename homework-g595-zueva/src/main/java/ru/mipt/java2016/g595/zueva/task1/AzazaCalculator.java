package ru.mipt.java2016.g595.zueva.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

class AzazaCalculator implements Calculator {

    static final AzazaCalculator INSTANCE = new AzazaCalculator();

    @Override
    public double calculate(String s) throws ParsingException {
        if (s == null) {
            throw new ParsingException("null");
        }
        if (s == ""){
            throw new ParsingException("empty string");
        }
        if (s == " "){
            throw new ParsingException("empty string");
        }
        for(int i = 1; i < s.length() - 1; ++i){
            if(s.charAt(i) == ' ' || s.charAt(i) == '\n' ||  s.charAt(i) == '\t'){
                s = s.substring(0, i - 1) + s.substring(i + 1, s.length() - 1);
            }
        }
        if(s.charAt(0) == ' ' || s.charAt(0) == '\n' ||  s.charAt(0) == '\t'){
            s = s.substring(1,s.length() - 1);
        }
        if(s.charAt(s.length() - 1) == ' ' || s.charAt(s.length() - 1) == '\n' ||  s.charAt(s.length()  - 1) == '\t'){
            s = s.substring(0, s.length() - 2);
        }
        for(int i = 0; i < s.length(); ++i) {
            if(!(s.charAt(i) == '+' || s.charAt(i) == '-' || s.charAt(i) == '*' ||s.charAt(i) == '/' || s.charAt(i) == '('
                    ||s.charAt(i) == ')'||s.charAt(i) == '.'||s.charAt(i) == '1' || s.charAt(i) == '2' || s.charAt(i) == '0' ||
                    s.charAt(i) == '3' || s.charAt(i) == '4' || s.charAt(i) == '5' || s.charAt(i) == '6' || s.charAt(i) == '7' ||
                    s.charAt(i) == '8' || s.charAt(i) == '9')){
                throw new ParsingException("Bad symbol");
            }
        }
        for(int i = 0; i < s.length() - 1; ++i){
            if(s.charAt(i) == '/' && s.charAt(i + 1) == '0') {
                return Double.POSITIVE_INFINITY;
            }
        }
        for(int i =0; i < s.length() - 2; ++i) {
            if(s.charAt(i) == '/' && s.charAt(i + 2) == '0' && s.charAt(i + 1) == '-') {
                return Double.NEGATIVE_INFINITY;
            }
        }
        s = to_polish(s);
        String buffer;
        double num_1 = 0;
        double num_2 = 0;
        Deque<Double> stack = new ArrayDeque<>();
        StringTokenizer tok = new StringTokenizer(s);
        while (tok.hasMoreTokens()) try {
            buffer = tok.nextToken().trim();
            if (oper(buffer.charAt(0)) && buffer.length() == 1) {
                num_1 = stack.pop();
                num_2 = stack.pop();
                switch (buffer.charAt(0)) {
                    case '+': {
                        num_2 = num_2 + num_1;
                        break;
                    }
                    case '-': {
                        num_2 = num_2 - num_1;
                        break;
                    }
                    case '*': {
                        num_2 = num_2 * num_1;
                        break;
                    }
                    case '/': {
                        num_2 = num_2 / num_1;
                        break;
                    }

                    default:
                        throw new Exception("\nIndefinite operator\n");
                }
                stack.push(num_2);
            } else {
                num_2 = Float.parseFloat(buffer);
                stack.push(num_2);
            }
        } catch (Exception error) {
            throw new ParsingException("\nIndefinite symbol\n");
        }


        return stack.pop();
    }

    public static boolean oper(char a) {
        if (a == '+' || a == '-' || a == '*' || a == '/') {
            return true;
        } else {
            return false;
        }
    }
    public static short priority(char a) {
        switch (a) {
            case '*':
                return 2;
            case '/':
                return 2;
            case '+':
                return 1;
            case '-':
                return 1;
            default:
                return -1;
        }
    }

    public static String to_polish(String input_string)  {

        char c;
        char t;
        StringBuilder buffer = new StringBuilder("");
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < input_string.length(); ++i) {
            c = input_string.charAt(i);
            if (oper(c)) {
                // System.out.println("@");
                while (buffer.length() > 0) {
                    t = buffer.substring(buffer.length() - 1).charAt(0);
                    if (oper(t) && (priority(c) <= priority(t))) {
                        output.append(" ").append(t).append(" ");
                        buffer.setLength(buffer.length() - 1);
                    } else {
                        output.append(" ");
                        break;
                    }
                }
                output.append(" ");
                buffer.append(c);
            } else if ('(' == c) {
                buffer.append(c);
            } else if (')' == c) {
                t = buffer.substring(buffer.length() - 1).charAt(0);
                while ('(' != t) {
                    // System.out.println("&");
                    output.append(" ").append(t);
                    buffer.setLength(buffer.length() - 1);
                    t = buffer.substring(buffer.length() - 1).charAt(0);
                }
                buffer.setLength(buffer.length() - 1);
            } else {

                output.append(c);
            }
        }

        while (buffer.length() > 0) {
            output.append(" ").append(buffer.substring(buffer.length() - 1));
            buffer.setLength(buffer.length() - 1);
        }

        return output.toString();
    }
}

