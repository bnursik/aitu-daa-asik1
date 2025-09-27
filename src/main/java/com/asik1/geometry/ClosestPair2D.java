package com.asik1.geometry;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Divide-and-conquer closest pair of points in 2D.
 * Returns minimal squared Euclidean distance as a double.
 */
public final class ClosestPair2D {

    private ClosestPair2D() {}

    /** Compute squared distance between closest pair among n points. */
    public static double closestPair(int[] xs, int[] ys, Counters ctr, DepthTracker depth) {
        if (xs == null || ys == null || xs.length != ys.length)
            throw new IllegalArgumentException("xs, ys null or length mismatch");
        int n = xs.length;
        if (n < 2) return Double.MAX_VALUE;

        Point[] pts = new Point[n];
        for (int i = 0; i < n; i++) pts[i] = new Point(xs[i], ys[i]);
        Arrays.sort(pts, Comparator.comparingInt(p -> p.x));

        Point[] aux = new Point[n]; // temp buffer for merges
        if (ctr != null) ctr.incArrayAllocs();

        return rec(pts, aux, 0, n - 1, ctr, depth);
    }

    private static double rec(Point[] pts, Point[] aux, int lo, int hi, Counters ctr, DepthTracker depth) {
        try (DepthTracker.Scope ignored = depth != null ? depth.enter() : null) {
            int n = hi - lo + 1;
            if (n <= 3) {
                return brute(pts, lo, hi, ctr);
            }

            int mid = lo + (hi - lo) / 2;
            int midX = pts[mid].x;

            double dl = rec(pts, aux, lo, mid, ctr, depth);
            double dr = rec(pts, aux, mid + 1, hi, ctr, depth);
            double d = Math.min(dl, dr);

            // Merge by y into aux
            mergeByY(pts, aux, lo, mid, hi, ctr);

            // Build strip
            int m = 0;
            for (int i = lo; i <= hi; i++) {
                if (Math.abs(pts[i].x - midX) < d) {
                    aux[m++] = pts[i];
                }
            }

            // Check at most 7 neighbors
            for (int i = 0; i < m; i++) {
                for (int j = i + 1; j < m && (aux[j].y - aux[i].y) < d; j++) {
                    d = Math.min(d, dist2(aux[i], aux[j], ctr));
                }
            }
            return d;
        }
    }

    private static void mergeByY(Point[] pts, Point[] aux, int lo, int mid, int hi, Counters ctr) {
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi) {
            if (lessEqY(pts[i], pts[j], ctr)) {
                aux[k++] = pts[i++];
            } else {
                aux[k++] = pts[j++];
            }
        }
        while (i <= mid) aux[k++] = pts[i++];
        while (j <= hi) aux[k++] = pts[j++];

        System.arraycopy(aux, 0, pts, lo, hi - lo + 1);
        if (ctr != null) ctr.addMoves(hi - lo + 1);
    }

    private static double brute(Point[] pts, int lo, int hi, Counters ctr) {
        double d = Double.MAX_VALUE;
        for (int i = lo; i <= hi; i++) {
            for (int j = i + 1; j <= hi; j++) {
                d = Math.min(d, dist2(pts[i], pts[j], ctr));
            }
        }
        Arrays.sort(pts, lo, hi + 1, Comparator.comparingInt(p -> p.y));
        return d;
    }

    private static double dist2(Point a, Point b, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        double dx = (double)a.x - b.x;
        double dy = (double)a.y - b.y;
        return dx * dx + dy * dy;
    }

    private static boolean lessEqY(Point a, Point b, Counters ctr) {
        if (ctr != null) ctr.incComparisons();
        return a.y <= b.y;
    }

    private static final class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }
}
