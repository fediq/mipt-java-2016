package ru.mipt.java2016.homework.g594.sharuev.task4;

import org.slf4j.Logger;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class TopCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

    private Logger LOG;

    public double calculate(String expression) throws ParsingException {
        sb.setLength(0);
        numbers.clear();
        operators.clear();
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
                throw new ParsingException(
                        String.format("Not enough operands for operator %s", "a")); // TODO
            }
            args[i] = numbers.pop();
        }
        numbers.push(oper.evaluate(args));
    }

    private void pushBuffer() throws ParsingException {
        switch (state) {
            case NUMBER:
                try {
                    numbers.push(Double.parseDouble(sb.toString()));
                } catch (NumberFormatException e) {
                    throw new ParsingException(
                            String.format("Number \"%s\" is not valid", sb.toString()));
                }
                //LOG.trace("Push number "+numbers.peek());
                sb.setLength(0);
                unary = false;
                break;
            case LITERAL:
                String operatorStr = unary ? "U" + sb.toString() : sb.toString();
                Operator operator = Operator.getOperator(operatorStr);
                if (operator == null) {
                    throw new ParsingException(
                            String.format("Unknown operator \"%s\"", operatorStr));
                }

                if (operator == Operator.RBRACKET) {
                    while (!operators.isEmpty() && operators.peek() != Operator.LBRACKET) {
                        performOperation(operators.pop());
                    }
                    if (!operators.empty()) {
                        operators.pop(); // вытащить (
                    } else {
                        throw new ParsingException("Closing bracket without opening one");
                    }
                    unary = false;
                } else {
                    while (!operators.empty() && operators.peek() != Operator.LBRACKET
                            && ((operators.peek().getAssociativity() == Operator.Associativity.LEFT) ?
                            (operator.getPriority() <= operators.peek().getPriority()) :
                            (operator.getPriority() < operators.peek().getPriority()))) {
                        performOperation(operators.pop());
                    }
                    operators.push(operator);
                    state = ParserState.NONE;
                    unary = true;
                }
                sb.setLength(0);
                break;
            default:
                // nop
        }
    }

    private double eval(String str) throws ParsingException {
        state = ParserState.NONE;
        unary = true;
        for (int i = 0; i < str.length(); ++i) {
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
            if (oper != null) {
                if (oper == Operator.LBRACKET) {
                    unary = false; // скобку пушим не унарную

                    //operators.push(Operator.LBRACKET);
                    pushBuffer();
                    unary = true;
                    continue;
                }
                if (oper == Operator.UNARY_PLUS && operators.peek() == Operator.UNARY_PLUS) {
                    throw new ParsingException("Two unary + in a row");
                }
                pushBuffer();

                state = ParserState.NONE;
            }
        }
        pushBuffer();

        while (!operators.empty()) {
            if (operators.peek() == Operator.LBRACKET) {
                throw new ParsingException("No closing bracket");
            }
            performOperation(operators.pop());
        }
        if (numbers.isEmpty()) {
            throw new ParsingException("String consists of only whitespaces");
        }
        return numbers.peek();
    }

    private enum Operator {
        PLUS(2, 1, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[1] + args[0];
            }
        },
        MINUS(2, 1, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[1] - args[0];
            }
        },
        MULTIPLY(2, 2, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[1] * args[0];
            }
        },
        DIVIDE(2, 2, Associativity.LEFT) {
            @Override
            double evaluate(double... args) {
                return args[1] / args[0];
            }
        },
        POWER(2, 3, Associativity.RIGHT) {
            @Override
            double evaluate(double... args) {
                return Math.pow(args[1], args[0]);
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
        LBRACKET(1, 5, Associativity.LEFT) {

        },
        RBRACKET(1, 5, Associativity.LEFT) {

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

        int arity;
        int priority;
        Associativity associativity;
        private static HashMap<String, Operator> opers;

        static {
            opers = new HashMap<>();
            opers.put("+", Operator.PLUS);
            opers.put("-", Operator.MINUS);
            opers.put("*", Operator.MULTIPLY);
            opers.put("/", Operator.DIVIDE);
            opers.put("^", Operator.POWER);
            opers.put("U-", Operator.UNARY_MINUS);
            opers.put("U(", Operator.LBRACKET);
            opers.put("(", Operator.LBRACKET);
            opers.put("U)", Operator.RBRACKET);
            opers.put(")", Operator.RBRACKET);
        }
    }

    private HashMap<String, Double> variables;
    private HashMap<String, TopCalculatorFunction> functions;
    private HashMap<String, TopCalculatorFunction> predefinedFunctions;
    {
        variables = new HashMap<>();
        functions = new HashMap<>();
        predefinedFunctions = new HashMap<>();
    }

    {
        /*
        sin(a)
cos(a)
tg(a)
sqrt(a)
pow(m, e)
abs(a)
sign(a)
log(a, n)
log2(a)
rnd()
max(a, b)
min(a, b)
         */
        //predefinedFunctions.put()
    }

    public Double getVariable(String variableName) {
        return variables.get(variableName);
    }

    public boolean putVariable(String variableName,
                               String variableValueExpr) throws ParsingException {
        return variables.put(variableName, eval(variableValueExpr)) != null;
    }

    public boolean deleteVariable(String variableName) {
        return variables.remove(variableName) != null;
    }

    public List<String> getVariablesNames() {
        return new ArrayList<>(variables.keySet());
    }

    public TopCalculatorFunction getFunction(String name) {
        TopCalculatorFunction f = predefinedFunctions.get(name);
        if (f == null) {
            f = functions.get(name);
        }
        return f;
    }

    public boolean putFunction
            (String name, String body, List<String> args) throws ParsingException {

        if (predefinedFunctions.containsKey(name)) {
            throw new ParsingException(
                    String.format("Can't redefine predefined function \"%s\"", name));
        }

        return functions.put(name, new TopCalculatorFunction(body, args)) != null;
    }

    public boolean deleteFunction(String name) {
        return functions.remove(name) != null;
    }

    public List<String> getFunctionsNames() {
        return new ArrayList<>(functions.keySet());
    }

    private double evalFunction(TopCalculatorFunction function) {
        return 0;
    }


}
