package ru.mipt.java2016.homework.g595.rodin.task2.Serializer;



import java.util.StringTokenizer;

/**
 * Created by Dmitry on 27.10.16.
 */
public class CJSONCreator<KeyType, ValueType> {

    private ISerialize<KeyType> keySerializer;

    private  ISerialize<ValueType> valueSerializer;

    public CJSONCreator(ISerialize<KeyType> keySerializer,
                        ISerialize<ValueType> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    public String getJSON(KeyType key, ValueType value) throws IllegalArgumentException {
        StringBuilder stringBuilder = new StringBuilder("{");
        return stringBuilder.append(keySerializer.serialize(key)).append("{")
                .append(valueSerializer.serialize(value)).append("}};").toString();
    }

    public KeyType getKeyFormJSON(String expression) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(expression, "{}", false);
        String token = tokenizer.nextToken();
        return keySerializer.deserialize(token);
    }

    public ValueType getValueFromJSON(String expression) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(expression, "{}", false);
        String token = tokenizer.nextToken();
        token = tokenizer.nextToken();
        return valueSerializer.deserialize(token);
    }

}
