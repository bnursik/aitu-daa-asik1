package com.asik1.sort;

import com.asik1.common.Compare;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;

/**
 * Top-down MergeSort for int[] with:
 * - linear merge
 * - single reusable buffer (allocated once in public entrypoint)
 * - small-n cutoff to insertion sort
 *
 * Metrics:
 * - comparisons: every value comparison
 * - moves: every element assignment (including copies to/from buffer)
 * - arrayAllocs: counts the single temp buffer allocation
 * - depth: each recursive frame calls depth.enter()
 */
public final class MergeSort {

    private static final int DEFAULT_CUTOFF = 24; // tune later if needed

    private MergeSort() {}

    /** Convenience entry with default cutoff. */
    public static void sort(int[] a, Counters ctr, DepthTracker depth) {
        sort(a, ctr, depth, DEFAULT_CUTOFF);
    }

    /** Public entry allowing custom cutoff. */
    public static void sort(int[] a, Counters ctr, DepthTracker depth, int cutoff) {
        if (a == null || a.length <= 1) return;
        if (cutoff < 0) cutoff = 0;
        int[] tmp = new int[a.length];
        if (ctr != null) ctr.incArrayAllocs();
        sortRec(a, tmp, 0, a.length - 1, cutoff, ctr, depth);
    }

    private static void sortRec(int[] a, int[] tmp, int lo, int hi, int cutoff, Counters ctr, DepthTracker depth) {
        try (DepthTracker.Scope ignored = depth != null ? depth.enter() : null) {
            int n = hi - lo + 1;
            if (n <= 1) return;

            if (n <= cutoff) {
                insertionSort(a, lo, hi, ctr);
                return;
            }

            int mid = lo + ((hi - lo) >>> 1);
            sortRec(a, tmp, lo, mid, cutoff, ctr, depth);
            sortRec(a, tmp, mid + 1, hi, cutoff, ctr, depth);

            // If already ordered, skip merge (optimization)
            if (Compare.lessEq(a[mid], a[mid + 1], ctr)) {
                return;
            }
            merge(a, tmp, lo, mid, hi, ctr);
        } catch (Exception e) {
            // try-with-resources may pass null scope when depth is null; ignore
        }
    }

    /** Classic linear merge using single shared tmp. */
    private static void merge(int[] a, int[] tmp, int lo, int mid, int hi, Counters ctr) {
        // copy a[lo..hi] -> tmp[lo..hi]
        for (int i = lo; i <= hi; i++) {
            tmp[i] = a[i];
            if (ctr != null) ctr.incMoves();
        }

        int i = lo;      // left ptr
        int j = mid + 1; // right ptr
        int k = lo;      // write ptr into a

        while (i <= mid && j <= hi) {
            if (Compare.lessEq(tmp[i], tmp[j], ctr)) {
                a[k++] = tmp[i++];
                if (ctr != null) ctr.incMoves();
            } else {
                a[k++] = tmp[j++];
                if (ctr != null) ctr.incMoves();
            }
        }
        while (i <= mid) {
            a[k++] = tmp[i++];
            if (ctr != null) ctr.incMoves();
        }
        // right tail already in place
    }

    /** Insertion sort for small slices, counts comparisons/moves. */
    private static void insertionSort(int[] a, int lo, int hi, Counters ctr) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = a[i];
            if (ctr != null) ctr.incMoves(); // read key into register counted as "move" (write back later)
            int j = i - 1;
            while (j >= lo && Compare.greater(a[j], key, ctr)) {
                a[j + 1] = a[j];
                if (ctr != null) ctr.incMoves();
                j--;
            }
            a[j + 1] = key;
            if (ctr != null) ctr.incMoves();
        }
    }

    }

