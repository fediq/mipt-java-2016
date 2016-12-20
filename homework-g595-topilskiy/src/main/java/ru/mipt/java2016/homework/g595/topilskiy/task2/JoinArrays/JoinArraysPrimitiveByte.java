package ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays;

/**
 * Class for Joining PrimitiveByte (byte) Arrays
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 * @see "https://www.mkyong.com/java/java-how-to-join-arrays/"
 */
public class JoinArraysPrimitiveByte /* implements IJoinArrays<byte> */ {
    /* FORBID: direct instantiation of this class */
    private JoinArraysPrimitiveByte() { }

    /**
     * Method to join arraysToJoin into a single continuous joinedArrays
     *
     * @param  arraysToJoin - arrays to join into a single one
     * @return a single Array, containing all the arraysToJoin in order
     */
    public static byte[] joinArrays(byte[]... arraysToJoin) {
        final byte[] joinedArrays;

        if (arraysToJoin == null) {
            joinedArrays = null;
        } else {
            int joinedArraysLength = 0;
            for (byte[] arrayCurrent : arraysToJoin) {
                joinedArraysLength += arrayCurrent.length;
            }

            joinedArrays = new byte[joinedArraysLength];

            int arrayCopyOffset = 0;
            for (byte[] arrayCurrent : arraysToJoin) {
                System.arraycopy(arrayCurrent, 0,
                        joinedArrays, arrayCopyOffset,
                        arrayCurrent.length);

                arrayCopyOffset += arrayCurrent.length;
            }
        }

        return joinedArrays;
    }
}