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

        s = topolish(s);
        String buffer;
        double num_1 = 0;
        double num_2 = 0;
        Deque<Double> stack = new ArrayDeque<>();
        StringTokenizer tok = new StringTokenizer(s);
        while (tok.hasMoreTokens()) try {
            buffer = tok.nextToken().trim();
            if (oper(buffer.charAt(0)) && buffer.length() == 1) {
                System.out.println(buffer.charAt(0));
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

    public static String topolish(String input_string)  {

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

