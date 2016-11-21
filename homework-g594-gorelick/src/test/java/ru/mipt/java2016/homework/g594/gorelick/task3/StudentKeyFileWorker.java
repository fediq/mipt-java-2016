package ru.mipt.java2016.homework.g594.gorelick.task3;

import ru.mipt.java2016.homework.tests.task2.StudentKey;
import java.io.IOException;
import java.io.RandomAccessFile;

class StudentKeyFileWorker implements FileWorker<StudentKey> {
    @Override
    public StudentKey read(RandomAccessFile file, long position) throws IOException {
        IntegerFileWorker integerFileWorker = new IntegerFileWorker();
        StringFileWorker stringSerializer = new StringFileWorker();
        file.seek(position);
        int groupId = integerFileWorker.read(file, file.getFilePointer());
        String name = stringSerializer.read(file, file.getFilePointer());
        return new StudentKey(groupId, name);
    }
    @Override
    public void write(RandomAccessFile file, StudentKey object, long position) throws IOException {
        IntegerFileWorker integerFileWorker = new IntegerFileWorker();
        StringFileWorker stringSerializer = new StringFileWorker();
        file.seek(position);
        integerFileWorker.write(file, object.getGroupId(), file.getFilePointer());
        stringSerializer.write(file, object.getName(), file.getFilePointer());
    }
}