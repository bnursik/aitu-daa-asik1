package com.asik1.geometry;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ClosestPair2DTest {

    @Test
    void tinyCases() {
        int[] xs = {0, 3};
        int[] ys = {0, 4};
        double d2 = ClosestPair2D.closestPair(xs, ys, new Counters(), new DepthTracker());
        assertEquals(25.0, d2, 1e-9);
    }

    @Test
    void randomVsBrute() {
        Random rnd = new Random(7);
        for (int n = 2; n <= 200; n += 37) {
            int[] xs = new int[n];
            int[] ys = new int[n];
            for (int i = 0; i < n; i++) {
                xs[i] = rnd.nextInt(1000);
                ys[i] = rnd.nextInt(1000);
            }
            double fast = ClosestPair2D.closestPair(xs, ys, new Counters(), new DepthTracker());
            double brute = brute(xs, ys);
            assertEquals(brute, fast, 1e-9, "n=" + n);
        }
    }

    @Test
    void largeCloudRuns() {
        int n = 20000;
        int[] xs = new int[n];
        int[] ys = new int[n];
        Random rnd = new Random(123);
        for (int i = 0; i < n; i++) {
            xs[i] = rnd.nextInt();
            ys[i] = rnd.nextInt();
        }
        double d2 = ClosestPair2D.closestPair(xs, ys, new Counters(), new DepthTracker());
        assertTrue(d2 >= 0.0, "distance must be non-negative");
    }

    private static double brute(int[] xs, int[] ys) {
        double best = Double.MAX_VALUE;
        for (int i = 0; i < xs.length; i++) {
            for (int j = i + 1; j < ys.length; j++) {
                double dx = (double)xs[i] - xs[j];
                double dy = (double)ys[i] - ys[j];
                double d2 = dx*dx + dy*dy;
                if (d2 < best) best = d2;
            }
        }
        return best;
    }
}
