package my_calc;

/**
 * Created by Nadya Zueva
 * при написании использована статья на e-maxx
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;
import java.util.*;
import java.io.*;
import java.util.Iterator;

public interface Calculator {
    public static void main(String[] args) throws Exception {
        System.out.println("Enter string:\n");
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String string;

        try {
            string = read.readLine();
            string = to_polish(string);
        //    System.out.println(string);
            System.out.println(count(string));
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }

    public static float count(String s) throws Exception {
        String buffer;
        float num_1 = 0;
        float num_2 = 0;

        //Stack<Float> stack = new ArrayStack<Float>();
        Deque<Float> stack = new ArrayDeque<Float>();
        StringTokenizer tok = new StringTokenizer(s);
        while (tok.hasMoreTokens()) {
            try {
                buffer = tok.nextToken().trim();
                if (oper(buffer.charAt(0))&& buffer.length() == 1) {
                    num_1 = stack.pop(); num_2 = stack.pop();
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
                            num_2 = (Float)num_2 / num_1;
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
                throw new Exception("\nIndefinite symbol\n");
            }
        }


        return stack.pop();
    }


    //является ли символ оператором
    public static boolean oper(char a) {
        if (a == '+' || a == '-' || a == '*' || a == '/') {
            return true;
        } else {
            return false;
        }
    }

        /*является ли символ скобкой
        public static boolean brack(char a) {
            if (a == '(' || a == ')') {
                return true;
            }
            else {
                return false;
            }
        }*/
//является ли числом
       /* public static boolean numb(char a) {
            switch (a) {
                case '0': {
                    return true;
                }
                case '1': {
                    return true;
                }
                case '2': {
                    return true;
                }
                case '3': {
                    return true;
                }
                case '4': {
                    return true;
                }
                case '5': {
                    return true;
                }
                case '6': {
                    return true;
                }
                case '7': {
                    return true;
                }
                case '8': {
                    return true;
                }
                case '9': {
                    return true;
                }
            }
            return false;
        }*/

    //приоритет операции
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

    public static String to_polish(String input_string) throws Exception {

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

