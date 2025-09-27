package com.asik1.common;

import com.asik1.metrics.Counters;

public final class Compare {
    private Compare() {}

    public static boolean less(int x, int y, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return x < y;
    }
    public static boolean greater(int x, int y, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return x > y;
    }
    public static boolean lessEq(int x, int y, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return x <= y;
    }
    public static boolean greaterEq(int x, int y, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return x >= y;
    }
}
