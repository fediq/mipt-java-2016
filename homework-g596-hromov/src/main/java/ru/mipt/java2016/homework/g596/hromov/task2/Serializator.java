package ru.mipt.java2016.homework.g596.hromov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by igorhromov on 30.10.16.
 */
interface Serializator<type> {

    void serializeToStream(type value, DataOutputStream outStream);

    type deserializeFromStream(DataInputStream inputStream);

}
