package com.asik1.common;

public final class Guards {
    private Guards() {}

    public static void checkSlice(int lo, int hi, int length) {
        if (lo < 0 || hi >= length || lo > hi) {
            throw new IllegalArgumentException("Invalid slice: [" + lo + "," + hi + "] for length " + length);
        }
    }
}
