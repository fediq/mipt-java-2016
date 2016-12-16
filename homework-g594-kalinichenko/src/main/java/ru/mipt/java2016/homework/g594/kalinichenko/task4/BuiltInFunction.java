package ru.mipt.java2016.homework.g594.kalinichenko.task4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Function;

import static java.lang.Math.*;

import static ru.mipt.java2016.homework.g594.kalinichenko.task4.BuiltInFunction.Func0.RND;
import static ru.mipt.java2016.homework.g594.kalinichenko.task4.BuiltInFunction.Func1.*;
import static ru.mipt.java2016.homework.g594.kalinichenko.task4.BuiltInFunction.Func2.*;

/**
 * Created by masya on 16.12.16.
 */
public class BuiltInFunction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuiltInFunction.class);

    public static final BuiltInFunction INSTANCE = new BuiltInFunction();

    protected enum Func1
    {
        SIN {
            @Override
            public double calc(double a) {
                return sin(a);
            }
        },
        COS {
            @Override
            public double calc(double a) {
                return cos(a);
            }
        },
        TG {
            @Override
            public double calc(double a) {
                return tan(a);
            }
        },
        SQRT {
            @Override
            public double calc(double a) {
                return sqrt(a);
            }
        },
        ABS {
            @Override
            public double calc(double a) {
                return abs(a);
            }
        },
        SIGN {
            @Override
            public double calc(double a) {
                return signum(a);
            }
        },
        LOG2 {
            @Override
            public double calc(double a) {
                return log(a)/log(2);
            }
        };
        public abstract double calc(double a);
    }
    protected enum Func0
    {
        RND {
            @Override
            public double calc() {
                return random();
            }
        };
        public abstract double calc();
    }

    protected enum Func2
    {
        POW {
            @Override
            public double calc(double a, double b) {
                return pow(a, b);
            }
        },
        LOG {
            @Override
            public double calc(double a, double n) {
                return log(a)/log(n);
            }
        },
        MAX {
            @Override
            public double calc(double a, double b) {
                return max(a, b);
            }
        },
        MIN {
            @Override
            public double calc(double a, double b) {
                return min(a, b);
            }
        };
        public abstract double calc(double a, double b);
    }
    
    private static HashMap<String, Func0> map0 = new HashMap<>();
    static {
        map0.put("rnd", RND);
    }

    private static HashMap<String, Func1> map1 = new HashMap<>();
    static {
        map1.put("sin", SIN);
        map1.put("cos", COS);
        map1.put("tg", TG);
        map1.put("sqrt", SQRT);
        map1.put("abs", ABS);
        map1.put("sign", SIGN);
        map1.put("log2", LOG2);
    }

    private static HashMap<String, Func2> map2 = new HashMap<>();
    static {
        map2.put("pow", POW);
        map2.put("log", LOG);
        map2.put("max", MAX);
        map2.put("min", MIN);
    }

    public static boolean find(String name, int args) {
        if (args == 0) {
            return map0.containsKey(name);
        } else if (args == 1) {
            return map1.containsKey(name);
        } else if (args == 2) {
            return map2.containsKey(name);
        }
        return false;
    }
    public static boolean find(String name)
    {
        return map0.containsKey(name) || map1.containsKey(name) || map2.containsKey(name);
    }

    public static double execute(String name, ArrayList<Double> args)
    {
        LOGGER.trace("Execute builtin function " + name);
        LOGGER.trace("Args size: " + args.size());
        if (args.size() == 0)
        {
            return map0.get(name).calc();
        }
        else if (args.size() == 1)
        {
            return map1.get(name).calc(args.get(0));
        }
        else if (args.size() == 2)
        {
            return map2.get(name).calc(args.get(0), args.get(1));
        }
        throw new IllegalStateException("Wrong query to function");
    }
}
