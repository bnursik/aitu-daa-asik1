package com.asik1.sort;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;

import java.util.concurrent.ThreadLocalRandom;

/**
 * QuickSort (int[]) with:
 *  - randomized pivot
 *  - smaller-first recursion, iterate over the larger side (bounded stack ~ O(log n) expected)
 *  - Hoare partition (fewer swaps than Lomuto)
 *  - optional small-n cutoff to insertion sort
 *
 * Metrics:
 *  - comparisons: every value comparison
 *  - moves: every element assignment (swap counts as 3 moves via temp)
 *  - depth: tracked only on recursive descent into the smaller side
 */
public final class QuickSort {

    private static final int CUTOFF = 24; // insertion sort threshold

    private QuickSort() {}

    public static void sort(int[] a, Counters ctr, DepthTracker depth) {
        if (a == null || a.length <= 1) return;
        qs(a, 0, a.length - 1, ctr, depth);
    }

    private static void qs(int[] a, int lo, int hi, Counters ctr, DepthTracker depth) {
        // Tail-recursive loop: recurse only into the smaller side; iterate the other.
        while (lo < hi) {
            int n = hi - lo + 1;
            if (n <= CUTOFF) {
                insertion(a, lo, hi, ctr);
                return;
            }

            // randomized pivot value (do not move it for Hoare)
            int pIdx = ThreadLocalRandom.current().nextInt(lo, hi + 1);
            int pivot = a[pIdx];

            int mid = hoarePartition(a, lo, hi, pivot, ctr);

            // Left partition is [lo..mid], right is [mid+1..hi]
            int leftSize = mid - lo + 1;
            int rightSize = hi - (mid + 1) + 1;

            if (leftSize < rightSize) {
                // Recurse into the smaller (left), then loop over right
                if (depth != null) try (DepthTracker.Scope ignored = depth.enter()) {
                    qs(a, lo, mid, ctr, depth);
                }
                lo = mid + 1;
            } else {
                // Recurse into the smaller (right), then loop over left
                if (depth != null) try (DepthTracker.Scope ignored = depth.enter()) {
                    qs(a, mid + 1, hi, ctr, depth);
                }
                hi = mid;
            }
        }
    }

    /** Hoare partition using a pivot VALUE. Returns final index j so that a[lo..j] <= pivot <= a[j+1..hi]. */
    private static int hoarePartition(int[] a, int lo, int hi, int pivot, Counters ctr) {
        int i = lo - 1;
        int j = hi + 1;
        while (true) {
            do { i++; } while (less(a[i], pivot, ctr));
            do { j--; } while (less(pivot, a[j], ctr));
            if (i >= j) return j;
            swap(a, i, j, ctr);
        }
    }

    private static void insertion(int[] a, int lo, int hi, Counters ctr) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            if (ctr != null) ctr.incMoves(); // write to temp register
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

    /* --- helpers with counting --- */
    private static boolean less(int x, int y, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return x < y;
    }
    private static boolean greater(int x, int y, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return x > y;
    }
    private static void swap(int[] a, int i, int j, Counters ctr) {
        if (i == j) return;
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
        if (ctr != null) ctr.addMoves(3); // three assignments
    }
}
