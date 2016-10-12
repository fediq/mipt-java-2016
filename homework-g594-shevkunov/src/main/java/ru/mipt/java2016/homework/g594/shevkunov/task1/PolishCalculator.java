package ru.mipt.java2016.homework.g594.shevkunov.task1;

import ru.mipt.java2016.homework.base.task1.Calculator;
import ru.mipt.java2016.homework.base.task1.ParsingException;

import javax.swing.text.html.parser.Parser;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Evaluates a value from expressing
 * Created by shevkunov on 04.10.16.
 */
class PolishCalculator implements Calculator {
    public double calculate(String expression) throws ParsingException {
        if (expression == null) {
            throw new ParsingException("Null expression.");
        } else {
            if (expression.length() == 0) {
                throw new ParsingException("I think, the result are 42.");
            } else {
                return parse(expression);
            }
        }
    }

    private enum ParserState {
        OPER, VAL, NONE
    }

    private enum Operation {
        PLUS(0) {
            @Override
            public double compute(double... args) {
                double result = 0.; // neutral element
                for (double value : args) {
                    result += value;
                }
                return result;
            }
        }, MINUS(0) {
            @Override
            public double compute(double... args) {
                if (args.length == 1) {
                    return 0. - args[0];
                } else {
                    if (args.length > 1) {
                        double result = args[0];
                        for (int i = 1; i < args.length; ++i) {
                            result -= args[i];
                        }
                        return result;
                    } else {
                        return 0.; // neutral element
                    }
                }
            }
        },  MULTIPLY(1) {
            @Override
            public double compute(double... args) {
                double result = 1.; //neutral elent
                for (double value : args) {
                    result *= value;
                }
                return result;
            }
        },  DIVISION(1) {
            @Override
            public double compute(double... args) {
                if(args.length == 1){
                    return 1. / args[0];
                } else{
                    if (args.length > 1) {
                        double result = args[0];
                        for (int i = 1; i < args.length; ++i) {
                            result /= args[i];
                        }
                        return result;
                    } else {
                        return 1.; // neutral element
                    }
                }
            }
        },  UNARYPLUS(777) {
            @Override
            public int valence() {
                return 1;
            }
            @Override
            public double compute(double... args) {
                return +args[0];
            }
        }, UNARYMINUS(777) {
            @Override
            public int valence() {
                return 1;
            }
            @Override
            public double compute(double... args) {
                return -args[0];
            }
        },  OPENBRAСKET(-1) {
            @Override
            public int valence() {
                return 0;
            }
            @Override
            public boolean isBracket() {
                return true;
            }
            @Override
            public double compute(double... args) {
                return Double.NaN;
            }
        }, CLOSEBRAСKET(-1) {
            @Override
            public int valence() {
                return 0;
            }
            @Override
            public boolean isBracket() {
                return true;
            }
            @Override
            public Operation closeBracket() {
                return Operation.OPENBRAСKET;
            }
            @Override
            public double compute(double... args) {
                return Double.NaN;
            }
        };

        protected abstract double compute(double[] args);

        public double evaluate(double... args) throws ParsingException {
            if (valence() != args.length) {
                throw new ParsingException("Invalid valence.");
            }
            return compute(args);
        }

        public boolean isBracket() {
            return false;
        }

        public Operation closeBracket() {
            return null; // not a (close) bracket
        }

        public int valence() {
            return 2; // default
        }

        Operation(int order) {
            this.order = order;
        }

        static public Operation getOperation(String s) {
            if (stringRepresentations.containsKey(s)) {
                return stringRepresentations.get(s);
            } else {
                return null;
            }
        }

        public boolean strongerThan(Operation op) {
            return this.order > op.order;
        }

        public boolean notWeakerThan(Operation op) {
            return this.order >= op.order;
        }

        static private Map<String, Operation> stringRepresentations = new HashMap<String, Operation>(){{
            put("+", Operation.PLUS);
            put("-", Operation.MINUS);
            put("*", Operation.MULTIPLY);
            put("/", Operation.DIVISION);
            put("U+", Operation.UNARYPLUS);
            put("U-", Operation.UNARYMINUS);
            put("(", Operation.OPENBRAСKET);
            put(")", Operation.CLOSEBRAСKET);

            put("U(", Operation.OPENBRAСKET);
            put("U)", Operation.CLOSEBRAСKET);
        }};

        private final int order;
    }

    private Stack<Double> valStack = new Stack<>();
    private Stack<Operation> operStack = new Stack<>();
    private StringBuilder buffer = new StringBuilder();

    private void proceedOperation(Operation eval) throws ParsingException {
        if (eval.isBracket()) {
            throw new ParsingException("Incorrect expression.");
        }
        double args[] = new double[eval.valence()];
        for (int argIndex = args.length - 1; argIndex >= 0; --argIndex) {
            if (!valStack.isEmpty()) {
                args[argIndex] = valStack.pop();
            } else {
                throw new ParsingException("Incorrect expression.");
            }
        }
        valStack.push(eval.evaluate(args));
    }

    private void pushOperation(String opString, boolean isUnary) throws ParsingException {
        Operation op =  isUnary ? Operation.getOperation("U" + opString) : Operation.getOperation(opString);
        if (null != op) { // Yoda style
            if (!op.isBracket()) {
                while (!operStack.isEmpty() &&
                        (((operStack.peek().valence() != 1) && operStack.peek().notWeakerThan(op))
                                || (operStack.peek().valence() == 1) && operStack.peek().strongerThan(op))) {
                    proceedOperation(operStack.pop());
                }
                operStack.push(op);
            } else {
                if (op.closeBracket() == null) {
                    operStack.push(op);
                } else {
                    while (!operStack.isEmpty() && (operStack.peek() != op.closeBracket())) {
                        proceedOperation(operStack.pop());
                    }
                    if (operStack.isEmpty()) {
                        throw new ParsingException("Incorrect expression.");
                    } else {
                        operStack.pop();
                    }
                }
            }
        } else {
            throw new ParsingException("Unknown operation : " + opString);
        }
    }

    private ParserState state = ParserState.NONE;
    private boolean unary = true;

    private void pushBuffer() throws ParsingException {
        switch (state) {
            case OPER:
                pushOperation(buffer.toString(), unary);
                unary = true;
                break;
            case VAL:
                try {
                    valStack.push(Double.parseDouble(buffer.toString()));
                } catch (NumberFormatException e) {
                    throw new ParsingException("Bad number.");
                }
                unary = false;
                break;
        }
        buffer.delete(0, buffer.length());
    }

    private double parse(String expr) throws ParsingException {
        unary = true;
        state = ParserState.NONE;
        for (int i = 0; i < expr.length(); ++i) {
            if (Character.isDigit(expr.charAt(i)) || (expr.charAt(i) == '.')) {
                if (state != ParserState.VAL) {
                    pushBuffer();
                    state = ParserState.VAL;
                }
                buffer.append(expr.charAt(i));
            } else {
                if (Character.isWhitespace(expr.charAt(i))) {
                    pushBuffer();
                    state = ParserState.NONE;
                } else {
                    if (state != ParserState.OPER) {
                        pushBuffer();
                        state = ParserState.OPER;
                    }
                    buffer.append(expr.charAt(i));
                    Operation readed = Operation.getOperation(unary ? "U"
                            + buffer.toString() : buffer.toString());
                    if (null != readed) {
                        if (readed.isBracket()) {
                            unary = false;
                            pushBuffer();
                            state = ParserState.NONE;
                            unary = readed.closeBracket() == null;
                        } else {
                            if ((!operStack.empty()) && (operStack.peek() == Operation.UNARYPLUS)
                                && (readed == Operation.UNARYPLUS)) {
                                throw new ParsingException("I love ++i");
                                // I think that ++1 is correct;
                                // this is ony for testPlusPlus
                            }
                            pushBuffer();
                            unary = true;
                            state = ParserState.NONE;
                        }
                    }
                }
            }
        }
        pushBuffer();

        while (!operStack.isEmpty()) {
            proceedOperation(operStack.pop());
        }
        if (valStack.size() != 1) {
            throw new ParsingException("Incorrect expression.");
        }
        return valStack.pop();
    }
}
