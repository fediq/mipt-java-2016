package ru.mipt.java2016.homework.g595.novikov.task1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.collections.IteratorUtils;
import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.g595.novikov.myutils.MapUnion2;
import ru.mipt.java2016.homework.g595.novikov.myutils.SavingPointer;
import ru.mipt.java2016.homework.g595.novikov.task4.MyFunction;

public class MyCalculator implements Calculator {
    public static class Tokenizer implements SavingPointer<String> {
        private String expr;
        private String current;
        private int pos = 0;

        Tokenizer(String expression) { // FIXME: "1 1" is ["1", "1"], but tokenizer returns ["11"]
            expr = expression.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "");
            next();
        }

        Tokenizer(Tokenizer tk) {
            expr = tk.expr;
            pos = tk.pos;
            current = tk.current;
        }

        public boolean hasCurrent() {
            return current != null;
        }

        public String getCurrent() {
            return current;
        }

        private static boolean isSpecial(char symb) {
            return symb == '(' || symb == ')' || symb == '+' || symb == '-' || symb == '*'
                    || symb == '/' || symb == ',';
        }

        public Tokenizer next() {
            if (pos == expr.length()) {
                current = null;
            } else if (isSpecial(expr.charAt(pos))) {
                current = Character.toString(expr.charAt(pos));
                ++pos;
            } else {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(expr.charAt(pos));
                    pos += 1;

                } while (pos != expr.length() && !isSpecial(expr.charAt(pos)));
                current = sb.toString();
            }

            return this;
        }
    }

    class MyEvaluableFunction implements MyFunction {
        private List<String> args;
        private List<String> functionTokens;

        MyEvaluableFunction(List<String> myArgs, String myExpression) {
            args = myArgs;
            functionTokens = IteratorUtils.toList(new Tokenizer(myExpression)
                    .toIterator()); // FIXME : unchecked assignment ???
            validate();
        }

        private void validate() {
            // do nothing, TODO : Validate args
        }

        public double eval(SavingPointer<String> tokens, Map<String, Double> variables,
                Map<String, MyFunction> functions) throws ParsingException {
            Map<String, Double> argumentValues = new HashMap<>();
            for (Iterator<String> iter = args.iterator(); iter.hasNext();) {
                String argument = iter.next();
                argumentValues.put(argument, expr(tokens, variables, functions));
                if (iter.hasNext()) {
                    if (!tokens.getCurrent().equals(",")) {
                        throw new ParsingException("wrong number of arguments");
                    }
                    tokens.next();
                }
            }
            if (!tokens.getCurrent().equals(")")) {
                throw new ParsingException("expected ) in the end of function body");
            }
            tokens.next();
            return expr(SavingPointer.fromIterator(functionTokens.iterator()),
                    new MapUnion2<>(argumentValues, variables), functions);
        }
    }

    private static boolean isIdentifier(String str) {
        return str.charAt(0) >= 'a' && str.charAt(0) <= 'z'; // TODO : reimplement this
    }

    private static double brackets(SavingPointer<String> tokens, Map<String, Double> variables,
            Map<String, MyFunction> functions) throws ParsingException {
        if (tokens.hasCurrent() && tokens.getCurrent().equals("-")) {
            return -brackets(tokens.next(), variables, functions);
        }

        if (tokens.hasCurrent() && tokens.getCurrent().equals("(")) {
            double res = expr(tokens.next(), variables, functions);
            if (tokens.hasCurrent() && tokens.getCurrent().equals(")")) {
                tokens.next();
                return res;
            }
            throw new ParsingException("Error during brackets() : cannot find ')'");
        } else if (tokens.hasCurrent() && !isIdentifier(tokens.getCurrent())) {
            double res;
            try {
                res = Double.valueOf(tokens.getCurrent());
            } catch (NumberFormatException e) {
                throw new ParsingException(
                        "Error during brackets() : cannot parse float : " + tokens.getCurrent());
            }
            tokens.next();
            return res;
        } else if (tokens.hasCurrent()) {
            String identifier = tokens.getCurrent();
            tokens.next();
            if (tokens.hasCurrent() && tokens.getCurrent().equals("(")) {
                tokens.next();
                if (!functions.containsKey(identifier)) {
                    throw new ParsingException("unknown identifier : " + identifier);
                }
                MyFunction func = functions.get(identifier);
                return func.eval(tokens, variables, functions);
            } else {
                if (variables.containsKey(identifier)) {
                    return variables.get(identifier);
                }
                throw new ParsingException("unknown identifier : " + identifier);
            }
        }
        throw new ParsingException("Error during brackets() : cannot find '(' or number");
    }

    private static double mul(SavingPointer<String> tokens, Map<String, Double> variables,
            Map<String, MyFunction> functions) throws ParsingException {
        double res = brackets(tokens, variables, functions);
        while (tokens.hasCurrent() && (tokens.getCurrent().equals("*") || tokens.getCurrent()
                .equals("/"))) {
            if (tokens.getCurrent().equals("*")) {
                res *= brackets(tokens.next(), variables, functions);
            } else {
                res /= brackets(tokens.next(), variables, functions);
            }
        }
        return res;
    }

    private static double add(SavingPointer<String> tokens, Map<String, Double> variables,
            Map<String, MyFunction> functions) throws ParsingException {
        double res = mul(tokens, variables, functions);
        while (tokens.hasCurrent() && (tokens.getCurrent().equals("-") || tokens.getCurrent()
                .equals("+"))) {
            if (tokens.getCurrent().equals("+")) {
                res += mul(tokens.next(), variables, functions);
            } else {
                res -= mul(tokens.next(), variables, functions);
            }
        }
        return res;
    }

    private static double expr(SavingPointer<String> tokens, Map<String, Double> variables,
            Map<String, MyFunction> functions) throws ParsingException {
        return add(tokens, variables, functions);
    }

    public double calculateTokens(SavingPointer<String> tokens, Map<String, Double> variables,
            Map<String, MyFunction> functions) throws ParsingException {
        double res =
                expr(tokens, variables, functions); // let's think we've already got immutable map
        if (tokens.hasCurrent()) {
            throw new ParsingException("there are more tokens");
        }
        return res;
    }

    public double calculateExpression(String expression, Map<String, Double> variables,
            Map<String, MyFunction> functions) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("expression is null");
        }
        Tokenizer tokens = new Tokenizer(expression);
        double res = expr(tokens, variables, functions);
        if (tokens.hasCurrent()) {
            throw new ParsingException("there are more tokens");
        }
        return res;
    }

    @Override
    public double calculate(String expression) throws ParsingException {
        return calculateExpression(expression, Collections.emptyMap(), Collections.emptyMap());
    }

    public MyFunction addFunction(List<String> args, String expression) {
        return new MyEvaluableFunction(args, expression);
    }

    private class MyBuiltinFunction2Arg implements MyFunction {
        private BiFunction<Double, Double, Double> func;

        MyBuiltinFunction2Arg(BiFunction<Double, Double, Double> func) {
            this.func = func;
        }

        @Override
        public double eval(SavingPointer<String> tokens, Map<String, Double> variables,
                Map<String, MyFunction> functions) throws ParsingException {
            double a;
            double b;
            a = expr(tokens, variables, functions);
            if (!tokens.getCurrent().equals(",")) {
                throw new ParsingException("wrong number of arguments");
            }
            tokens.next();
            b = expr(tokens, variables, functions);
            if (!tokens.getCurrent().equals(")")) {
                throw new ParsingException("expected ) in the end of function body");
            }
            tokens.next();
            return func.apply(a, b);
        }
    }

    private class MyBuiltinFunction1Arg implements MyFunction {
        private Function<Double, Double> func;

        MyBuiltinFunction1Arg(Function<Double, Double> func) {
            this.func = func;
        }

        @Override
        public double eval(SavingPointer<String> tokens, Map<String, Double> variables,
                Map<String, MyFunction> functions) throws ParsingException {
            double a;
            a = expr(tokens, variables, functions);
            if (!tokens.getCurrent().equals(")")) {
                throw new ParsingException("expected ) in the end of function body");
            }
            tokens.next();
            return func.apply(a);
        }
    }

    private class MyBuiltinFunction0Arg implements MyFunction {
        private Supplier<Double> func;

        MyBuiltinFunction0Arg(Supplier<Double> func) {
            this.func = func;
        }

        @Override
        public double eval(SavingPointer<String> tokens, Map<String, Double> variables,
                Map<String, MyFunction> functions) throws ParsingException {
            if (!tokens.getCurrent().equals(")")) {
                throw new ParsingException("expected ) in the end of function body");
            }
            tokens.next();
            return func.get();
        }
    }

    public MyFunction addBuiltinFunction0Arg(Supplier<Double> func) {
        return new MyBuiltinFunction0Arg(func);
    }

    public MyFunction addBuiltinFunction1Arg(Function<Double, Double> func) {
        return new MyBuiltinFunction1Arg(func);
    }

    public MyFunction addBuiltinFunction2Arg(BiFunction<Double, Double, Double> func) {
        return new MyBuiltinFunction2Arg(func);
    }
}
