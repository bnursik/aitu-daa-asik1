package com.asik1.sort;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    @Test
    void randomMatchesJdkSort() {
        int n = 20000;
        int[] a = randomArray(n, 123);
        int[] b = a.clone();
        Arrays.sort(b);

        Counters c = new Counters();
        DepthTracker d = new DepthTracker();

        QuickSort.sort(a, c, d);

        assertArrayEquals(b, a, "QuickSort must match Arrays.sort");
        // depth should be ~ O(log n) on randomized pivot + smaller-first
        assertTrue(d.getMaxDepth() <= 2 * floorLog2(n) + 6, "Depth should be bounded near 2*log2(n)");
        assertTrue(c.getComparisons() > 0);
        assertTrue(c.getMoves() > 0);
    }

    @Test
    void adversarialShapes() {
        // sorted increasing
        int[] inc = range(0, 5000);
        check(inc);

        // reverse
        int[] dec = range(0, 5000);
        for (int i = 0; i < dec.length; i++) dec[i] = dec.length - i;
        check(dec);

        // many duplicates
        int[] dup = new int[15000];
        Random r = new Random(7);
        for (int i = 0; i < dup.length; i++) dup[i] = r.nextInt(5);
        check(dup);
    }

    @RepeatedTest(3)
    void depthBoundHoldsStatistically() {
        int n = 1 << 16; // 65,536
        int[] a = randomArray(n, System.nanoTime());
        Counters c = new Counters();
        DepthTracker d = new DepthTracker();

        QuickSort.sort(a, c, d);

        int bound = 2 * floorLog2(n) + 8;
        assertTrue(d.getMaxDepth() <= bound, "Expected depth <= " + bound + " but was " + d.getMaxDepth());
    }

    /* ---------- helpers ---------- */
    private static void check(int[] a) {
        int[] b = a.clone();
        Arrays.sort(b);
        Counters c = new Counters();
        DepthTracker d = new DepthTracker();
        QuickSort.sort(a, c, d);
        assertArrayEquals(b, a);
    }

    private static int[] randomArray(int n, long seed) {
        Random rnd = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt();
        return a;
    }

    private static int[] range(int from, int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = from + i;
        return a;
    }

    private static int floorLog2(int n) { return 31 - Integer.numberOfLeadingZeros(n); }
}
