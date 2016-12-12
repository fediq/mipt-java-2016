package ru.mipt.java2016.homework.g595.topilskiy.task2.JoinArrays;

/**
 * Interface for Joining Generic Arrays, consisting of ArrayComponentType
 *
 * @author Artem K. Topilskiy
 * @since 30.10.16
 * @see "https://www.mkyong.com/java/java-how-to-join-arrays/"
 */
public interface IJoinArrays<ArrayComponentType> {
    /**
     * Method to join arraysToJoin into a single continuous Array
     *
     * @param  arraysToJoin - arrays to join into a single one
     * @return a single Array, containing all the arraysToJoin in order
     */
    ArrayComponentType[] joinArrays(ArrayComponentType[]... arraysToJoin);
}
