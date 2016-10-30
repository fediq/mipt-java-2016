package ru.mipt.java2016.homework.g596.egorov.task2.serializers;

import java.io.DataInputStream;     //В Каждом "сериализаторе" пришлось
import java.io.DataOutputStream;    //Подключать эти модули
import java.io.IOException;         //Как этого избежать?!

/**
 * Created by евгений on 30.10.2016.
 */
public interface SerializerInterface<T> {
    T deserialize(DataInputStream rd) throws IOException;

    void serialize(DataOutputStream wr, T obj) throws IOException;
}

