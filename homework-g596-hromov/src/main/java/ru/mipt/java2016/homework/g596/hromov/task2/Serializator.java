package ru.mipt.java2016.homework.g596.hromov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by igorhromov on 30.10.16.
 */
interface Serializator<Class> {

    void serializeToStream(Class value, DataOutputStream outStream)  throws IOException;
    Class deserializeFromStream(DataInputStream inputStream) throws IOException;

}
