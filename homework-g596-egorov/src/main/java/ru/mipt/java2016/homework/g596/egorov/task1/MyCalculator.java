package ru.mipt.java2016.homework.g596.egorov.task1;
/**
 * Created by евгений on 12.10.2016.
 */


import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;


import java.util.Stack;
import java.lang.Character;
import static java.lang.Character.isDigit;



public class MyCalculator implements Calculator {
    private Stack<Character> func = new Stack<>();
    private Stack<Double> operands = new Stack<>();

    @Override
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        }
        return (solution(expression));
    }

    private double do_it(Double x, Character oper, Double y) throws ParsingException
    {
        switch (oper) {
            case('+'):
                return y + x;
            case('-'):
                return y - x;
            case('*'):
                return y * x;
            case('/'):
                return y / x;
            default:
                throw new ParsingException("Wrong operator in do_it");
        }
    }
    private int priority(Character x) throws ParsingException
    {
        switch(x) {
            case '+':
            case '-':
                return 3;
            case '*':
            case '/':
                return 2;
            case '(':
                return 100;
            default:
                throw new ParsingException("Wrong operator in priority");
        }
    }


    private boolean handler(Character cur_sim) throws ParsingException  //Решает, что нужно делать с функ.символом
    {
        if (cur_sim == ')') {
            if (operands.size() == 0)
                throw new ParsingException("Too few operands!");
            while (func.peek() != '(') {
                if (operands.size() >= 2)
                    operands.push(do_it(operands.pop(), func.pop(), operands.pop()));
                else
                    throw new ParsingException("Too few operands!");
            }
            func.pop();
            return true;
        }

        if (priority(cur_sim) < priority(func.peek()) || cur_sim == '(') {
            func.push(cur_sim);
            return true;
        }

        if (priority(cur_sim) >= priority(func.peek())) {    //выталкивание
            if (operands.size() < 2)
                throw new ParsingException("Too few operands!");
            operands.push(do_it(operands.pop(), func.pop(), operands.pop()));
            func.push(cur_sim);

            Character save;
            save = func.pop();
            while (priority(save) >= priority(func.peek()) && operands.size() > 1) {
                operands.push(do_it(operands.pop(), func.pop(), operands.pop()));
            }
            func.push(save);

            return true;
        }
        return false;
    }

    private String del_spaces(String ex){
        String ans = "";
        Integer i = 0;
        while (ex.length() > i){
            if (Character.isWhitespace(ex.charAt(i))) {
                ++i;
                continue;
            }
            ans += ex.charAt(i);
            ++i;
        }
        return ans;
    }

    private boolean isoper(Character c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '(');
    }

    private int seeknum(Integer i, String s, Integer sign)throws ParsingException {
        //НА ВХОД ПОДАЁТСЯ НАЧАЛО ЧИСЛА
        //проходит по числу, запихивает его в стек и проверяет на точки
        Integer dot_count = 0;  //считывание числа
        String number = "";
        number += s.charAt(i);
        ++i;
        while (s.charAt(i) == '.' || isDigit(s.charAt(i))){
            if (s.charAt(i) == '.'){
                if (++dot_count > 1)    //с проверкой на кол-во точек
                    throw new ParsingException("Wrong input(dots)");
            }
            number += s.charAt(i);
            ++i;
        }
        if (sign == -1 && Double.parseDouble(number) == 0) {

            operands.push(-0.0);
        }
        else
            operands.push(Double.parseDouble(number) * sign);
        return i;   //Возвращается №позиции после числа
    }

    private double solution(String expression) throws ParsingException {
        String number = "";
        func.push('(');
        String s = '(' + del_spaces(expression) + ')';
        Integer RightBracketSum = 0; //не учитываем искусственные скобки(они могут повлиять на псп выражения)
        //Scanner scanner = new Scanner(s);
        int i = 1;
        while (s.length() > i) {
            char save_cur_ch = '?';
            if (isoper(s.charAt(i-1)) && (s.charAt(i) == '-') && (i+1 < s.length())) {
                if (s.charAt(i - 1) == '/') {// && s.charAt(i) == '*'){
                    save_cur_ch = s.charAt(i);  //если cur_ch иниц, то после него стоит "-"
                    ++i;
                    if (save_cur_ch == '-')
                        i = seeknum(i, s, -1);
                    else
                        i = seeknum(i, s, 1);
                } else
                    operands.push(0.0);
            }

            if (s.charAt(i) == ')' && i != s.length() - 1)  //Не учитывается последяя скобка(искусственная)
                --RightBracketSum;
            if (s.charAt(i) == '(')
                ++RightBracketSum;
            if (RightBracketSum < 0)
                throw new ParsingException("Wrong count of Bracket!");
            if (isDigit(s.charAt(i))){
                i = seeknum(i, s, 1);
                continue;
            }
            //До этого момента может дойти либо неправильный символ,
            //  либо оператор, либо скобка
            if (handler(s.charAt(i))){
                ++i;    //если там был оператор или скобка,
                        // то выполнилось необх. действие,
                        //если нет, то вызвалось исключение;
            }
        }

        if (operands.size() > 1 || func.size() > 0)
           throw new ParsingException("Too much braces!");
        return operands.pop();
    }
}
