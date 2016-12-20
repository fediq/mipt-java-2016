package ru.mipt.java2016.homework.g596.litvinov.task4;

import java.util.Comparator;

/**
 * Created by
 *
 * @author Stanislav A. Litvinov
 * @since 19.12.16.
 */
public class BillingVariableLengthCompare implements Comparator<BillingVariable> {
    @Override
    public int compare(BillingVariable v1, BillingVariable v2) {
        if (v1.getName().length() > v2.getName().length()) {
            return -1;
        } else if (v1.getName().length() < v2.getName().length()) {
            return 1;
        } else {
            return 0;
        }
    }
}
