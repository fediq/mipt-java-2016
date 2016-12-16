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
        this.args = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length(); ++i) {
            if (args.charAt(i) != SEPARATOR) {
                sb.append(args.charAt(i));
            } else {
                this.args.add(sb.toString());
                sb.delete(0, sb.length());
            }
        }

        if (sb.length() > 0) {
            this.args.add(sb.toString());
        }
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
        String all = "";
        for (Iterator<String> i = args.iterator(); i.hasNext(); ) {
            all += i.next();
            if (i.hasNext()) {
                all += ",";
            }
        }
        return all;
    }
}
