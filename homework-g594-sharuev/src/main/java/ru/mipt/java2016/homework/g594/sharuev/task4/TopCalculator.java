package ru.mipt.java2016.homework.g594.sharuev.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

@Component
public class TopCalculator implements ru.mipt.java2016.homework.base.task1.Calculator {

    private Stack<Double> numbers = new Stack<>();
    private StringBuilder sb = new StringBuilder();
    private Stack<Operator> operators = new Stack<>();
    private Stack<PredefinedFunction> predefinedFunctions = new Stack<>();
    private boolean unary;
    private ParserState state;
    private int i;
    private String expr;

    private static TopCalculator innerCalculator;

    static {
        innerCalculator = new TopCalculator();
    }

    @Autowired
    private Dao dao;

    public double calculate(String expression) throws ParsingException {

        /*TopCalculatorFunction main = new TopCalculatorFunction(expression, new ArrayList<String>(), 0);*/
        sb.setLength(0);
        numbers.clear();
        operators.clear();
        if (expression == null) {
            throw new ParsingException("Null expression");
        } else if (expression.equals("")) {
            throw new ParsingException("Empty string");
        }

        return eval(expression, new HashMap<>());

        //return evalFunction(main, new HashMap<>());
    }

    private enum ParserState {
        NUMBER {
            @Override
            boolean isAcceptableChar(char c) {
                return Character.isDigit(c) || c == '.';
            }
        }, OPERATOR {
            @Override
            boolean isAcceptableChar(char c) {
                //return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
                return true;
            }
        }, LETTERS {
            @Override
            boolean isAcceptableChar(char c) {
                return Character.isAlphabetic(c) || Character.isDigit(c) || c == '_';
            }
        }, PARAMETER {
            @Override
            boolean isAcceptableChar(char c) {
                return c != ',' && c != ')'; // T-O-D-O: такое прокатывает только когда параметр тупо число
            }
        }, NONE {
            @Override
            boolean isAcceptableChar(char c) {
                return false;
            }
        };

        abstract boolean isAcceptableChar(char c);
    }

    private void performOperation(Operator oper) throws ParsingException {
        double[] args = new double[oper.getArity()];
        for (int j = 0; j < args.length; ++j) {
            if (numbers.isEmpty()) {
                throw new ParsingException(
                        String.format("Not enough operands for operator %s",
                                "a")); // T-O-D-O: в обратном направлении
            }
            args[j] = numbers.pop();
        }
        numbers.push(oper.evaluate(args));
    }

    private void pushBuffer() throws ParsingException {
        if (sb.length() == 0) {
            return;
        }
        switch (state) {
            case NUMBER:
                try {
                    numbers.push(Double.parseDouble(sb.toString()));
                } catch (NumberFormatException e) {
                    throw new ParsingException(
                            String.format("Number \"%s\" is not valid", sb.toString()));
                }

                sb.setLength(0);
                unary = false;
                break;
            case LETTERS:
                boolean accepted = false;
                TopCalculatorVariable var = dao.loadVariable(sb.toString());
                if (var != null) {
                    numbers.push(var.getValue());
                    accepted = true;
                }

                PredefinedFunction predef = PredefinedFunction.getFunction(sb.toString());
                if (predef != null) {
                    sb.setLength(0);
                    state = ParserState.PARAMETER;
                    predefinedFunctions.push(predef);
                    break;
                }

                if (!accepted) {
                    throw new ParsingException(String.format("Unknown literal %s", sb.toString()));
                }
                sb.setLength(0);
                state = ParserState.NONE;
                break;
            case OPERATOR:
                String operatorStr = unary ? "U" + sb.toString() : sb.toString();
                Operator operator = Operator.getOperator(operatorStr);

                if (operator == null) {
                    throw new ParsingException(
                            String.format("Unknown operator \"%s\"", operatorStr));
                }
                if (operator == Operator.LBRACKET) {
                    if (state != ParserState.PARAMETER) {
                        operators.push(Operator.LBRACKET);
                    }
                    unary = true;
                } else if (operator == Operator.RBRACKET) {

                    while (!operators.isEmpty() && operators.peek() != Operator.LBRACKET) {
                        performOperation(operators.pop());
                    }
                    if (!operators.empty()) {
                        operators.pop(); // вытащить (
                    } else {
                        throw new ParsingException("Closing bracket without opening one");
                    }
                    unary = false;

                } else if (operator == Operator.UNARY_PLUS && operators.peek() == Operator.UNARY_PLUS) {
                    throw new ParsingException("Two unary + in a row");
                } else if (operator == Operator.COMMA) {
                    // T-O-D-O: вычислить
                    numbers.push(Double.parseDouble(sb.toString()));
                    unary = true;
                } else {
                    while (!operators.empty() && operators.peek() != Operator.LBRACKET
                            && ((operators.peek().getAssociativity() == Operator.Associativity.LEFT) ?
                            (operator.getPriority() <= operators.peek().getPriority()) :
                            (operator.getPriority() < operators.peek().getPriority()))) {
                        performOperation(operators.pop());
                    }
                    operators.push(operator);
                    unary = true;
                }

                sb.setLength(0);
                state = ParserState.NONE;

                break;
            case PARAMETER:
                Operator oper = Operator.getOperator(sb.toString());
                if (oper == null) {
                    // T-O-D-O: вычислить
                    numbers.push(Double.parseDouble(sb.toString()));

                    PredefinedFunction func = predefinedFunctions.pop();
                    ArrayList<Double> args = new ArrayList<>();
                    for (int j = 0; j < func.getArity(); ++j) {
                        if (numbers.size() == 0) {
                            throw new ParsingException(
                                    String.format("Not enough arguments for function % s" /*,func.getName()*/));
                        }
                        args.add(numbers.pop());

                    }
                    numbers.push(func.evaluate(args));
                } else {
                    switch (oper) {
                        case LBRACKET:
                            break;
                        case RBRACKET:
                            state = ParserState.NONE;
                            break;
                        default:
                            // nop
                    }
                }

                sb.setLength(0);
            default:
                // nop
        }
    }

    private double eval(String str, Map<String, Double> args) throws ParsingException {
        state = ParserState.NONE;
        unary = true;
        expr = str;
        for (i = 0; i < expr.length(); ++i) {
            char c = expr.charAt(i);

            // Если это кусок параметра, то дописываем.
            if (state == ParserState.PARAMETER && ParserState.PARAMETER.isAcceptableChar(c)) {
                sb.append(c);
                continue;
            }

            // Число, если оно не относится к литералу
            if (ParserState.NUMBER.isAcceptableChar(c) && state != ParserState.LETTERS) {
                if (state == ParserState.NONE) {
                    pushBuffer();
                    state = ParserState.NUMBER;
                }
                sb.append(c);
                continue;
            }

            // Пробел
            if (Character.isWhitespace(c)) {
                if (state != ParserState.NONE) {
                    pushBuffer();
                    state = ParserState.NONE;
                }
                continue;
            }

            // Литерал из букв и цифр (начинается всегда с буквы) читаем до пробела или оператора.
            if (ParserState.LETTERS.isAcceptableChar(c)) {
                if (state != ParserState.LETTERS) {
                    pushBuffer();
                    state = ParserState.LETTERS;
                    unary = false;
                }
                sb.append(c);
                continue;
            }

            // Если это не буква, не цифра и не пробел, то это оператор.
            if (ParserState.OPERATOR.isAcceptableChar(c)) {
                if (state != ParserState.OPERATOR) {
                    pushBuffer();
                    if (state != ParserState.PARAMETER) {
                        state = ParserState.OPERATOR;
                    }
                } /*else if (Operator.getOperator(Character.toString(c)) != null) {
                    pushBuffer();
                    state = ParserState.OPERATOR;
                    unary = false;
                }*/
                sb.append(c);
                if (Operator.getOperator(sb.toString()) != null) {
                    pushBuffer();
                }
                continue;
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

        },
        COMMA(0, 0, Associativity.LEFT) {

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

        private int arity;
        private int priority;
        private Associativity associativity;
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

    private enum PredefinedFunction {
        SIN(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.sin(args.get(0));
            }
        },
        COS(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.cos(args.get(0));
            }
        },
        TG(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.tan(args.get(0));
            }
        },
        SQRT(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.sqrt(args.get(0));
            }
        },
        POW(2) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.pow(args.get(0), args.get(1));
            }
        },
        ABS(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.abs(args.get(0));
            }
        },
        SIGN(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.signum(args.get(0));
            }
        },
        LOG(2) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.log(args.get(0)) / Math.log(args.get(1));
            }
        },
        LOG2(1) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.log(args.get(0)) / Math.log(2);
            }
        },
        RND(0) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.random();
            }
        },
        MAX(2) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.max(args.get(0), args.get(1));
            }
        },
        MIN(2) {
            @Override
            Double evaluate(List<Double> args) {
                return Math.min(args.get(0), args.get(1));
            }
        };

        public static PredefinedFunction getFunction(String funcName) {
            return funcs.get(funcName);
        }

        PredefinedFunction(int arity) {
            this.arity = arity;
        }

        private static HashMap<String, PredefinedFunction> funcs;
        private int arity;

        static {
            funcs = new HashMap<>();
            funcs.put("sin", PredefinedFunction.SIN);
            funcs.put("cos", PredefinedFunction.COS);
            funcs.put("tg", PredefinedFunction.TG);
            funcs.put("sqrt", PredefinedFunction.SQRT);
            funcs.put("pow", PredefinedFunction.POW);
            funcs.put("abs", PredefinedFunction.ABS);
            funcs.put("sign", PredefinedFunction.SIGN);
            funcs.put("log", PredefinedFunction.LOG);
            funcs.put("log2", PredefinedFunction.LOG2);
            funcs.put("rnd", PredefinedFunction.RND);
            funcs.put("max", PredefinedFunction.MAX);
            funcs.put("min", PredefinedFunction.MIN);
        }

        abstract Double evaluate(List<Double> args);

        public int getArity() {
            return arity;
        }
    }

    private double evalFunction(TopCalculatorFunction function,
                                Map<String, Double> args) throws ParsingException {
        if (args.size() != function.getArity()) {
            throw new ParsingException("");
        }

        sb.setLength(0);
        numbers.clear();
        operators.clear();
        if (function.getFunc() == null) {
            throw new ParsingException("Null expression");
        } else if (function.getFunc().equals("")) {
            throw new ParsingException("Empty string");
        }

        return eval(function.getFunc(), args);
    }

}
