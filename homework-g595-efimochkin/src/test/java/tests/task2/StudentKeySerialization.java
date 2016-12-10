package tests.task2;

//import ru.mipt.java2016.homework.tests.task2.Student;

import ru.mipt.java2016.homework.g595.efimochkin.task3.BaseSerialization;
import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by sergejefimockin on 28.11.16.
 */
public class StudentKeySerialization implements BaseSerialization<StudentKey> {

    private static StudentKeySerialization instance = new StudentKeySerialization();

    public static StudentKeySerialization getInstance() {return instance;}

    private StudentKeySerialization() { }

    @Override
    public Long write(RandomAccessFile file, StudentKey object) throws IOException {
        try {
            Long offset = file.getFilePointer();
            file.writeInt(object.getGroupId());
            file.writeUTF(object.getName());
            return offset;
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

    @Override
    public StudentKey read(RandomAccessFile file) throws IOException {
        try {
            return new StudentKey(file.readInt(), file.readUTF());
        } catch (IOException e) {
            throw new IOException("Could not write to file.");
        }
    }

}
