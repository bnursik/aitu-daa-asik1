package com.asik1.select;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;

import static com.asik1.common.ArraysEx.swap;
import static com.asik1.common.Compare.*;

/**
 * Deterministic Select (Median-of-Medians, groups of 5).
 * - In-place compaction of medians to the front of the slice.
 * - 3-way partition around chosen pivot VALUE to handle duplicates robustly.
 * - Recurse only into the needed side (and prefer the smaller side) for bounded stack.
 *
 * API returns the k-th smallest value in a[] (0-based k).
 */
public final class DeterministicSelect {

    private static final int CUTOFF = 24; // small slice solved by insertion sort

    private DeterministicSelect() {}

    /** Returns the k-th smallest value in a (0-based). */
    public static int select(int[] a, int k, Counters ctr, DepthTracker depth) {
        if (a == null) throw new IllegalArgumentException("array is null");
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of range");
        return selectRec(a, 0, a.length - 1, k, ctr, depth);
    }

    private static int selectRec(int[] a, int lo, int hi, int k, Counters ctr, DepthTracker depth) {
        while (true) {
            int n = hi - lo + 1;
            if (n <= CUTOFF) {
                insertion(a, lo, hi, ctr);
                return a[k];
            }

            int pivotVal = pivotByMoM5(a, lo, hi, ctr, depth); // VALUE, not index

            // 3-way partition around pivotVal: [lo..lt-1] < pv, [lt..gt] == pv, [gt+1..hi] > pv
            int[] bounds = partition3(a, lo, hi, pivotVal, ctr);
            int lt = bounds[0], gt = bounds[1];

            if (k < lt) {
                // go left (smaller side preference)
                if (depth != null) try (DepthTracker.Scope ignored = depth.enter()) {
                    hi = lt - 1; // loop on the larger side, recurse (enter depth) on the smaller
                    // continue while-loop to shrink range without growing stack more than needed
                }
            } else if (k <= gt) {
                return pivotVal; // k falls into == pivot region
            } else {
                if (depth != null) try (DepthTracker.Scope ignored = depth.enter()) {
                    lo = gt + 1;
                }
            }
        }
    }

    /** Choose pivot VALUE via Median-of-Medians with groups of 5 and in-place compaction of medians. */
    private static int pivotByMoM5(int[] a, int lo, int hi, Counters ctr, DepthTracker depth) {
        int n = hi - lo + 1;
        int groups = (n + 4) / 5;

        // Move each group's median into a contiguous block at the start: a[lo + i]
        for (int g = 0; g < groups; g++) {
            int s = lo + g * 5;
            int e = Math.min(s + 4, hi);
            insertion(a, s, e, ctr); // sort small group
            int m = s + (e - s) / 2; // median index within the group
            swap(a, lo + g, m, ctr);
        }

        int medIndex = lo + groups / 2;
        // Recursively select the median of the 'groups' medians.
        return selectRec(a, lo, lo + groups - 1, medIndex, ctr, depth);
    }

    /** Dutch National Flag 3-way partition around pivot VALUE. Returns [lt, gt]. */
    private static int[] partition3(int[] a, int lo, int hi, int pivot, Counters ctr) {
        int lt = lo, i = lo, gt = hi;
        while (i <= gt) {
            if (less(a[i], pivot, ctr)) {
                swap(a, lt++, i++, ctr);
            } else if (greater(a[i], pivot, ctr)) {
                swap(a, i, gt--, ctr);
            } else {
                i++;
            }
        }
        return new int[]{lt, gt};
    }

    /** Insertion sort on a slice; counts comparisons/moves in ctr. */
    private static void insertion(int[] a, int lo, int hi, Counters ctr) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            if (ctr != null) ctr.incMoves();
            int j = i - 1;
            while (j >= lo && greater(a[j], key, ctr)) {
                a[j + 1] = a[j];
                if (ctr != null) ctr.incMoves();
                j--;
            }
            a[j + 1] = key;
            if (ctr != null) ctr.incMoves();
        }
    }
}
