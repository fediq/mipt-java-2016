package ru.mipt.java2016.seminars.seminar6;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionExample {
    private static void newInstance() {
        Class<Sample> clazz = null;
        try {
            clazz = (Class<Sample>) Class.forName("ru.mipt.java2016.seminars.seminar6.Sample");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Sample newObj = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static void getInfo(Object obj) {
        Class objClass = obj.getClass();
        System.out.println("getName(): " + objClass.getName());
        System.out.println("getSimpleName(): " + objClass.getSimpleName());

        Class superClass = objClass.getSuperclass();
        System.out.println("superClass.getSimpleName(): " + superClass.getSimpleName());

        Constructor[] constructors = objClass.getDeclaredConstructors();
        System.out.println("Constructors:");
        for (Constructor c : constructors) {
            System.out.println(c.getName());
        }
        Method[] methods = objClass.getDeclaredMethods();
        System.out.println("Methods:");
        for (Method m : methods) {
            System.out.println(m.getName());
        }
        Field[] fields = objClass.getDeclaredFields();
        System.out.println("Fields:");
        for (Field f : fields) {
            System.out.println(f.getName());
        }
    }

    private static void invoke(Object obj) {
        Method method = null;
        try {
            method = obj.getClass().getDeclaredMethod("doStuff", int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            method.setAccessible(true);
            method.invoke(obj, 5);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        invoke(new Sample());
    }
}
