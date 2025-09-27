package com.asik1.metrics;

/**
 * Immutable record for one experiment row + a builder to fill fields.
 * This aligns with the CSV schema we agreed on.
 */
public final class Metrics {
    public final String algo;
    public final int n;
    public final String shape;
    public final long seed;
    public final int trial;
    public final long timeNs;
    public final int maxDepth;
    public final long comparisons;
    public final long moves;
    public final long arrayAllocs;
    public final String notes;

    private Metrics(Builder b) {
        this.algo = b.algo;
        this.n = b.n;
        this.shape = b.shape;
        this.seed = b.seed;
        this.trial = b.trial;
        this.timeNs = b.timeNs;
        this.maxDepth = b.maxDepth;
        this.comparisons = b.comparisons;
        this.moves = b.moves;
        this.arrayAllocs = b.arrayAllocs;
        this.notes = b.notes;
    }

    public static final class Builder {
        private String algo = "unknown";
        private int n = -1;
        private String shape = "unknown";
        private long seed = 0L;
        private int trial = 0;
        private long timeNs = -1L;
        private int maxDepth = 0;
        private long comparisons = 0;
        private long moves = 0;
        private long arrayAllocs = 0;
        private String notes = "";

        public Builder algo(String v) { this.algo = v; return this; }
        public Builder n(int v) { this.n = v; return this; }
        public Builder shape(String v) { this.shape = v; return this; }
        public Builder seed(long v) { this.seed = v; return this; }
        public Builder trial(int v) { this.trial = v; return this; }
        public Builder timeNs(long v) { this.timeNs = v; return this; }
        public Builder maxDepth(int v) { this.maxDepth = v; return this; }
        public Builder comparisons(long v) { this.comparisons = v; return this; }
        public Builder moves(long v) { this.moves = v; return this; }
        public Builder arrayAllocs(long v) { this.arrayAllocs = v; return this; }
        public Builder notes(String v) { this.notes = v; return this; }

        public Metrics build() { return new Metrics(this); }
    }

    /** CSV header order */
    public static String csvHeader() {
        return "algo,n,shape,seed,trial,timeNs,maxDepth,comparisons,moves,arrayAllocs,notes";
    }

    /** CSV row matching header. Commas in notes are quoted. */
    public String toCsvRow() {
        return String.join(",",
                escape(algo),
                Integer.toString(n),
                escape(shape),
                Long.toString(seed),
                Integer.toString(trial),
                Long.toString(timeNs),
                Integer.toString(maxDepth),
                Long.toString(comparisons),
                Long.toString(moves),
                Long.toString(arrayAllocs),
                escape(notes)
        );
    }

    private static String escape(String s) {
        if (s == null) return "";
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + v + "\"" : v;
    }
}
