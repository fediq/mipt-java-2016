package ru.mipt.java2016.homework.g596.egorov.task3;

import ru.mipt.java2016.homework.g596.egorov.task3.serializers.AdvancedSerializerInterface;

/**
 * Created by евгений on 30.10.2016.
 */
public class AdvancedSerializerofInteger implements AdvancedSerializerInterface<Integer> {
    @Override
    public String serialize(Integer obj) {
        return obj.toString();
    }

    @Override
    public Integer deserialize(String s) {
        return Integer.parseInt(s);
    }
}
