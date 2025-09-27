package com.asik1.common;

import com.asik1.metrics.Counters;
import static com.asik1.common.Compare.less;
import static com.asik1.common.Compare.greater;
import static com.asik1.common.ArraysEx.swap;

/**
 * Partition helpers for QuickSort/Select.
 */
public final class Partition {
    private Partition() {}

    /** Hoare partition by pivot VALUE; returns j with a[lo..j] <= pivot <= a[j+1..hi]. */
    public static int hoareByValue(int[] a, int lo, int hi, int pivot, Counters ctr) {
        int i = lo - 1, j = hi + 1;
        while (true) {
            do { i++; } while (less(a[i], pivot, ctr));
            do { j--; } while (greater(a[j], pivot, ctr));
            if (i >= j) return j;
            swap(a, i, j, ctr);
        }
    }
}
