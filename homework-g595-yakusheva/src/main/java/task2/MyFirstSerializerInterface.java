package task2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Софья on 26.10.2016.
 */
public interface MyFirstSerializerInterface <Value> {

    void serializeToStream(OutputStream outputStream, Value value) throws IOException;

    Value deserializeFromStream(InputStream inputStream) throws IOException;
}
