package ru.itmo.java;

public class HashTable {

    private static final double LOAD_FACTOR = 0.5;
    private static final int CAPACITY_MULTIPLIER = 2;
    private static final int NOT_FOUND = -1;

    private Entry[] pairs;
    private int Capacity;
    private int Size;
    private final double LoadFactor;
    public HashTable(int initialCapacity, double loadFactor) {
        Capacity = initialCapacity;
        pairs = new Entry[Capacity];
        LoadFactor = loadFactor;
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, LOAD_FACTOR);
    }

    private int findNewPos(Object key) {
        int hash = index(key, pairs.length);
        for (int i = hash; i < pairs.length; i++) {
            if (pairs[i] == null || pairs[i].isTombstone()) {
                return i;
            }
        }
        for (int i = 0; i < hash; i++) {
            if (pairs[i] == null || pairs[i].isTombstone()) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    private int findInd(Object key) {
        int hash = index(key, pairs.length);
        for (int i = hash; i < pairs.length; i++) {
            if (pairs[i] == null) {
                return NOT_FOUND;
            }

            if (!pairs[i].isTombstone() && pairs[i].key.equals(key)) {
                return i;
            }
        }
        for (int i = 0; i < hash; i++) {
            if (pairs[i] == null) {
                return NOT_FOUND;
            }

            if (!pairs[i].isTombstone() && pairs[i].key.equals(key)) {
                return i;
            }
        }

        return NOT_FOUND;
    }

    private void updateCapacity() {
        int threshold = (int) (Capacity * LoadFactor);
        if (Size >= threshold) {
            Capacity = Capacity * CAPACITY_MULTIPLIER;
            Entry[] oldPairs = pairs;
            pairs = new Entry[Capacity];
            int prevSize = Size;

            for (Entry oldPair : oldPairs) {
                if (oldPair != null && !oldPair.isTombstone()) {
                    pairs[findNewPos(oldPair.getKey())] = new Entry(oldPair.getKey(), oldPair.getValue());
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
        if (pairs[pos] != null) {
            prevValue = pairs[pos].getValue();
        }

        pairs[pos] = new Entry(key, value);
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

        Entry pair = pairs[pos];

        return pair.getValue();
    }

    Object remove(Object key) {
        int pos = findInd(key);
        if (pos == NOT_FOUND) {
            return null;
        }

        Object result = pairs[pos].getValue();
        pairs[pos] = Entry.createTombstone();
        Size--;
        return result;
    }

    int size() {
        return Size;
    }

    private int index(Object object, int length) {
        return Math.abs(object.hashCode() % length);
    }

    private static class Entry {

        private Object key;
        private Object value;
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
