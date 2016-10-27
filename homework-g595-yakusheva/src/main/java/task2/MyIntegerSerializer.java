package task2;

import java.io.*;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyIntegerSerializer implements MyFirstSerializerInterface <Integer>{
    @Override
    public void serializeToStream(OutputStream outputStream, Integer o) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeDouble(o);
    }

    @Override
    public Integer deserializeFromStream(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        return dataInputStream.readInt();
    }
}
