package com.asik1.common;

import com.asik1.metrics.Counters;

public final class ArraysEx {
    private ArraysEx() {}

    /** Swap a[i] and a[j]; counts 3 moves if i!=j. */
    public static void swap(int[] a, int i, int j, Counters ctr) {
        if (i == j) return;
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        if (ctr != null) ctr.addMoves(3);
    }
}
