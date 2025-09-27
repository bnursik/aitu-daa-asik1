package com.asik1.metrics;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks recursion depth safely. Use try-with-resources:
 *
 * try (DepthTracker.Scope s = depth.enter()) {
 *     // recursive body
 * }
 */
public final class DepthTracker {
    private final ThreadLocal<Integer> current = ThreadLocal.withInitial(() -> 0);
    private final AtomicInteger maxDepth = new AtomicInteger(0);

    public Scope enter() {
        int d = current.get() + 1;
        current.set(d);
        // CAS-like update of max
        maxDepth.getAndAccumulate(d, Math::max);
        return new Scope(this);
    }

    private void exit() {
        int d = current.get() - 1;
        if (d < 0) d = 0; // guard
        current.set(d);
    }

    public int getCurrentDepth() { return current.get(); }
    public int getMaxDepth() { return maxDepth.get(); }

    /** Reset between independent runs. */
    public void reset() {
        current.set(0);
        maxDepth.set(0);
    }

    /** AutoCloseable scope for try-with-resources. */
    public static final class Scope implements AutoCloseable {
        private final DepthTracker owner;
        private boolean closed = false;
        private Scope(DepthTracker owner) { this.owner = owner; }
        @Override public void close() {
            if (!closed) { owner.exit(); closed = true; }
        }
    }
}
