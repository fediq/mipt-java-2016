package ru.mipt.java2016.homework.g596.egorov.task3;

import ru.mipt.java2016.homework.g596.egorov.task3.serializers.AdvancedSerializerInterface;

/**
 * Created by евгений on 30.10.2016.
 */
public class AdvancedSerializerofDouble implements AdvancedSerializerInterface<Double> {

    @Override
    public String serialize(Double obj) {
        return obj.toString();
    }

    @Override
    public Double deserialize(String s) {
        return Double.parseDouble(s);
    }
}
