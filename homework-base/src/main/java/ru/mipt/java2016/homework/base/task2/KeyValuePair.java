package ru.mipt.java2016.homework.base.task2;

/**
 * @author Fedor S. Lavrentyev
 * @since 04.10.16
 */
public class KeyValuePair<A, B> {
    private final A key;
    private final B value;

    public KeyValuePair(A key, B value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        this.key = key;
        this.value = value;
    }

    public A getKey() {
        return key;
    }

    public B getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeyValuePair<?, ?> keyValuePair = (KeyValuePair<?, ?>) o;

        return key.equals(keyValuePair.key) && value.equals(keyValuePair.value);

    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
