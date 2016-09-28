package ru.mipt.java2016.homework.g000.lavrentyev.task1;

import net.sourceforge.jeval.Evaluator;
import ru.mipt.java2016.homework.base.task1.Calculator;

/**
 * Версия {@link JEvalCalculator}, которая не создает новый  {@link Evaluator} каждый раз,
 * а переиспользует один и тот же экземпляр.
 *
 * @author Fedor S. Lavrentyev
 * @since 28.09.16
 */
public class CachedJEvalCalculator extends JEvalCalculator {
    public static final Calculator INSTANCE = new CachedJEvalCalculator();

    private final ThreadLocal<Evaluator> evaluator = new ThreadLocal<Evaluator>() {
        @Override
        protected Evaluator initialValue() {
            return CachedJEvalCalculator.super.buildEvaluator();
        }
    };

    @Override
    protected Evaluator buildEvaluator() {
        return evaluator.get();
    }
}
