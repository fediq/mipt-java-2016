package ru.mipt.java2016.homework.g595.murzin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by dima on 05.11.16.
 */
public class Offset {
    public static final SerializationStrategy<Offset> STRATEGY = new SerializationStrategy<Offset>() {
        @Override
        public void serializeToStream(Offset offset, DataOutputStream output) throws IOException {
            output.writeInt(offset.fileIndex);
            output.writeLong(offset.fileOffset);
        }

        @Override
        public Offset deserializeFromStream(DataInputStream input) throws IOException {
            return new Offset(input.readInt(), input.readLong());
        }
    };

    public static final Offset NONE = new Offset(-1, -1);

    public final int fileIndex;
    public final long fileOffset;

    public Offset(int fileIndex, long fileOffset) {
        this.fileIndex = fileIndex;
        this.fileOffset = fileOffset;
    }
}
