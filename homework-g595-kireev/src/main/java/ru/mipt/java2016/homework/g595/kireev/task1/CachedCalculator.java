package ru.mipt.java2016.homework.g595.kireev.task1;

import net.sourceforge.jeval.Evaluator;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 * Created by sun on 18.12.16.
 */
public class CachedCalculator  extends WonderCalculator {
    public static final Calculator INSTANCE = new CachedCalculator();

    private CachedCalculator() {
    }

    private final ThreadLocal<Evaluator> evaluator = new ThreadLocal<Evaluator>() {
        @Override
        protected Evaluator initialValue() {
            return CachedCalculator.super.buildEvaluator();
        }
    };

    @Override
    protected Evaluator buildEvaluator() {
        return evaluator.get();
    }
}

