package com.asik1.select;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeterministicSelectTest {

    @Test
    void matchesArraysSortForManyKs() {
        int n = 3001;
        int[] base = randomArray(n, 123);
        int[] sorted = base.clone();
        Arrays.sort(sorted);

        // probe a bunch of ranks, including edges and middle
        int[] ks = {0, 1, n/4, n/2 - 1, n/2, n - 2, n - 1};
        for (int k : ks) {
            int[] a = base.clone();
            int v = DeterministicSelect.select(a, k, new Counters(), new DepthTracker());
            assertEquals(sorted[k], v, "k=" + k + " must match Arrays.sort");
        }
    }

    @Test
    void handlesDuplicatesAndTinyArrays() {
        // all equal
        int[] eq = new int[1000];
        Arrays.fill(eq, 7);
        for (int k = 0; k < eq.length; k++) {
            int v = DeterministicSelect.select(eq.clone(), k, new Counters(), new DepthTracker());
            assertEquals(7, v);
        }

        // tiny
        int[] t1 = {42};
        assertEquals(42, DeterministicSelect.select(t1.clone(), 0, new Counters(), new DepthTracker()));
        int[] t3 = {3,1,2};
        assertEquals(1, DeterministicSelect.select(t3.clone(), 0, new Counters(), new DepthTracker()));
        assertEquals(2, DeterministicSelect.select(t3.clone(), 1, new Counters(), new DepthTracker()));
        assertEquals(3, DeterministicSelect.select(t3.clone(), 2, new Counters(), new DepthTracker()));
    }

    @RepeatedTest(3)
    void randomTrials100() {
        Random rnd = new Random(77);
        for (int trial = 0; trial < 100; trial++) {
            int n = 300 + rnd.nextInt(700); // 300..999
            int[] a = randomArray(n, rnd.nextLong());
            int[] b = a.clone();
            Arrays.sort(b);

            int k = rnd.nextInt(n);
            int v = DeterministicSelect.select(a, k, new Counters(), new DepthTracker());
            assertEquals(b[k], v);
        }
    }

    private static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt();
        return a;
    }
}
