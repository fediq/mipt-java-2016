package ru.mipt.java2016.homework.g594.vorobeyv.task1;

/**
 * Created by Morell on 12.10.2016.
 */

import com.sun.javafx.fxml.expression.Expression;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.sun.org.apache.xpath.internal.compiler.OpCodes;
import ru.mipt.java2016.homework.base.task1.ParsingException;
import ru.mipt.java2016.homework.base.task1.Calculator;
import java.util.ArrayList;
import java.util.Stack;


import java.util.ArrayList;

public class Calcul implements Calculator {
        // Виды символов в parsed ArrayList.
    boolean IsOp( char c ){
        return c=='+' || c=='-' || c=='*' || c=='/';
    }
    boolean IsBreket( char c ){
        return c == '(' || c == ')';
    }
    // Если это +|- => то унарная, *,/=> false
    boolean UnaryOp( char c ){
        return  c =='+';
    }
    boolean decimal( char c ){  return c == ' ' || c == '\n' || c == '\t';}
    public class Token{}
        public class Op extends Token{
            public char oper;
            public boolean unary;
            public byte priority;

            public Op( char oper, boolean unary ){
                this.oper = oper;
                this.unary = unary;
            }
            public  Op( char oper ){
                this.oper = oper;
            }
        }
        public class Num extends Token{
            public double num;
            public boolean positive = true;// Отрицательное или положительное число.
            public Num( double num ){
                this.num = num;
            }
        }
        public class Brackets extends Token{
            public char br;
            public boolean type;
            public Brackets( char br, boolean type ){
                this.br = br;
                this.type = type;
            }
        }
        public double calculate(String expression) throws ParsingException {
           // Пустая строка. => except
            if( expression == null ){
                throw new ParsingException("Null expression");
            }
           // Тут не пустая строка.
            StringBuilder expr = new StringBuilder();
            expr.append(expression);

            // Проверка строки.
            ArrayList<Token> parsed = parser( expr );
            if( parsed.size() != 0 ){  // Подсчет.
                return evaluator( parsed );
            } else {
                throw new ParsingException("Illegal ecpression");  // Строка была из пробелов
            }
        }

        private ArrayList<Token> parser(StringBuilder expr)throws ParsingException {
            ArrayList<Token> parsed = new ArrayList<Token>();

            int i = 0;
            int BracketBalance = 0;
            boolean unary = true;
            while (i < expr.length()) {
                char cur = expr.charAt(i);
                if (decimal( cur )) {
                    i++;
                } else if (Character.isDigit(cur)) {// Цифра
                    unary = false;
                    StringBuilder CurDouble = new StringBuilder();
                    //   CurDouble.append( cur );
                    while (i < expr.length()
                            && Character.isDigit(expr.charAt(i))) {
                        CurDouble.append(expr.charAt(i));
                        i++;// ATTENTION 123v - после цикла i будет на v.
                    }
                    if (i < expr.length()) {
                        // Если точка, то на текущий момент вид: 213.^^^
                        if (expr.charAt(i) == '.') {
                           CurDouble.append('.');
                            // Если выраж.: ^^^213. => throw Exception (точка не нужна)
                            if (i == expr.length() - 1) {
                                throw new ParsingException("Illegal expression");
                            } else if (Character.isDigit(expr.charAt(i + 1))) {// Если ^^^213.D
                                // Перешли на D.
                                i++;
                                while (i < expr.length()
                                        && Character.isDigit(expr.charAt(i))) {
                                    CurDouble.append(expr.charAt(i));
                                    i++;// ATTENTION 123v - после цикла i будет на v.
                                }
                            }
                        } else { // Добавить '.0' до double
                            CurDouble.append(".0");
                        }
                    } else {
                        CurDouble.append(".0");
                    }// Если число в конце строки => покием строку по while выше
                    // Тут в CurDouble набран наш double
                    String StrDouble = CurDouble.toString();
                    double NewDouble = Double.parseDouble(StrDouble);// ATTENTION проверить parse -> double
                    Num NewNum = new Num( NewDouble );
                    parsed.add( NewNum );
                } else if (IsBreket(cur)) {// Скобка.
                    boolean open;
                    if (cur == '(') {
                        unary = true;
                        open = true;
                        BracketBalance++;
                    } else {
                        unary = false;
                        open = false;
                        BracketBalance--;
                    }
                    Brackets NewBracket = new Brackets(cur, open);
                    parsed.add(NewBracket);
                    i++;
                } else if (IsOp(cur)) { // Оператор
                    Op NewOp = new Op(cur);
                    if (unary && UnaryOp(cur)) {
                        NewOp.unary = true;
                        NewOp.priority = 4;
                    } else if (unary && !UnaryOp(cur)) { // Текущая операция должна быть унарной, но cur =*|/
                        throw new ParsingException("Illegal expression");
                    } else {// Бинарная операция
                        NewOp.unary = false;
                        if (cur == '+' || cur == '-') {
                            NewOp.priority = 1;
                        } else {
                            NewOp.priority = 2; // * или /
                        }
                    }
                    parsed.add(NewOp);
                    // Cur операц. => след операц. унар
                    unary = true;
                    i++;
                } else {// Символы не арифм.выраж.
                    throw new ParsingException("Illegal expression");
                }
            }

            if( BracketBalance != 0 ){
                throw new ParsingException("Illegal expression");
            } else {
                return parsed;
            }
        }

        private void elem_op( Stack<Token> numeral, Op CurOp ) throws ParsingException{
            // Унарная.
            if( CurOp.unary ) {
                if( numeral.empty()){
                    throw  new ParsingException("Illegal expression");
                }
                Num LNum = (Num) numeral.peek();
                numeral.pop();
                switch ( CurOp.oper ) {
                    case '-': {
                        LNum.num *= -1;
                        break;
                    }
                    case '+': {
                        break;
                    }
                }
                numeral.push( LNum);
            } else { // Бинарная.
                if ( numeral.size() < 2 ){
                    throw  new ParsingException("Illegal expression");
                }
                Num RNum = (Num) numeral.peek();
                numeral.pop();
                Num LNum = (Num) numeral.peek();
                numeral.pop();
                // Результат пишем в Lnum
                switch ( CurOp.oper ) {
                    case '+': {
                        LNum.num = LNum.num + RNum.num;
                        numeral.push( LNum );
                        break;
                    }
                    case '-': {
                        LNum.num = LNum.num - RNum.num;
                        numeral.push( LNum );
                        break;
                    }
                    case '*': {
                        LNum.num = LNum.num * RNum.num;
                        numeral.push( LNum );
                        break;
                    }
                    case '/': {
                        // ATTENTION бесконечность
                        if (Double.isInfinite(LNum.num / RNum.num)) {
                            LNum.num /= RNum.num;
                        } else {
                            LNum.num = LNum.num / RNum.num;
                        }
                        numeral.push( LNum );
                        break;
                    }
                }
            }
        }

        private double evaluator( ArrayList<Token> parsed ) throws ParsingException{
            Stack<Token> numeral = new Stack<Token>();
            Stack<Token> operand = new Stack<Token>();
            for( int i = 0; i < parsed.size(); i++ ){
                Token cur = parsed.get(i);
                if( cur instanceof Num ){
                    numeral.push( cur );
                } else if( cur instanceof Brackets ){
                    Brackets CurBracket = (Brackets) cur;
                    if( CurBracket.br == '(' ){
                        operand.push( cur );
                    } else{
                        // Случай Cur =')' и на вершине operand ')' => баланс не 0( проверяли в parser )
                        if( numeral.empty() ) {
                            throw new ParsingException("Illegal expression");
                        } else {
                            // Между '(' и ')' только +,-,*,/
                            while (operand.peek() instanceof Op ) {
                                elem_op( numeral , (Op)operand.peek());
                                operand.pop();
                            }
                            operand.pop();
                        }
                    }
                } else if( cur instanceof Op ) {
                    Op CurOp = (Op)cur;
                    while ( !operand.empty()
                            && operand.peek() instanceof Op
                            && ( CurOp.unary && ((Op) operand.peek()).priority > CurOp.priority
                            || !CurOp.unary && ((Op) operand.peek()).priority >= CurOp.priority) ) {
                        elem_op( numeral, ((Op) operand.peek()));
                        operand.pop();

                    }
                    operand.push( CurOp );
                }
            }

            if( numeral.empty() ) {
                throw new ParsingException("Illegal expression");
            } else {
                try {
                    while( !operand.empty() ){
                        Op CurOp = (Op) operand.peek();
                        elem_op( numeral, CurOp );
                        operand.pop();
                    }
                }
                catch (ParsingException ex){
                    throw ex;
                }

                return ((Num) numeral.peek()).num;
            }
        }
}


