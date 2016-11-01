package ru.mipt.java2016.homework.g597.povarnitsyn.task2;
import java.io.*;

/**
 * Created by Ivan on 30.10.2016.
 */
public interface SerializationInterface<T> {
    T deserialize(BufferedReader input) throws IOException;
    void serialize(PrintWriter output, T object) throws IOException;
}
