package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import ru.mipt.java2016.homework.tests.task2.StudentKey;

import java.nio.ByteBuffer;

/**
 * StudentKeySerializer
 * Created by randan on 10/30/16.
 */
public class StudentKeySerializer implements SerializerInterface<StudentKey> {

    @Override
    public int sizeOfSerialize(StudentKey object) {
        return 4 + 2 * (object.getName().length() + 1);
    }

    @Override
    public ByteBuffer serialize(StudentKey object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(this.sizeOfSerialize(object));
        resultBuffer.putInt(object.getGroupId());
        for(char symbol : object.getName().toCharArray()){
            resultBuffer.putChar(symbol);
        }
        resultBuffer.putChar('\0');
        return resultBuffer;
    }

    @Override
    public StudentKey deserialize(ByteBuffer inputBuffer) {
        int groupId;
        StringBuilder name = new StringBuilder();
        groupId = inputBuffer.getInt();
        char symbol;
        while ((symbol = inputBuffer.getChar()) != '\0'){
            name.append(symbol);
        }
        return new StudentKey(groupId, name.toString());
    }
}
