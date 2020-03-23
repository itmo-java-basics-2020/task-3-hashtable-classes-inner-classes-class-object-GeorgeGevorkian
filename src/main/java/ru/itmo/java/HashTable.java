package ru.itmo.java;

public class HashTable {

    private static final double LOAD_FACTOR = 0.5;
    private static final int CAPACITY_MULTIPLIER = 2;
    private static final int NOT_FOUND = -1;

    private Entry[] pairs;
    private int capacity;
    private int size;
    private final double LoadFactor;
    public HashTable(int initialCapacity, double loadFactor) {
        capacity = initialCapacity;
        pairs = new Entry[capacity];
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
        int threshold = (int) (capacity * LoadFactor);
        if (size >= threshold) {
            capacity = capacity * CAPACITY_MULTIPLIER;
            Entry[] oldPairs = pairs;
            pairs = new Entry[capacity];
            int prevSize = size;

            for (Entry oldPair : oldPairs) {
                if (oldPair != null && !oldPair.isTombstone()) {
                    pairs[findNewPos(oldPair.getKey())] = new Entry(oldPair.getKey(), oldPair.getValue());
                }
            }

            size = prevSize;
        }
    }

    public Object put(Object key, Object value) {
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
            size++;
        }
        updateCapacity();

        return prevValue;
    }

     public Object get(Object key) {
        int pos = findInd(key);
        if (pos == NOT_FOUND) {
            return null;
        }

        Entry pair = pairs[pos];

        return pair.getValue();
    }

    public Object remove(Object key) {
        int pos = findInd(key);
        if (pos == NOT_FOUND) {
            return null;
        }

        Object result = pairs[pos].getValue();
        pairs[pos] = Entry.createTombstone();
        size--;
        return result;
    }

    public int size() {
        return size;
    }

    private int index(Object object, int length) {
        return Math.abs(object.hashCode() % length);
    }

    private static class Entry {

        private final Object key;
        private final Object value;
        private final boolean isTombstone;

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
            this.isTombstone = false;
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
