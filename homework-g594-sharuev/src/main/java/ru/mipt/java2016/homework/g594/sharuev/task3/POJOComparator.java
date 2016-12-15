package ru.mipt.java2016.homework.g594.sharuev.task3;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class POJOComparator<Value> implements Comparator<Value> {

    private Class clazz;

    public POJOComparator(Class clazzVal) {
        clazz = clazzVal;
    }

    public Class getComparingClass() {
        return clazz;
    }

    @Override
    public int compare(Value v1, Value v2) {
        try {
            try {
                // Если сам объект уже имеет компаратор
                Method m = clazz.getMethod("compareTo", Object.class);
                m.setAccessible(true);
                return (int) m.invoke(v1, v2);
            } catch (NoSuchMethodException e) {
                // Иначе сравниваем его поля
                ArrayList<Field> fields = new ArrayList<>();
                Class toSerialize = clazz;
                do {
                    Collections.addAll(fields, toSerialize.getDeclaredFields());
                    toSerialize = toSerialize.getSuperclass();
                } while (toSerialize != null);

                for (Field field : fields) {
                    field.setAccessible(true);
                    int result = compareAtom(field.getType(), field.get(v1), field.get(v2));
                    if (result != 0) {
                        return result;
                    }

                }
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Tryin' to compare shit");
        }
        return 0;
    }

    private int compareAtom(Class o, Object v1,
                            Object v2) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (o == int.class) {
            return (int) v1 > (int) v2 ? 1 : ((int) v1 < (int) v2 ? -1 : 0);
        } else if (o == double.class) {
            return (double) v1 > (double) v2 ? 1 : ((double) v1 < (double) v2 ? -1 : 0);
        } else if (o == boolean.class) {
            return (boolean) v1 && !((boolean) v2) ? 1 : (!((boolean) v1) && ((boolean) v2) ? -1 : 0);
        } else {
            Method compareTo = o.getMethod("compareTo", Object.class);
            return (int) compareTo.invoke(v1, v2);
        }
    }
}
