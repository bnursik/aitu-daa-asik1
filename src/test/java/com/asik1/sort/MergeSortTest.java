package com.asik1.sort;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    @Test
    void sortsRandomCorrectly() {
        int n = 2000;
        int[] a = randomArray(n, 123);
        int[] b = a.clone();

        Counters c = new Counters();
        DepthTracker d = new DepthTracker();

        MergeSort.sort(a, c, d);

        Arrays.sort(b);
        assertArrayEquals(b, a, "MergeSort must match Arrays.sort");
        assertTrue(d.getMaxDepth() <= 2 * floorLog2(n) + 4, "Depth should be O(log n)");
        assertTrue(c.getComparisons() > 0);
        assertTrue(c.getMoves() > 0);
        assertTrue(c.getArrayAllocs() == 1, "Exactly one buffer allocation expected");
    }

    @Test
    void handlesEdgeCases() {
        // empty
        int[] e = {};
        Counters c1 = new Counters(); DepthTracker d1 = new DepthTracker();
        MergeSort.sort(e, c1, d1);
        assertArrayEquals(new int[]{}, e);

        // single
        int[] s = {7};
        MergeSort.sort(s, new Counters(), new DepthTracker());
        assertArrayEquals(new int[]{7}, s);

        // all equal
        int[] eq = new int[1000];
        Arrays.fill(eq, 5);
        MergeSort.sort(eq, new Counters(), new DepthTracker());
        assertTrue(allEqual(eq));

        // reverse
        int[] rev = new int[2048];
        for (int i = 0; i < rev.length; i++) rev[i] = rev.length - i;
        int[] revSorted = rev.clone(); Arrays.sort(revSorted);
        MergeSort.sort(rev, new Counters(), new DepthTracker());
        assertArrayEquals(revSorted, rev);

        // few unique
        int[] fu = new int[5000];
        Random r = new Random(7);
        for (int i = 0; i < fu.length; i++) fu[i] = r.nextInt(5);
        int[] fuSorted = fu.clone(); Arrays.sort(fuSorted);
        MergeSort.sort(fu, new Counters(), new DepthTracker());
        assertArrayEquals(fuSorted, fu);
    }

    @RepeatedTest(3)
    void depthRoughlyLogN() {
        int n = 1 << 15; // 32768
        int[] a = randomArray(n, System.nanoTime());
        Counters c = new Counters();
        DepthTracker d = new DepthTracker();
        MergeSort.sort(a, c, d, 32);

        int log2 = floorLog2(n);
        int maxDepth = d.getMaxDepth();

        // MergeSort depth should be ~ log2(n) + O(1..2) depending on cutoff and exact framing
        assertTrue(maxDepth <= log2 + 10, "Max recursion depth should be close to log2(n)");
    }

    /* ------------ helpers ------------ */

    private static int[] randomArray(int n, long seed) {
        Random rnd = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = rnd.nextInt();
        return a;
    }

    private static int floorLog2(int n) {
        return 31 - Integer.numberOfLeadingZeros(n);
        // n >=1 assumed for callers that need it
    }

    private static boolean allEqual(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i] != a[0]) return false;
        return true;
    }
}
