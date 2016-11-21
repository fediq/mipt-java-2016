package ru.mipt.java2016.homework.g594.gorelick.task2;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Alexander Gorelick on 10/31/16.
 */
interface Serializer<V> {
     ByteBuffer serialize(V object) throws IOException;
     V deserialize(ByteBuffer b_array) throws IOException;
}