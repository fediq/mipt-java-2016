package ru.mipt.java2016.homework.g595.belyh.task4;

import javafx.util.Pair;

import java.util.*;

/**
 * Created by white2302 on 16.12.2016.
 */

import ru.mipt.java2016.homework.base.task1.ParsingException;


public class Calculator {
    private HashMap<String, Function> map = new HashMap<>();
    private HashMap<String, Integer> s = new HashMap<>();
    private HashMap<String, Double> variable = new HashMap<>();

    public Calculator() {
        s.put("sin", 1);
        s.put("cos", 1);
        s.put("tg", 1);
        s.put("sqrt", 1);
        s.put("pow", 2);
        s.put("abs", 1);
        s.put("sign", 1);
        s.put("log", 2);
        s.put("log2", 1);
        s.put("rnd", 0);
        s.put("max", 2);
        s.put("min", 2);
    }

    private String expr;
    private int pos;
    private char c;

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("String is empty");
        }

        expr = expression;
        pos = 0;
        double ans = getSum();
        if (c != '\n') {
            throw new ParsingException("Incorrect string");
        }

        return ans;
    }

    private void skipSpaces() {
        while (pos < expr.length() && (expr.charAt(pos) == ' ' || expr.charAt(pos) == '\n'
                || expr.charAt(pos) == '\t')) {
            pos++;
        }
    }

    private void getChar() {
        skipSpaces();
        if (pos == expr.length()) {
            c = '\n';
            return;
        }

        c = expr.charAt(pos);
        pos++;
    }

    private double getExpression() throws ParsingException {
        getChar();
        if (c == '\n') {
            throw new ParsingException("End of string");
        }

        if (c == '(') {
            double ans = getSum();
            if (c != ')') {
                throw new ParsingException("Bad ballance");
            }

            getChar();

            return ans;
        } else if (c == '-') {
            return -getExpression();
        } else if ('0' <= c && c <= '9') {
            double ans = 0;
            while ('0' <= c && c <= '9') {
                ans *= 10;
                ans += c - '0';
                getChar();
            }

            if (c == '\n') {
                return ans;
            }

            if (c != '.') {
                return ans;
            }

            getChar();
            double cur = 0.1;

            while ('0' <= c && c <= '9') {
                ans += cur * (c - '0');
                cur /= 10;
                getChar();
            }

            return ans;
        } else {
            String tmp = new String();
            while (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || c == '_') {
                tmp = tmp + c;
                getChar();
            }

            if (c != '(') {
                if (variable.containsKey(tmp)) {
                    return variable.get(tmp);
                }

                throw new ParsingException("Not exists variable");
            }

            if (s.containsKey(tmp)) {
                int num = s.get(tmp);
                ArrayList<Double> arg = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    double value = getSum();
                    arg.add(value);

                    if (i + 1 != num && c != ',') {
                        throw new ParsingException("Incorrect Argument");
                    } else if (i + 1 == num && c != ')') {
                        throw new ParsingException("Incorrect Argument");
                    }
                }

                getChar();

                if (tmp.equals("sin")) {
                    return Math.sin(arg.get(0));
                } else if (tmp.equals("cos")) {
                    return Math.cos(arg.get(0));
                }  else if (tmp.equals("tg")) {
                    return Math.tan(arg.get(0));
                } else if (tmp.equals("sqrt")) {
                    return Math.sqrt(arg.get(0));
                } else if (tmp.equals("pow")) {
                    return Math.pow(arg.get(0), arg.get(1));
                } else if (tmp.equals("abs")) {
                    return Math.abs(arg.get(0));
                } else if (tmp.equals("sign")) {
                    return Math.signum(arg.get(0));
                } else if (tmp.equals("log")) {
                    return Math.log(arg.get(1)) / Math.log(arg.get(0));
                } else if (tmp.equals("log2")) {
                    return Math.log(arg.get(0)) / Math.log(2.0);
                } else if (tmp.equals("rnd")) {
                    return Math.random();
                } else if (tmp.equals("max")) {
                    return Math.max(arg.get(0), arg.get(1));
                } else if (tmp.equals("min")) {
                    return Math.min(arg.get(0), arg.get(1));
                } else {
                    throw new ParsingException("Incorrect Argument");
                }
            } else if (map.containsKey(tmp)) {
                int num = map.get(tmp).getVariable().size();

                ArrayList<Double> arg = new ArrayList<>();
                for (int i = 0; i < num; i++) {
                    double value = getSum();
                    arg.add(value);

                    if (i + 1 != num && c != ',') {
                        throw new ParsingException("Incorrect Argument");
                    } else if (i + 1 == num && c != ')') {
                        throw new ParsingException("Incorrect Argument");
                    }
                }

                getChar();

                return map.get(tmp).calculate(arg);
            } else {
                throw new ParsingException("Incorrect name");
            }
        }
    }

    private double getMult() throws ParsingException {
        double ans = getExpression();
        while (pos < expr.length()) {
            if (c == '\n') {
                break;
            }

            if (c == '*') {
                ans *= getExpression();
            } else if (c == '/') {
                double cur = getExpression();

                ans /= cur;
            } else {
                break;
            }
        }

        return ans;
    }

    private double getSum() throws ParsingException {
        double ans = getMult();
        while (pos < expr.length()) {
            if (c == '\n') {
                break;
            }
            if (c == '+') {
                ans += getMult();
            } else if (c == '-') {
                ans -= getMult();
            } else {
                break;
            }
        }

        return ans;
    }

    boolean addVariable(String name, String ss) throws ParsingException {
        if (s.containsKey(name)) {
            return false;
        }

        variable.put(name, calculate(ss));

        return true;
    }

    Double getExpr(String name) {
        if (variable.containsKey(name)) {
            return variable.get(name);
        } else {
            return null;
        }
    }

    boolean deleteFunction(String name) {
        if (map.containsKey(name)) {
            map.remove(name);
            return true;
        }

        return false;
    }

    ArrayList<String> getVariable() {
        ArrayList<String> tmp = new ArrayList<>();
        for (Map.Entry<String, Double> it : variable.entrySet()) {
            tmp.add(it.getKey());
        }

        return tmp;
    }

    ArrayList<String> getFunction() {
        ArrayList<String> tmp = new ArrayList<>();
        for (Map.Entry<String, Function> it : map.entrySet()) {
            tmp.add(it.getKey());
        }

        return tmp;
    }

    Pair<String, ArrayList<String>> getFunction(String name) {
        if (map.containsKey(name)) {
            return new Pair(map.get(name).getS(), map.get(name).getVariable());
        }

        return null;
    }

    boolean addFunction(String name, ArrayList<String> l, String ss) {
        if (s.containsKey(name)) {
            return false;
        }

        map.put(name, new Function(l, ss));

        return true;
    }

    boolean deleteVariable(String name) {
        if (variable.containsKey(name)) {
            variable.remove(name);
            return true;
        }

        return false;
    }
}
