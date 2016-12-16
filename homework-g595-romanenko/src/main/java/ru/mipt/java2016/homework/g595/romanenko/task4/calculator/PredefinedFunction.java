package ru.mipt.java2016.homework.g595.romanenko.task4.calculator;

import ru.mipt.java2016.homework.base.task1.ParsingException;

import java.util.*;

/**
 * ru.mipt.java2016.homework.g595.romanenko.task4
 *
 * @author Ilya I. Romanenko
 * @since 27.11.16
 **/
public class PredefinedFunction implements IEvaluateFunction {

    public enum PredefinedFunctionType {
        SIN, COS, TG, SQRT, POW, ABS,
        SIGN, LOG, LOG2, RND, MAX, MIN
    }

    public static final Map<PredefinedFunctionType, Integer> PREDEFINED_FUNCTION_VALENCY;

    static {
        Map<PredefinedFunctionType, Integer> tmpValency = new HashMap<>();
        tmpValency.put(PredefinedFunctionType.SIN, 1);
        tmpValency.put(PredefinedFunctionType.COS, 1);
        tmpValency.put(PredefinedFunctionType.TG, 1);
        tmpValency.put(PredefinedFunctionType.SQRT, 1);
        tmpValency.put(PredefinedFunctionType.POW, 2);
        tmpValency.put(PredefinedFunctionType.ABS, 1);
        tmpValency.put(PredefinedFunctionType.SIGN, 1);
        tmpValency.put(PredefinedFunctionType.LOG, 1);
        tmpValency.put(PredefinedFunctionType.LOG2, 1);
        tmpValency.put(PredefinedFunctionType.RND, 0);
        tmpValency.put(PredefinedFunctionType.MAX, 2);
        tmpValency.put(PredefinedFunctionType.MIN, 2);
        PREDEFINED_FUNCTION_VALENCY = Collections.unmodifiableMap(tmpValency);
    }

    private final PredefinedFunctionType currentFunctionType;
    private final List<Double> args = new ArrayList<>();
    private static final Random RANDOM = new Random(42);

    public PredefinedFunction(PredefinedFunctionType currentFunctionType) {
        this.currentFunctionType = currentFunctionType;
        int valency = PREDEFINED_FUNCTION_VALENCY.get(currentFunctionType);
        for (int i = 0; i < valency; i++) {
            args.add(0.0);
        }
    }

    @Override
    public Double evaluate() throws ParsingException {
        Double result = 0.0;
        switch (currentFunctionType) {
            case SIN:
                result = Math.sin(args.get(0));
                break;
            case COS:
                result = Math.cos(args.get(0));
                break;
            case TG:
                result = Math.tan(args.get(0));
                break;
            case SQRT:
                result = Math.sqrt(args.get(0));
                break;
            case POW:
                result = Math.pow(args.get(0), args.get(1));
                break;
            case ABS:
                result = Math.abs(args.get(0));
                break;
            case SIGN:
                result = Math.signum(args.get(0));
                break;
            case LOG:
                result = Math.log(args.get(0));
                break;
            case LOG2:
                result = Math.log(args.get(0)) / Math.log(2);
                break;
            case RND:
                synchronized (RANDOM) {
                    result = RANDOM.nextDouble();
                }
                break;
            case MAX:
                result = Math.max(args.get(0), args.get(1));
                break;
            case MIN:
                result = Math.min(args.get(0), args.get(1));
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    public void setArgs(List<Double> args) throws ParsingException {
        if (this.args.size() != args.size()) {
            throw new ParsingException("Amount of function's args doesn't match.");
        }
        for (int i = 0; i < args.size(); i++) {
            this.args.set(i, args.get(i));
        }
    }

    @Override
    public boolean isPredefined() {
        return true;
    }
}
