package ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays;

import java.lang.reflect.Array;

/**
 * Class for Joining Generic Arrays, consisting of ArrayComponentType
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 * @see "https://www.mkyong.com/java/java-how-to-join-arrays/"
 */
public class JoinArraysGeneric<ArrayComponentType> implements IJoinArrays<ArrayComponentType> {
    /**
     * Method to join arraysToJoin into a single continuous joinedArrays
     *
     * @param  arraysToJoin - arrays to join into a single one
     * @return a single Array, containing all the arraysToJoin in order
     */
    @Override
    @SafeVarargs
    public final ArrayComponentType[] joinArrays(ArrayComponentType[]... arraysToJoin) {
        final ArrayComponentType[] joinedArrays;

        if (arraysToJoin == null) {
            joinedArrays = null;
        } else {
            int joinedArraysLength = 0;
            for (ArrayComponentType[] arrayCurrent : arraysToJoin) {
                joinedArraysLength += arrayCurrent.length;
            }

            /*
             * Creating space for Joined Array
             * NOTE: The conversion to (ArrayComponentType[]) of Array.newInstance(...)
             * is completely valid, because the new Array
             * contains exactly arguments of ArrayComponentType
             */
            Class<?> arrayComponentTypeClass = arraysToJoin[0].getClass().getComponentType();
            joinedArrays = (ArrayComponentType[])
                            Array.newInstance(arrayComponentTypeClass, joinedArraysLength);

            int arrayCopyOffset = 0;
            for (ArrayComponentType[] arrayCurrent : arraysToJoin) {
                System.arraycopy(arrayCurrent, 0,
                                 joinedArrays, arrayCopyOffset,
                                 arrayCurrent.length);

                arrayCopyOffset += arrayCurrent.length;
            }
        }

        return joinedArrays;
    }
}