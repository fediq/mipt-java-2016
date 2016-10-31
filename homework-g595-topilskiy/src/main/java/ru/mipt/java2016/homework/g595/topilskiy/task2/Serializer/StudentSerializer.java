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
public class StudentSerializer implements ISerializer<Student> {
    /* ClassSerializers for serializing and deserializing within the class */
    private static final BooleanSerializer BOOLEAN_SERIALIZER = (BooleanSerializer)
            SerializerFactory.getSerializer("Boolean");
    private static final IntegerSerializer INTEGER_SERIALIZER = (IntegerSerializer)
            SerializerFactory.getSerializer("Integer");
    private static final DoubleSerializer  DOUBLE_SERIALIZER  = (DoubleSerializer)
            SerializerFactory.getSerializer("Double");
    private static final DateSerializer    DATE_SERIALIZER    = (DateSerializer)
            SerializerFactory.getSerializer("Date");
    private static final StringSerializer  STRING_SERIALIZER  = (StringSerializer)
            SerializerFactory.getSerializer("String");

    /**
     * Serialize a Student object into Bytes
     *
     * @param  value - object to be serialized
     * @return Array of Bytes, into which value has been serialized into
     */
    @Override
    public byte[] serialize(Student value) {
        JoinArraysPrimitiveByte joinArraysPrimitiveByte = new JoinArraysPrimitiveByte();

        byte[] studentGroupIDBytes       = INTEGER_SERIALIZER.serialize(value.getGroupId());
        byte[] studentNameBytes          =  STRING_SERIALIZER.serialize(value.getName());
        byte[] studentHometownBytes      =  STRING_SERIALIZER.serialize(value.getHometown());
        byte[] studentBirthdateBytes     =    DATE_SERIALIZER.serialize(value.getBirthDate());
        byte[] studentHasDormitoryBytes  = BOOLEAN_SERIALIZER.serialize(value.isHasDormitory());
        byte[] studentAverageScoreBytes  =  DOUBLE_SERIALIZER.serialize(value.getAverageScore());
        return joinArraysPrimitiveByte.joinArrays(studentGroupIDBytes,
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
        if (valueBytes.length < IntegerSerializer.getIntegerByteSize()) {
            throw new IllegalArgumentException("Illegal Deserialization");
        }

        int valueBytesHead = 0;
        int valueBytesTail = IntegerSerializer.getIntegerByteSize();


        /* READ GroupID */
        int studentGroupID = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Name */
        valueBytesTail += IntegerSerializer.getIntegerByteSize();
        int nameLen = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += nameLen;
        String studentName = STRING_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Hometown */
        valueBytesTail += IntegerSerializer.getIntegerByteSize();
        int hometownLen = INTEGER_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += hometownLen;
        String studentHometown = STRING_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Birthday */
        valueBytesTail += DateSerializer.getDateByteSize();
        Date studentBirthday = DATE_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ HasDormitory */
        valueBytesTail += BooleanSerializer.getBooleanByteSize();
        Boolean studentHasDormitory = BOOLEAN_SERIALIZER.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Average Score */
        valueBytesTail += DoubleSerializer.getDoubleByteSize();
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
