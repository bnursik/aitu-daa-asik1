package com.asik1.common;

import com.asik1.metrics.Counters;

import java.util.concurrent.ThreadLocalRandom;

import static com.asik1.common.ArraysEx.swap;

/** Fisher–Yates shuffle for int[]. */
public final class Shuffle {
    private Shuffle() {}

    public static void shuffle(int[] a, int lo, int hi, Counters ctr) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        for (int i = hi; i > lo; i--) {
            int j = rnd.nextInt(lo, i + 1);
            swap(a, i, j, ctr);
        }
    }
}
