package ru.mipt.java2016.homework.g595.topilskiy.task2.Serializer;

import ru.mipt.java2016.homework.tests.task2.Student;
import ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays.JoinArraysPrimitiveByte;

import java.util.Date;

import static java.util.Arrays.copyOfRange;


/**
 * Class for a Student Serializer
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 */
public class StudentSerializerSingleton implements ISerializer<Student> {
    /* The single allowed instance of a singleton class */
    private static StudentSerializerSingleton instance;

    /* FORBID: direct instantiation of a singleton class */
    private StudentSerializerSingleton() {}

    /**
     * Return (and create if needed) the only instance of this singleton
     *
     * @return a valid instance of the singleton
     */
    public static StudentSerializerSingleton getInstance() {
        if (instance == null) {
            instance = new StudentSerializerSingleton();
        }

        return instance;
    }


    /* ClassSerializers for serializing and deserializing within the class */
    private static final BooleanSerializerSingleton BOOLEAN_SERIALIZER =
                         BooleanSerializerSingleton.getInstance();
    private static final IntegerSerializerSingleton INTEGER_SERIALIZER =
                         IntegerSerializerSingleton.getInstance();
    private static final DoubleSerializerSingleton DOUBLE_SERIALIZER  =
                         DoubleSerializerSingleton.getInstance();
    private static final DateSerializerSingleton DATE_SERIALIZER    =
                         DateSerializerSingleton.getInstance();
    private static final StringSerializerSingleton STRING_SERIALIZER  =
                         StringSerializerSingleton.getInstance();

    /**
     * Serialize a Student object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Student value) {
        byte[] studentGroupIDBytes       = INTEGER_SERIALIZER.serialize(value.getGroupId());
        byte[] studentNameBytes          =  STRING_SERIALIZER.serialize(value.getName());
        byte[] studentHometownBytes      =  STRING_SERIALIZER.serialize(value.getHometown());
        byte[] studentBirthdateBytes     =    DATE_SERIALIZER.serialize(value.getBirthDate());
        byte[] studentHasDormitoryBytes  = BOOLEAN_SERIALIZER.serialize(value.isHasDormitory());
        byte[] studentAverageScoreBytes  =  DOUBLE_SERIALIZER.serialize(value.getAverageScore());
        return JoinArraysPrimitiveByte.joinArrays(studentGroupIDBytes,
                                                  studentNameBytes,
                                                  studentHometownBytes,
                                                  studentBirthdateBytes,
                                                  studentHasDormitoryBytes,
                                                  studentAverageScoreBytes);
    }

    /**
     * Deserialize a String object from an Array of Bytes
     *
     * @param  valueBytes - Array of Bytes that contains an object of Student
     * @return Deserialized value from valueBytes
     * @throws IllegalArgumentException if valueBytes cannot be converted to Student
     */
    @Override
    public Student deserialize(byte[] valueBytes) throws IllegalArgumentException {
        if (valueBytes.length < IntegerSerializerSingleton.getIntegerByteSize()) {
            throw new IllegalArgumentException("Illegal Deserialization");
        }

        int valueBytesHead = 0;
        int valueBytesTail = IntegerSerializerSingleton.getIntegerByteSize();


        /* READ GroupID */
        int studentGroupID = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Name */
        valueBytesTail += IntegerSerializerSingleton.getIntegerByteSize();
        int nameLen = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += nameLen;
        String studentName = STRING_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Hometown */
        valueBytesTail += IntegerSerializerSingleton.getIntegerByteSize();
        int hometownLen = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += hometownLen;
        String studentHometown = STRING_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Birthday */
        valueBytesTail += DateSerializerSingleton.getDateByteSize();
        Date studentBirthday = DATE_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ HasDormitory */
        valueBytesTail += BooleanSerializerSingleton.getBooleanByteSize();
        Boolean studentHasDormitory = BOOLEAN_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Average Score */
        valueBytesTail += DoubleSerializerSingleton.getDoubleByteSize();
        Double studentAverageScore = DOUBLE_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );


        /* Complete Student */
        return new Student(studentGroupID,
                           studentName,
                           studentHometown,
                           studentBirthday,
                           studentHasDormitory,
                           studentAverageScore);
    }
}
