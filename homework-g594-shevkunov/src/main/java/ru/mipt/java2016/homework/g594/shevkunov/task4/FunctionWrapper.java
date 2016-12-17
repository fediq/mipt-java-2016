package ru.mipt.java2016.homework.g594.shevkunov.task4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by shevkunov on 16.12.16.
 */
public class FunctionWrapper {
    FunctionWrapper(String name, List<String> args, String value) {
        this.name = name;
        this.value = value;
        this.args = args;
    }

    FunctionWrapper(String name, String args, String value) {
        this.name = name;
        this.value = value;
        this.args = stringToList(args);
    }

    static final char SEPARATOR = ',';
    private List<String> args;
    private String name;
    private String value;

    public List<String> getArgs() {
        return args;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Function name = [" + name + "], args = [" + argsToString() + "]" + ", value = [" + value + "]";
    }

    public String argsToString() {
        return listToString(args);
    }

    public static List<String> stringToList(String s) {
        List<String> list = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != SEPARATOR) {
                sb.append(s.charAt(i));
            } else {
                list.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }

        if (sb.length() > 0) {
            list.add(sb.toString());
        }
        return list;
    }

    public static String listToString(List<String> list) {
        String all = "";
        for (Iterator<String> i = list.iterator(); i.hasNext();) {
            all += i.next();
            if (i.hasNext()) {
                all += ",";
            }
        }
        return all;
    }

    public static boolean isVariableChar(char ch) {
        return Character.isAlphabetic(ch) || Character.isDigit(ch) || ch == '_';
    }
}
