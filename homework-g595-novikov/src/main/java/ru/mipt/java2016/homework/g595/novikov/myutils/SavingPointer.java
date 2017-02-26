package ru.mipt.java2016.homework.g595.novikov.myutils;

import java.util.Iterator;

/**
 * Created by igor on 11/28/16.
 */
public interface SavingPointer<E> {
    boolean hasCurrent();

    E getCurrent();

    SavingPointer<E> next();

    class IteratorImplementation<E> implements Iterator<E> {
        private SavingPointer<E> pointer;
        private E nextObject;

        IteratorImplementation(SavingPointer<E> myPointer) {
            pointer = myPointer;
            nextObject = pointer.getCurrent();
        }

        @Override
        public boolean hasNext() {
            return nextObject != null;
        }

        public E next() {
            pointer.next();
            E old = nextObject;
            nextObject = pointer.getCurrent();
            return old;
        }
    }

    default Iterator<E> toIterator() {
        return new IteratorImplementation<E>(this);
    }

    class SavingPointerImplementation<E> implements SavingPointer<E> {
        private Iterator<E> iter;
        private E current;

        public SavingPointerImplementation(Iterator<E> myIter) {
            iter = myIter;
            next();
        }

        @Override
        public boolean hasCurrent() {
            return current != null;
        }

        @Override
        public E getCurrent() {
            return current;
        }

        @Override
        public SavingPointerImplementation<E> next() {
            if (iter.hasNext()) {
                current = iter.next();
            } else {
                current = null;
            }
            return this;
        }
    }

    static <E> SavingPointer<E> fromIterator(Iterator<E> iter) {
        return new SavingPointerImplementation<E>(iter);
    }
}
