package org.example;

public final class Counters {
    public long comps;
    public long swaps;
    public long allocs;

    public void reset() { comps = swaps = allocs = 0; }
}