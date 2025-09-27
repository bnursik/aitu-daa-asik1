package com.asik1.metrics;

import java.util.concurrent.atomic.LongAdder;


public final class Counters {
    private final LongAdder comparisons = new LongAdder();
    private final LongAdder moves = new LongAdder();
    private final LongAdder arrayAllocs = new LongAdder();

    public void incComparisons() { comparisons.increment(); }
    public void addComparisons(long k) { comparisons.add(k); }
    public long getComparisons() { return comparisons.sum(); }

    public void incMoves() { moves.increment(); }
    public void addMoves(long k) { moves.add(k); }
    public long getMoves() { return moves.sum(); }

    public void incArrayAllocs() { arrayAllocs.increment(); }
    public void addArrayAllocs(long k) { arrayAllocs.add(k); }
    public long getArrayAllocs() { return arrayAllocs.sum(); }

    public void reset() {
        comparisons.reset(); moves.reset(); arrayAllocs.reset();
    }
}
