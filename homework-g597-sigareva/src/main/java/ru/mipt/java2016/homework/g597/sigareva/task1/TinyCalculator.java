package ru.mipt.java2016.homework.g597.sigareva.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.Stack;
import java.util.Vector;

public class TinyCalculator implements Calculator {

    public static void main(String[] args) {
        String expression = " 1 + 2 + 3";
//        TinyCalculator calculator = new TinyCalculator(expression);
        TinyCalculator calculator = new TinyCalculator();
        try {
            System.out.println(calculator.calculate(expression));
        } catch (ParsingException e) {
            System.out.println(e.toString());
        }
    }

//    public TinyCalculator(final String expression) {
//        try {
//            this.calculate(expression);
//        } catch (ParsingException e) {
//            System.out.println(e.toString());
//        }
//    }

    @Override
    public double calculate(final String expression) throws ParsingException {


        String numbers = "0123456789";
        String operations = "+-/*";

        StringBuilder new_expression = new StringBuilder(expression.subSequence(0, expression.length()));

        StringBuilder new_number = new StringBuilder();

        Stack<Character> first_stack = new Stack<>();
        Stack<String> second_stack = new Stack<>();

        boolean number_began = false;
        boolean double_number = false;
        boolean space_wrote = false;
        boolean operation_has_done = false;

        for (int i = new_expression.length() - 1; i >= 0; --i) {

        }

        for (int i = 0; i < new_expression.length(); ++i) {

            boolean is_operation = false;

            for (int j = 0; j < operations.length(); ++j) {
                if (new_expression.charAt(i) == operations.charAt(j)) {
                    is_operation = true;
                    break;
                }
            }

            boolean is_digit = false;

            for (int j = 0; j < numbers.length(); ++j) {
                if (new_expression.charAt(i) == numbers.charAt(j)) {
                    is_digit = true;
                    break;
                }
            }

            if (new_expression.charAt(i) == '(') {
                space_wrote = false;
                if (number_began) {
                    throw new ParsingException("Cannot resolve");
                } else {
                    first_stack.add(new_expression.charAt(i));
                    operation_has_done = false;
                }
            }
            else {
                if (new_expression.charAt(i) == ')') {
                    if (space_wrote)
                    space_wrote = false;
                    if (!number_began) {
                        throw new ParsingException("Cannot resolve");
                    }
                    else {
                        operation_has_done = false;
                        second_stack.add(new_number.toString());
                        new_number.setLength(0);
                        number_began = false;
                        double_number = false;

                        while (first_stack.size() > 0) {
                            if (first_stack.lastElement() == '(') {
                                first_stack.removeElementAt(first_stack.size() - 1);
                                break;
                            } else {
                                second_stack.add(Character.toString(first_stack.elementAt(first_stack.size() - 1)));
                                first_stack.removeElementAt(first_stack.size() - 1);
                            }
                        }
                    }
                } else {
                    if (is_operation && number_began) {
                        operation_has_done = true;
                        space_wrote = false;
                        second_stack.add(new_number.toString());
                        new_number.setLength(0);
                        number_began = false;
                        double_number = false;

                        if (new_expression.charAt(i) == '+') {
                            while (first_stack.size() > 0) {
                                if (first_stack.lastElement() == '(') {
                                    break;
                                } else {
                                    second_stack.add(Character.toString(first_stack.elementAt(first_stack.size() - 1)));
                                    first_stack.removeElementAt(first_stack.size() - 1);
                                }
                            }
                            first_stack.add(new_expression.charAt(i));
                        }
                        if (new_expression.charAt(i) == '-') {
                            while (first_stack.size() > 0) {
                                if (first_stack.lastElement() == '(') {
                                    break;
                                } else {
                                    second_stack.add(Character.toString(first_stack.elementAt(first_stack.size() - 1)));
                                    first_stack.removeElementAt(first_stack.size() - 1);
                                }
                            }
                            first_stack.add(new_expression.charAt(i));
                        }
                        if (new_expression.charAt(i) == '*') {
                            while (first_stack.size() > 0) {
                                if (first_stack.lastElement() == '(' || first_stack.lastElement() == '+' || first_stack.lastElement() == '-') {
                                    break;
                                } else {
                                    second_stack.add(Character.toString(first_stack.elementAt(first_stack.size() - 1)));
                                    first_stack.removeElementAt(first_stack.size() - 1);
                                }
                            }
                            first_stack.add(new_expression.charAt(i));
                        }
                        if (new_expression.charAt(i) == '/') {
                            while (first_stack.size() > 0) {
                                if (first_stack.lastElement() == '(' || first_stack.lastElement() == '+' || first_stack.lastElement() == '-') {
                                    break;
                                } else {
                                    second_stack.add(Character.toString(first_stack.elementAt(first_stack.size() - 1)));
                                    first_stack.removeElementAt(first_stack.size() - 1);
                                }
                            }
                            first_stack.add(new_expression.charAt(i));
                        }
                    } else {
                        if (is_digit) {
                            if (number_began && !space_wrote) {
                                new_number.append(new_expression.charAt(i));
                            } else {
                                if(!number_began) {
                                    operation_has_done = false;
                                    number_began = true;
                                    new_number.append(new_expression.charAt(i));
                                }
                                else{
                                    throw new ParsingException("Cannot resolve");
                                }
                            }
                        } else {
                            if (new_expression.charAt(i) == '.' && !double_number && number_began) {
                                new_number.append(new_expression.charAt(i));
                                double_number = true;
                            } else {
                                if (new_expression.charAt(i) == ' ') {
                                    if (number_began) {
                                        second_stack.add(new_number.toString());
                                        new_number.setLength(0);
                                        number_began = false;
                                        space_wrote = true;
                                        double_number = false;
                                    }
                                } else {
                                    throw new ParsingException("Cannot resolve");
                                }
                            }
                        }
                    }
                }
            }
        }

        if (operation_has_done){
            throw new ParsingException("Cannot resolve");
        }

        if (new_number.length() > 0) {
            second_stack.add(new_number.toString());
        }

        while (first_stack.size() > 0){
            boolean flag = false;
            for (int j = 0; j < operations.length(); ++j){
                if (first_stack.lastElement() == operations.charAt(j)){
                    flag = true;
                    second_stack.add(Character.toString(first_stack.lastElement()));
                    first_stack.remove(first_stack.size() - 1);
                    break;
                }
            }
            if (!flag){
                throw new ParsingException("Cannot resolve");
            }
        }

        for (String s : second_stack) {
            System.out.print(s + " ");
        }

        System.out.println();

        Vector<Double> result = new Vector<>();

        for (int j = 0; j < second_stack.size(); ++j){

            boolean operation_was_done = false;

            if (second_stack.elementAt(j).equals("+")){
                double first_number = result.elementAt(result.size() - 2);
                double second_number = result.elementAt(result.size() - 1);

                result.removeElementAt(result.size() - 1);
                result.removeElementAt(result.size() - 1);

                result.add(first_number + second_number);
                operation_was_done = true;
            }
            if (second_stack.elementAt(j).equals("-")){
                double first_number = result.elementAt(result.size() - 2);
                double second_number = result.elementAt(result.size() - 1);

                result.removeElementAt(result.size() - 1);
                result.removeElementAt(result.size() - 1);

                result.add(first_number - second_number);
                operation_was_done = true;
            }
            if (second_stack.elementAt(j).equals("*")){
                double first_number = result.elementAt(result.size() - 2);
                double second_number = result.elementAt(result.size() - 1);

                result.removeElementAt(result.size() - 1);
                result.removeElementAt(result.size() - 1);

                result.add(first_number * second_number);
                operation_was_done = true;
            }
            if (second_stack.elementAt(j).equals("/")){
                double first_number = result.elementAt(result.size() - 2);
                double second_number = result.elementAt(result.size() - 1);

                result.removeElementAt(result.size() - 1);
                result.removeElementAt(result.size() - 1);

                result.add(first_number / second_number);
                operation_was_done = true;
            }
            if (!operation_was_done){
                result.add(Double.parseDouble(second_stack.elementAt(j)));
            }
        }

        return result.lastElement();
    }
}
