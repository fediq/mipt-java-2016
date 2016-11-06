package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dima on 05.11.16.
 */
class Offset {
    public static SerializationStrategy<Offset> STRATEGY = new SerializationStrategy<Offset>() {
        @Override
        public void serializeToStream(Offset offset, DataOutputStream output) throws IOException {

        }

        @Override
        public Offset deserializeFromStream(DataInputStream input) throws IOException {
            return null;
        }
    };

    public int fileIndex;
    public long fileOffset;

    public Offset(int fileIndex, long fileOffset) {
        this.fileIndex = fileIndex;
        this.fileOffset = fileOffset;
    }
}
