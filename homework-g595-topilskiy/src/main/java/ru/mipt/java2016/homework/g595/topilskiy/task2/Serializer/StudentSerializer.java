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
    /* An ClassSerializers for serializing and deserializing within the class */
    private static final BooleanSerializer booleanSerializer = (BooleanSerializer)
            SerializerFactory.getSerializer("Boolean");
    private static final IntegerSerializer integerSerializer = (IntegerSerializer)
            SerializerFactory.getSerializer("Integer");
    private static final DoubleSerializer  doubleSerializer  = (DoubleSerializer)
            SerializerFactory.getSerializer("Double");
    private static final DateSerializer    dateSerializer    = (DateSerializer)
            SerializerFactory.getSerializer("Date");
    private static final StringSerializer  stringSerializer  = (StringSerializer)
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

        byte[] StudentGroupIDBytes       = integerSerializer.serialize(value.getGroupId());
        byte[] StudentNameBytes          =  stringSerializer.serialize(value.getName());
        byte[] StudentHometownBytes      =  stringSerializer.serialize(value.getHometown());
        byte[] StudentBirthdateBytes     =    dateSerializer.serialize(value.getBirthDate());
        byte[] StudentHasDormitoryBytes  = booleanSerializer.serialize(value.isHasDormitory());
        byte[] StudentAverageScoreBytes  =  doubleSerializer.serialize(value.getAverageScore());
        return joinArraysPrimitiveByte.joinArrays(StudentGroupIDBytes,
                                                  StudentNameBytes,
                                                  StudentHometownBytes,
                                                  StudentBirthdateBytes,
                                                  StudentHasDormitoryBytes,
                                                  StudentAverageScoreBytes);
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
        int StudentGroupID = integerSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Name */
        valueBytesTail += IntegerSerializer.getIntegerByteSize();
        int nameLen = integerSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += nameLen;
        String StudentName = stringSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Hometown */
        valueBytesTail += IntegerSerializer.getIntegerByteSize();
        int hometownLen = integerSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesTail += hometownLen;
        String StudentHometown = stringSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Birthday */
        valueBytesTail += DateSerializer.getDateByteSize();
        Date StudentBirthday = dateSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ HasDormitory */
        valueBytesTail += BooleanSerializer.getBooleanByteSize();
        Boolean StudentHasDormitory = booleanSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );
        valueBytesHead = valueBytesTail;


        /* READ Average Score */
        valueBytesTail += DoubleSerializer.getDoubleByteSize();
        Double StudentAverageScore = doubleSerializer.deserialize(
                copyOfRange(valueBytes, valueBytesHead, valueBytesTail)
        );


        /* Complete Student */
        return new Student(StudentGroupID,
                           StudentName,
                           StudentHometown,
                           StudentBirthday,
                           StudentHasDormitory,
                           StudentAverageScore);
    }
}
