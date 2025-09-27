package org.example;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
public class SelectVsSortBench {

    @Param({"10000","50000","100000"})
    public int n;

    // 0% = минимум, 50% = медиана, 100% = максимум
    @Param({"0","50","100"})
    public int kPercent;

    @Param({"42"})
    public long seed;

    private int[] base;
    private int kIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(seed);
        base = rnd.ints(n, -1_000_000, 1_000_000).toArray();
        kIndex = (int) ((long)(n - 1) * kPercent / 100);
        if (kIndex < 0) kIndex = 0;
        if (kIndex >= n) kIndex = n - 1;
    }

    @Benchmark
    public void fullSortThenPick(Blackhole bh) {
        int[] a = base.clone();
        Arrays.sort(a);
        bh.consume(a[kIndex]);
    }

    @Benchmark
    public void deterministicSelect(Blackhole bh) {
        int[] a = base.clone();
        int v = Select.selectK(a, kIndex);
        bh.consume(v);
    }
}