package ru.mipt.java2016.homework.g594.sharuev.task4;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.HashMap;
import java.util.Stack;


public class TopCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression");
        } else if (expression.equals("")) {
            throw new ParsingException("Empty string");
        }

        return eval(expression);
    }

    private enum ParserState {
        NUMBER, LITERAL, NONE
    }

    private Stack<Double> numbers = new Stack<>();
    private StringBuilder sb = new StringBuilder();
    private Stack<Operator> operators = new Stack<>();
    private boolean unary;
    private ParserState state;

    private void performOperation(Operator oper) throws ParsingException {
        double[] args = new double[oper.getArity()];
        for (int i = 0; i < args.length; ++i) {
            if (numbers.isEmpty()) {
                throw new ParsingException("Not enough operands for operator %s"); // TODO
            }
            args[i] = numbers.pop();
        }
        numbers.push(oper.evaluate(args));
    }

    private void pushBuffer() throws ParsingException {
        switch (state) {
            case NUMBER:
                numbers.push(Double.parseDouble(sb.toString()));
                sb.setLength(0);
                unary = false;
            case LITERAL:
                String operatorStr = unary ? "U" + sb.toString() : sb.toString();
                Operator operator = Operator.getOperator(operatorStr);
                if (operator == null) {
                    throw new ParsingException(
                            String.format("Unknown operator \"%s\"", operatorStr));
                }

                if (operator == Operator.RBRACKET) {
                    if (operators.empty()) {
                        throw new ParsingException("Closing bracket without opening one");
                    }
                    while (operators.peek() != Operator.LBRACKET) {
                        pushBuffer();
                        if (operators.empty()) {
                            throw new ParsingException("Closing bracket without opening one");
                        }
                    }
                    operators.pop(); // Remove ( from stack.
                }

                while (!operators.empty() && operators.peek() != Operator.LBRACKET
                        && ((operators.peek().getAssociativity() == Operator.Associativity.LEFT) ?
                        (operator.getPriority() <= operators.peek().getPriority()) :
                        (operator.getPriority() < operators.peek().getPriority()))) {
                    performOperation(operators.pop());
                }


        }
    }

    private double eval(String str) throws ParsingException {
        for (int i = 0; i < sb.length(); ++i) {
            char c = str.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                if (state != ParserState.NUMBER) {
                    pushBuffer();
                    state = ParserState.NUMBER;
                }
                sb.append(c);
                continue;
            }

            if (Character.isWhitespace(c)) {
                if (state != ParserState.NONE) {
                    pushBuffer();
                    state = ParserState.NONE;
                }
                continue;
            }

            if (state != ParserState.LITERAL) {
                pushBuffer();
                state = ParserState.LITERAL;
            }
            sb.append(c);

            Operator oper = Operator.getOperator(sb.toString());
            if (oper == Operator.LBRACKET) {
                unary = true;
                pushBuffer();
                operators.push(Operator.LBRACKET);
                continue;
            }
            if (oper == Operator.UNARY_PLUS&& operators.peek() == Operator.UNARY_PLUS) {
                throw new ParsingException("Two unary + in a row");
            }
            unary = true;
            state = ParserState.NONE;
        }
        pushBuffer();
        while (!operators.empty()) {
            if (operators.peek() == Operator.LBRACKET) {
                throw new ParsingException("No closing bracket");
            }

        }
        return numbers.peek();
    }

    private enum Operator {
        PLUS(2, 1, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[0] + args[1];
            }
        },
        MINUS(2, 1, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[0] - args[1];
            }
        },
        MULTIPLY(2, 2, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[0] * args[1];
            }
        },
        DIVIDE(2, 2, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[0] / args[1];
            }
        },
        POWER(2, 3, Associativity.RIGHT) {
            @Override
            double evaluate(double... args) {
                return Math.pow(args[0], args[1]);
            }
        },
        UNARY_MINUS(1, 4, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return -args[0];
            }
        },
        UNARY_PLUS(1, 4, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[0];
            }
        },
        LBRACKET(1, 0, Associativity.LEFT) {

        },
        RBRACKET(1, 0, Associativity.LEFT) {

        };

        private static Operator getOperator(String operStr) {
            return opers.get(operStr);
        }

        private enum Associativity {
            LEFT, RIGHT, NONE
        }

        double evaluate(double... args) {
            throw new UnsupportedOperationException();
        }

        Associativity getAssociativity() {
            return associativity;
        }

        int getPriority() {
            return priority;
        }

        int getArity() {
            return arity;
        }

        Operator(int arity, int priority, Associativity associativity) {
            this.arity = arity;
            this.priority = priority;
            this.associativity = associativity;
        }

        static {
            HashMap<String, Operator> opers = new HashMap<>();
            opers.put("+", Operator.PLUS);
            opers.put("-", Operator.MINUS);
            opers.put("*", Operator.MULTIPLY);
            opers.put("/", Operator.DIVIDE);
            opers.put("^", Operator.POWER);
            opers.put("U-", Operator.UNARY_MINUS);
        }

        int arity;
        int priority;
        Associativity associativity;
        private static HashMap<String, Operator> opers;
    }
}
