package ru.mipt.java2016.homework.g594.sharuev.task3;
/*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.mipt.java2016.homework.g594.sharuev.task2.SerializationException;
import ru.mipt.java2016.homework.g594.sharuev.task2.SerializationStrategy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

public class JsonSerializationStrategy<Value> implements SerializationStrategy<Value> {

    public JsonSerializationStrategy() {
        type = new TypeToken<Value>() {
        }.getType();
        gson = new Gson();
    }

    @Override
    public byte[] serializeToBytes(Value value) throws SerializationException {
        return gson.toJson(value).getBytes();
    }

    @Override
    public void serializeToStream(Value value,
                                  OutputStream outputStream) throws SerializationException {
        try {
            outputStream.write(gson.toJson(value).getBytes());
        } catch (IOException e) {
            throw new SerializationException("OutputStream write error");
        }
    }

    @Override
    public Value deserializeFromStream(InputStream inputStream) throws SerializationException {
        return null;
    }

    @Override
    public Value deserialize(byte[] bytes, int offset) throws SerializationException {
        return gson.fromJson(new String(bytes), type);
    }

    private Gson gson;
    private Type type;
}
*/