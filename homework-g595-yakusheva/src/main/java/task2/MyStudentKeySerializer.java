package task2;

import java.io.*;
import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

/**
 * Created by Софья on 27.10.2016.
 */
public class MyStudentKeySerializer implements MyFirstSerializerInterface<StudentKey> {
    @Override
    public void serializeToStream(OutputStream outputStream, StudentKey o) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeUTF(o);
    }

    @Override
    public StudentKey deserializeFromStream(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        return dataInputStream.readUTF();
    }
}
