package task2;

import java.io.*;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyDoubleSerializer implements MyFirstSerializerInterface <Double>  {

    @Override
    public void serializeToStream(OutputStream outputStream, Double o) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeDouble(o);
    }

    @Override
    public Double deserializeFromStream(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        return dataInputStream.readDouble();
    }

}
