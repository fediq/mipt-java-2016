package ru.mipt.java2016.homework.g594.shevkunov.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.List;
import java.util.Map;

/**
 * Created by shevkunov on 17.12.16.
 */
public class Substitutor {
    Substitutor(Map<String, String> variables,
                Map<String, FunctionWrapper> functions) {
        this.variables = variables;
        this.functions = functions;
    }

    String functionSubstitute(FunctionWrapper function, String strArgs) throws ParsingException {
        List<String> substitutions = FunctionWrapper.stringToList(strArgs);
        List<String> args = function.getArgs();

        for (int i = 0; i < substitutions.size(); ++i) {
            substitutions.set(i, substitute(substitutions.get(i)));
        }

        String value = function.getValue();

        if (substitutions.size() != args.size()) {
            throw new ParsingException("Bad arguments substitution.");
        }

        StringBuilder res = new StringBuilder();
        for (int i = 0; i < value.length();) {
            boolean substituted = false;
            for (int j = 0; j < args.size(); ++j) {
                String arg = args.get(j);
                boolean stringComparable = (value.length() - i >= arg.length()) &&
                        arg.equals(value.substring(i, i + arg.length()));
                if (stringComparable) { // TODO So Sloow
                    boolean endedAtGoodSymbol = (value.length() - i == arg.length()) ||
                            !FunctionWrapper.isVariableChar(value.charAt(i + arg.length()));
                    if (endedAtGoodSymbol) {
                        res.append(substitutions.get(j));
                        i += arg.length();
                        substituted = true;
                        break;
                    }
                }

                res.append(value.charAt(i));
                ++i;
            }
        }

        return substitute(res.toString());
    }

    String substitute(String expression) throws ParsingException {
        ++level;
        if (level > maximumLevel) {
            level = 0;
            throw new ParsingException("So deep...");
        }
        StringBuilder buffer = new StringBuilder();
        StringBuilder newExpression = new StringBuilder();


        for (int i = 0; i < expression.length(); ++i) {
            if (FunctionWrapper.isVariableChar(expression.charAt(i))) {
                buffer.append(expression.charAt(i));
            }

            if (!FunctionWrapper.isVariableChar(expression.charAt(i)) || (expression.length() == i + 1)) {
                String proceed = buffer.toString();
                buffer.delete(0, buffer.length());

                if (expression.charAt(i) == '(') {
                    // FUNCTION
                    if (functions.containsKey(proceed)) {
                        int rPos = i + 1;
                        int balance = 1;
                        while ((rPos < expression.length()) && (balance != 0)) {
                            switch (expression.charAt(rPos)) {
                                case '(':
                                    ++balance;
                                    break;
                                case ')':
                                    --balance;
                                    break;
                                default:
                            }
                            ++rPos;
                        }

                        if (balance == 0) {
                            FunctionWrapper function = functions.get(proceed);
                            String substituted = functionSubstitute(function, expression.substring(i + 1, rPos - 1));
                            newExpression.append(" (");
                            newExpression.append(substituted);
                            newExpression.append(") ");
                        } else {
                            throw new ParsingException("Cannot substitude a function.");
                        }

                        i = rPos - 1;
                        continue;
                    } else {
                        newExpression.append(proceed);
                    }
                } else {
                    // VARIABLE
                    if (variables.containsKey(proceed)) {
                        newExpression.append(variables.get(proceed));
                    } else {
                        newExpression.append(proceed);
                    }
                }

                if (!FunctionWrapper.isVariableChar(expression.charAt(i))) {
                    newExpression.append(expression.charAt(i));
                }
            }
        }

        --level;
        return newExpression.toString();
    }

    private Map<String, String> variables;
    private Map<String, FunctionWrapper> functions;
    private int level = 0;
    private final int maximumLevel = 1000;
}
