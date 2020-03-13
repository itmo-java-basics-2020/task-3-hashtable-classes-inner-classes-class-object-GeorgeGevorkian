package ru.itmo.java;

import java.util.Map;

public class HashTable {

    private static final double LOAD_FACTOR = 0.5;
    private static final double CAPACITY_MULTIPLIER = 2.0;
    private static final int NOT_FOUND = -1;

    private Entry[] Pairs;
    private int Capacity;
    private int Size;
    private double LoadFactor;

    HashTable(int initialCapacity, double loadFactor) {
        Capacity = initialCapacity;
        Pairs = new Entry[Capacity];
        LoadFactor = loadFactor;
    }

    HashTable(int initialCapacity) {
        Capacity = initialCapacity;
        Pairs = new Entry[Capacity];
        LoadFactor = LOAD_FACTOR;
    }

    private int findNewPos(Object key) {
        int hash = index(key, Pairs.length);

        for (int i = hash; i < Pairs.length; i++) {
            if (Pairs[i] == null || Pairs[i].isTombstone()) {
                return i;
            }
        }

        for (int i = 0; i < hash; i++) {
            if (Pairs[i] == null || Pairs[i].isTombstone()) {
                return i;
            }
        }

        return NOT_FOUND;
    }

    private int findInd(Object key) {
        int hash = index(key, Pairs.length);

        for (int i = hash; i < Pairs.length; i++) {
            if (Pairs[i] == null) {
                return NOT_FOUND;
            }

            if (!Pairs[i].isTombstone() && Pairs[i].key.equals(key)) {
                return i;
            }
        }

        for (int i = 0; i < hash; i++) {
            if (Pairs[i] == null) {
                return NOT_FOUND;
            }

            if (!Pairs[i].isTombstone() && Pairs[i].key.equals(key)) {
                return i;
            }
        }

        return NOT_FOUND;
    }

    private void updateCapacity() {
        if (Size >= threshold()) {
            Capacity = (int) (Capacity * CAPACITY_MULTIPLIER);
            Entry[] oldPairs = Pairs;
            Pairs = new Entry[Capacity];
            int prevSize = Size;

            for (Entry oldPair : oldPairs) {
                if (oldPair != null && !oldPair.isTombstone()) {
                    Pairs[findNewPos(oldPair.getKey())] = new Entry(oldPair.getKey(), oldPair.getValue());
                }
            }

            Size = prevSize;
        }
    }

    Object put(Object key, Object value) {
        int pos = findInd(key);
        if (pos == NOT_FOUND) {
            pos = findNewPos(key);
        }

        Object prevValue = null;
        if (Pairs[pos] != null) {
            prevValue = Pairs[pos].getValue();
        }

        Pairs[pos] = new Entry(key, value);
        if (prevValue == null) {
            Size++;
        }
        updateCapacity();

        return prevValue;
    }

    Object get(Object key) {
        int pos = findInd(key);
        if (pos == NOT_FOUND) {
            return null;
        }

        Entry pair = Pairs[findInd(key)];

        return pair.getValue();
    }

    Object remove(Object key) {
        int pos = findInd(key);
        if (pos == NOT_FOUND) {
            return null;
        }

        Object result = Pairs[pos].getValue();
        Pairs[pos] = Entry.createTombstone();
        Size--;
        return result;
    }

    int size() {
        return Size;
    }

    private int threshold() {
        return (int) (Capacity * LoadFactor);
    }


    private int index(Object object, int length) {
        return Math.abs(object.hashCode() % length);
    }

    private static class Entry {

        private final Object key;
        private final Object value;
        private boolean isTombstone;

        public static Entry createTombstone() {
            return new Entry();
        }

        private Entry() {
            key = null;
            value = null;
            isTombstone = true;
        }

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }

        public boolean isTombstone() {
            return isTombstone;
        }
    }


}
