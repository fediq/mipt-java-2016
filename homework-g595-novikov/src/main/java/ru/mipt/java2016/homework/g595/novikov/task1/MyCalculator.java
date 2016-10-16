package ru.mipt.java2016.homework.g595.novikov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

public class MyCalculator implements Calculator {
    private static class Tokenizer {
        private String expr;
        private String current;
        private int pos = 0;
        private boolean next = true;
        
        Tokenizer(String expression) {
            expr = expression.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "");
            next();
        }
        
        Tokenizer(Tokenizer tk) {
            expr = tk.expr;
            pos = tk.pos;
            current = tk.current;
            next = tk.next;
        }
        
        public boolean hasNext() {
            return next;
        }
        
        public String getCurrent() {
            return current;
        }
        
        private static boolean isSpecial(char symb) {
            return symb == '(' || symb == ')' || symb == '+' 
                    || symb == '-' || symb == '*' || symb == '/';
        }
        
        public Tokenizer next() {
            if (pos == expr.length()) {
                next = false;
                current = null;
            } else if (isSpecial(expr.charAt(pos))) {
                current = Character.toString(expr.charAt(pos));
                ++pos;
            } else {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(expr.charAt(pos));
                    pos += 1;
                    
                } while (pos != expr.length() 
                        && !isSpecial(expr.charAt(pos)));
                current = sb.toString();
            }
            
            return this;
        }
    }
    
    private static double brackets(Tokenizer tokens) throws ParsingException {
        if (tokens.hasNext() && tokens.getCurrent().equals("-")) {
            return -brackets(tokens.next());
        }
        
        if (tokens.hasNext() && tokens.getCurrent().equals("(")) {
            double res = expr(tokens.next());
            if (tokens.hasNext() && tokens.getCurrent().equals(")")) {
                tokens.next();
                return res;
            }
            throw new ParsingException("Error during brackets() : cannot find ')'");
        } else if (tokens.hasNext()) {
            try {
                double res = Double.valueOf(tokens.getCurrent()).doubleValue();
                tokens.next();
                return res;
            } catch (NumberFormatException e) {
                throw new ParsingException("Error during brackets() : cannot parse float");
            }
        }
        throw new ParsingException("Error during brackets() : cannot find '(' or number");
    }
    
    private static double mul(Tokenizer tokens) throws ParsingException {
        double res = brackets(tokens);
        while (tokens.hasNext() && (tokens.getCurrent().equals("*")
                || tokens.getCurrent().equals("/"))) {
            if (tokens.getCurrent().equals("*")) {
                res *= brackets(tokens.next());
            } else {
                res /=  brackets(tokens.next());
            }
        }
        return res;
    }
    
    private static double add(Tokenizer tokens) throws ParsingException {
        double res = mul(tokens);
        while (tokens.hasNext() && (tokens.getCurrent().equals("-")
                || tokens.getCurrent().equals("+"))) {
            if (tokens.getCurrent().equals("+")) {
                res += mul(tokens.next());
            } else {
                res -= mul(tokens.next());
            }
        }
        return res;
    }
    
    private static double expr(Tokenizer tokens) throws ParsingException {
        return add(tokens);
    }
    
    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("expression is null");
        }
        Tokenizer tokens = new Tokenizer(expression);
        double res = expr(tokens);
        if (tokens.hasNext()) {
            throw new ParsingException("Error during calculate: there are more tokens");
        }
        return res;
    }
}
