package ru.mipt.java2016.homework.g595.proskurin.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.proskurin.task1.MyCalculator;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Александр on 16.12.2016.
 */

public class NewCalculator {

    private class Function {
        private String name;
        private ArrayList<String> params = new ArrayList<String>();
        private String expression;

        Function(String name, ArrayList<String> params, String expression) {
            this.name = name;
            this.params = params;
            this.expression = expression;
        }
    }

    private HashMap<String, Function> functions = new HashMap<String, Function>();
    private HashMap<String, String> variables = new HashMap<String, String>();

    NewCalculator() {

    }

    private MyCalculator solver = new MyCalculator();

    boolean checkStandart(String tname) {
        if (tname.equals("cos")) {
            return true;
        }
        if (tname.equals("sin")) {
            return true;
        }
        if (tname.equals("rnd")) {
            return true;
        }
        if (tname.equals("max")) {
            return true;
        }
        if (tname.equals("min")) {
            return true;
        }
        if (tname.equals("tg")) {
            return true;
        }
        if (tname.equals("sqrt")) {
            return true;
        }
        if (tname.equals("pow")) {
            return true;
        }
        if (tname.equals("abs")) {
            return true;
        }
        if (tname.equals("sign")) {
            return true;
        }
        if (tname.equals("log")) {
            return true;
        }
        if (tname.equals("log2")) {
            return true;
        }
        return false;
    }

    double getStandart(String tname, ArrayList<String> lst) throws ParsingException {
        if (tname.equals("cos")) {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.cos(solve(lst.get(0)));
        } else if (tname.equals("sin")) {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.sin(solve(lst.get(0)));
        } else if (tname.equals("rnd")) {
            if (lst.size() != 0) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.random();
        } else if (tname.equals("max")) {
            if (lst.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.max(solve(lst.get(0)), solve(lst.get(1)));
        } else if (tname.equals("min")) {
            if (lst.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.min(solve(lst.get(0)), solve(lst.get(1)));
        } else if (tname.equals("tg")) {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.tan(solve(lst.get(0)));
        } else if (tname.equals("sqrt")) {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.sqrt(solve(lst.get(0)));
        } else if (tname.equals("pow")) {
            if (lst.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.pow(solve(lst.get(0)), solve(lst.get(1)));
        } else if (tname.equals("abs")) {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.abs(solve(lst.get(0)));
        } else if (tname.equals("sign")) {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.signum(solve(lst.get(0)));
        } else if (tname.equals("log")) {
            if (lst.size() != 2) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.log(solve(lst.get(0))) / Math.log(solve(lst.get(1)));
        } else {
            if (lst.size() != 1) {
                throw new ParsingException("Incorrect expression");
            }
            return Math.log(solve(lst.get(0))) / Math.log(2.0);
        }
    }

    public double solve(String expression) throws ParsingException {
        double ans = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) >= 'a' && expression.charAt(i) <= 'z' || expression.charAt(i) == '_') {
                int pos = i;
                while (pos < expression.length() && (expression.charAt(pos) >= 'a' && expression.charAt(pos) <= 'z'
                        || expression.charAt(pos) == '_' || expression.charAt(pos) >= '1'
                        && expression.charAt(pos) <= '9' || expression.charAt(pos) >= 'A'
                        && expression.charAt(pos) <= 'Z')) {
                    pos++;
                }
                if (pos == expression.length() || expression.charAt(pos) != '(') {
                    if (!variables.containsKey(expression.substring(i, pos))) {
                        throw new ParsingException("There is no such variable");
                    }
                    String tmp = expression.substring(0, i);
                    tmp = tmp.concat(String.valueOf(solve(variables.get(expression.substring(i, pos)))));
                    tmp = tmp.concat(expression.substring(pos, expression.length()));
                    expression = tmp;
                    i = -1;
                } else {
                    if (!checkStandart(expression.substring(i, pos)) &&
                            !functions.containsKey(expression.substring(i, pos))) {
                        throw new ParsingException("There is no such function");
                    }
                    String tname = expression.substring(i, pos);
                    ArrayList<String> params = new ArrayList<String>();
                    int bal = 1;
                    String cname = "";
                    pos++;
                    while (pos < expression.length() && !(bal == 0 && expression.charAt(pos) == ')')) {
                        if (expression.charAt(pos) == ',') {
                            params.add(String.valueOf(solve(cname)));
                            cname = "";
                        } else if (bal != 0) {
                            cname = cname.concat(expression.substring(pos, pos + 1));
                        }
                        if (bal < 0) {
                            throw  new ParsingException("Incorrect expression!");
                        }
                        pos++;
                        if (pos < expression.length() && expression.charAt(pos) == '(') {
                            bal++;
                        } else if (pos < expression.length() && expression.charAt(pos) == ')') {
                            bal--;
                        }
                    }
                    if (pos != expression.length()) {
                        pos++;
                    }
                    if (bal != 0) {
                        throw  new ParsingException("Incorrect expression!");
                    }
                    if (!cname.equals("")) {
                        params.add(String.valueOf(solve(cname)));
                    }
                    if (!checkStandart(tname)) {
                        if (params.size() != functions.get(tname).params.size()) {
                            throw new ParsingException("Incorrect expression!");
                        }
                        String curExpr = functions.get(tname).expression;
                        for (int j = 0; j < functions.get(tname).params.size(); j++) {
                            curExpr = curExpr.replaceAll(functions.get(tname).params.get(j),
                                    String.valueOf(params.get(j)));
                        }
                        String tmp = expression.substring(0, i);
                        tmp = tmp.concat(String.valueOf(solve(curExpr)));
                        tmp = tmp.concat(expression.substring(pos, expression.length()));
                        expression = tmp;
                        i = -1;
                    } else {
                        String tmp = expression.substring(0, i);
                        tmp = tmp.concat(String.valueOf(getStandart(tname, params)));
                        tmp = tmp.concat(expression.substring(pos, expression.length()));
                        expression = tmp;
                        i = -1;
                    }
                }
            }
        }
        return solver.calculate(expression);
    }

    public void addFunc(String s) throws ParsingException {
        String tname = "#";
        String cname = "";
        String name = "";
        int pos = 0;
        ArrayList<String> tlist = new ArrayList<String>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                tname = s.substring(0, i);
            } else if (s.charAt(i) == ',') {
                tlist.add(cname);
                cname = "";
            } else if (s.charAt(i) == ')') {
                tlist.add(cname);
                cname = "";
                name = tname;
                tname = "$";
                pos = i + 4;
                break;
            } else if (!tname.equals("#") && !tname.equals("$")) {
                if (s.charAt(i) != ' ') {
                    cname = cname.concat(s.substring(i, i + 1));
                }
            }
        }
        Function tmp = new Function(name, tlist, s.substring(pos, s.length()));
        if (checkStandart(tname)) {
            throw new ParsingException("Incorrect expression!");
        }
        for (int i = 0; i < tlist.size(); i++) {
            if (checkStandart(tlist.get(i))) {
                throw new ParsingException("Incorrect expression!");
            }
        }
        functions.put(name, tmp);
        return;
    }

    public boolean addVar(String s) throws ParsingException {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                if (checkStandart(s.substring(0, i))) {
                    throw new ParsingException("Incorrect expression!");
                }
                variables.put(s.substring(0, i), s.substring(i + 3, s.length()));
                return true;
            }
        }
        return false;
    }

    public boolean delVar(String s) {
        if (!variables.containsKey(s)) {
            return false;
        }
        variables.remove(s);
        return true;
    }

    public ArrayList<String> getVars() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (HashMap.Entry<String, String> item : variables.entrySet()) {
            tmp.add(item.getKey());
        }
        return tmp;
    }

    public String getFunc(String s) {
        if (checkStandart(s)) {
            return "Function is standart";
        }
        if (!functions.containsKey(s)) {
            return "There is no such function";
        }
        String tmp = s;
        tmp = tmp.concat("(");
        for (int i = 0; i < functions.get(s).params.size(); i++) {
            tmp = tmp.concat(functions.get(s).params.get(i));
            if (i != functions.get(s).params.size() - 1) {
                tmp = tmp.concat(", ");
            }
        }
        tmp = tmp.concat(") = ");
        tmp = tmp.concat(functions.get(s).expression);
        return tmp;
    }

    public boolean delFunc(String s) {
        if (!functions.containsKey(s)) {
            return false;
        }
        functions.remove(s);
        return true;
    }

    public ArrayList<String> getFuncs() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (HashMap.Entry<String, Function> item : functions.entrySet()) {
            tmp.add(item.getKey());
        }
        return tmp;
    }
}

/*class Main {
    public static void main(String[] args) throws ParsingException {
        NewCalculator tmp = new NewCalculator();
        tmp.addVar("a = 10");
        tmp.addFunc("f(x) = x*x");
        System.out.println(tmp.solve("f(a)"));
        return;
    }
}*/
