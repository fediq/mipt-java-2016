package ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays;

/**
 * Class for Joining Byte Arrays
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 * @see "https://www.mkyong.com/java/java-how-to-join-arrays/"
 */
public class JoinArraysByte implements IJoinArrays<Byte> {
    /**
     * Method to join arraysToJoin into a single continuous joinedArrays
     *
     * @param  arraysToJoin - arrays to join into a single one
     * @return a single Array, containing all the arraysToJoin in order
     */
    public final Byte[] JoinArrays(Byte[]... arraysToJoin) {
        final Byte[] joinedArrays;

        if (arraysToJoin == null) {
            joinedArrays = null;
        } else {
            int joinedArraysLength = 0;
            for (Byte[] arrayCurrent : arraysToJoin) {
                joinedArraysLength += arrayCurrent.length;
            }

            joinedArrays = new Byte[joinedArraysLength];

            int arrayCopyOffset = 0;
            for (Byte[] arrayCurrent : arraysToJoin) {
                System.arraycopy(arrayCurrent, 0,
                        joinedArrays, arrayCopyOffset,
                        arrayCurrent.length);

                arrayCopyOffset += arrayCurrent.length;
            }
        }

        return joinedArrays;
    }
}