package ru.mipt.java2016.homework.g596.egorov.task3;

import ru.mipt.java2016.homework.g596.egorov.task3.serializers.AdvancedSerializerInterface;


/**
 * Created by евгений on 30.10.2016.
 */
public class AdvancedSerializerofString implements AdvancedSerializerInterface<String> {
    @Override
    public String serialize(String obj) {
        return obj;
    }

    @Override
    public String deserialize(String s) {
        return s;
    }
}
