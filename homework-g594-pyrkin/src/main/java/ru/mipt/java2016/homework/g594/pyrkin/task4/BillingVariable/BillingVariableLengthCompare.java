package ru.mipt.java2016.homework.g594.pyrkin.task4.BillingVariable;

import java.util.Comparator;

/**
 * Created by randan on 12/17/16.
 */
public class BillingVariableLengthCompare implements Comparator<BillingVariable> {
    @Override
    public int compare(BillingVariable billingVariable, BillingVariable t1) {
        if (billingVariable.getName().length() > t1.getName().length()) {
            return -1;
        } else if (billingVariable.getName().length() < billingVariable.getName().length()) {
            return 1;
        } else {
            return 0;
        }
    }
}
