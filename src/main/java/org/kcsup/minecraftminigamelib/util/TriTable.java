package org.kcsup.minecraftminigamelib.util;

import java.util.HashMap;

public class TriTable<A, B, C> {
    HashMap<A, HashMap<B, C>> table;

    public TriTable() {
        table = new HashMap<>();
    }

    public TriTable(A[] a, HashMap<B, C> hashMap) {
        table = new HashMap<>();
        for(A aEntry : a) {
            table.put(aEntry, hashMap);
        }
    }

    public C get(A a, B b) {
        return table.get(a).get(b);
    }

    public void putEntry(A a, B b, C c) {
        table.get(a).put(b, c);
    }

    public void putEmptyRow(A a) {
        table.put(a, new HashMap<>());
    }
}
