package ru.mipt.java2016.homework.g595.novikov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import java.util.Vector;

public class MyCalculator implements Calculator
{
    Tokenizer tokens;
    
    static class Tokenizer
    {
        String expr, current;
        int pos = 0;
        boolean next = true;
        
        Tokenizer (String expression)
        {
            expr = expression.replace(" ", "").replace("\n", "").replace("\t", "");
            next();
        }
        
        Tokenizer (Tokenizer tk)
        {
            expr = tk.expr;
            pos = tk.pos;
            current = tk.current;
            next = tk.next;
        }
        
        public boolean hasNext()
        {
            return next;
        }
        
        public String getCurrent()
        {
            return current;
        }
        
        boolean isSpecial(char c)
        {
            return c == '(' || c == ')' || c == '+' 
                    || c == '-' || c == '*' || c == '/';
        }
        
        public Tokenizer next()
        {
            if (pos == expr.length())
            {
                next = false;
                current = null;
            }
            else if (isSpecial(expr.charAt(pos)))
            {
                current = Character.toString(expr.charAt(pos));
                ++pos;
            }
            else
            {
                StringBuilder sb = new StringBuilder();
                do
                {
                    sb.append(expr.charAt(pos));
                    pos += 1;
                    
                } while (pos != expr.length() 
                        && !isSpecial(expr.charAt(pos)));
                current = sb.toString();
            }
            
            return this;
        }
    }
    
    double brackets() throws ParsingException
    {
        if (tokens.hasNext() && tokens.getCurrent().equals("-"))
        {
            tokens.next();
            return -brackets();
        }
        
        if (tokens.hasNext() && tokens.getCurrent().equals("("))
        {
            tokens.next();
            double res = expr();
            if (tokens.hasNext() && tokens.getCurrent().equals(")"))
            {
                tokens.next();
                return res;
            }
            throw new ParsingException("Error during brackets() : cannot find ')'");
        }
        else if (tokens.hasNext())
        {
            try {
                double res = Double.valueOf(tokens.getCurrent()).doubleValue();
                tokens.next();
                return res;
            }
            catch (NumberFormatException e)
            {
                throw new ParsingException("Error during brackets() : cannot parse float");
            }
        }
        throw new ParsingException("Error during brackets() : cannot find '(' or number");
    }
    
    double mul() throws ParsingException
    {
        double a = brackets();
        while (tokens.hasNext() && (tokens.getCurrent().equals("*")
                || tokens.getCurrent().equals("/")))
        {
            if (tokens.getCurrent().equals("*"))
            {
                tokens.next();
                a = a * brackets();
            }
            else
            {
                tokens.next();
                a = a / brackets();
            }
        }
        return a;
    }
    
    double add() throws ParsingException
    {
        double a = mul();
        while (tokens.hasNext() && (tokens.getCurrent().equals("-")
                || tokens.getCurrent().equals("+")))
        {
            if (tokens.getCurrent().equals("+"))
            {
                tokens.next();
                a += mul();
            }
            else
            {
                tokens.next();
                a -= mul();
            }
        }
        return a;
    }
    
    double expr() throws ParsingException
    {
        return add();
    }
    
    void printTokenizer()
    {
        Tokenizer tk = new Tokenizer(tokens);
        Vector<String> vec = new Vector<String>();
        while (tk.hasNext())
        {
            vec.add(tk.getCurrent());
            tk.next();
        }
        for (String str : vec)
            System.out.print(str + '_');
        System.out.println();
    }
    
    @Override
    public double calculate(String expression) throws ParsingException
    {
        if (expression == null)
            throw new ParsingException("expression is null");
        tokens = new Tokenizer(expression);
        double res = expr();
        if (tokens.hasNext())
            throw new ParsingException("Error during calculate: there are more tokens");
        tokens = null;
        return res;
    }
}
