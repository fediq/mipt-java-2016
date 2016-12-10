package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import ru.mipt.java2016.homework.tests.task2.StudentKey;
import ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays.JoinArraysPrimitiveByte;

import static java.util.Arrays.copyOfRange;

/**
 * Class for a StudentKey Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class StudentKeySerializerSingleton implements ISerializer<StudentKey> {
    /* The single allowed instance of a singleton class */
    private static StudentKeySerializerSingleton instance;

    /* FORBID: direct instantiation of a singleton class */
    private StudentKeySerializerSingleton() {}

    /**
     * Return (and create if needed) the only instance of this singleton
     *
     * @return a valid instance of the singleton
     */
    public static StudentKeySerializerSingleton getInstance() {
        if (instance == null) {
            instance = new StudentKeySerializerSingleton();
        }

        return instance;
    }


    /* ClassSerializers for serializing and deserializing within the class */
    private static final IntegerSerializerSingleton INTEGER_SERIALIZER =
                         IntegerSerializerSingleton.getInstance();
    private static final StringSerializerSingleton STRING_SERIALIZER =
                         StringSerializerSingleton.getInstance();

    /**
     * Serialize a StudentKey object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(StudentKey value) {
        byte[] studentKeyGroupIDBytes = INTEGER_SERIALIZER.serialize(value.getGroupId());
        byte[] studentKeyNameBytes = STRING_SERIALIZER.serialize(value.getName());
        return JoinArraysPrimitiveByte.joinArrays(studentKeyGroupIDBytes, studentKeyNameBytes);
    }

    /**
     * Deserialize a String object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of StudentKey
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to StudentKey
     */
    @Override
    public StudentKey deserialize(byte[] valueBytes) throws IllegalArgumentException {
        int valueBytesHead = 0;
        int valueBytesTail = IntegerSerializerSingleton.getIntegerByteSize();

        int studentKeyGroupID = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;
        valueBytesTail += IntegerSerializerSingleton.getIntegerByteSize();

        int nameLen = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += nameLen;

        String studentKeyName = STRING_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );

        return new StudentKey(studentKeyGroupID, studentKeyName);
    }
}
