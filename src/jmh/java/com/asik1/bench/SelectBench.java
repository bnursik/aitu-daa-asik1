package com.asik1.bench;

import com.asik1.metrics.Counters;
import com.asik1.metrics.DepthTracker;
import com.asik1.select.DeterministicSelect;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Compare DeterministicSelect (MoM5) vs Arrays.sort()+index for various n/k/shapes.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 7, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2)
@State(Scope.Thread)
public class SelectBench {

    @Param({"1000", "10000", "50000"})
    public int n;

    @Param({"RANDOM", "FEWUNIQ", "ALLEQUAL"})
    public Shape shape;

    @Param({"0", "25", "50", "75", "99"})
    public int percentile; // k as percentile

    private int[] data;
    private int k;

    @Setup(Level.Trial)
    public void setup() {
        data = new int[n];
        Random rnd = new Random(12345);
        switch (shape) {
            case RANDOM:
                for (int i = 0; i < n; i++) data[i] = rnd.nextInt();
                break;
            case FEWUNIQ:
                for (int i = 0; i < n; i++) data[i] = rnd.nextInt(5);
                break;
            case ALLEQUAL:
                Arrays.fill(data, 7);
                break;
        }
        k = Math.min(n - 1, Math.max(0, (int)Math.round(percentile / 100.0 * (n - 1))));
    }

    @Benchmark
    public int deterministicSelect(Blackhole bh) {
        int[] a = data.clone();
        Counters c = new Counters();
        DepthTracker d = new DepthTracker();
        int v = DeterministicSelect.select(a, k, c, d);
        bh.consume(c.getComparisons());
        bh.consume(d.getMaxDepth());
        return v;
    }

    @Benchmark
    public int arraysSortThenPick(Blackhole bh) {
        int[] a = data.clone();
        Arrays.sort(a);
        int v = a[k];
        bh.consume(v);
        return v;
    }

    public enum Shape { RANDOM, FEWUNIQ, ALLEQUAL }
}
