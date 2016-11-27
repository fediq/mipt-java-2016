package ru.mipt.java2016.homework.g595.romanenko.task4.base;

import java.util.ArrayList;
import java.util.List;

/**
 * ru.mipt.java2016.homework.g595.romanenko
 *
 * @author Ilya I. Romanenko
 * @since 26.11.16
 **/
public class CalculatorFunction {

    private String body;
    private List<String> args;

    public CalculatorFunction() {
        body = "";
        args = new ArrayList<>();
    }

    public CalculatorFunction(String body, List<String> args) {
        this.body = body;
        this.args = args;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return String.format(body, args.stream().reduce("", String::join));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CalculatorFunction)) {
            return false;
        }
        CalculatorFunction tmp = (CalculatorFunction) obj;
        return body.equals(tmp.body) &&
                args.equals(tmp.getArgs());
    }

    @Override
    public int hashCode() {
        return (body.hashCode() << 1) | (args.hashCode());
    }
}
