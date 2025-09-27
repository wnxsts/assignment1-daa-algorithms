package org.example;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import static org.example.AlgoUtils.*;

public final class QuickSort {

    public static void sort(int[] a) { sort(a, null, null); }
    public static void sort(int[] a, Counters c, DepthTracker d) {

        if (a == null || a.length <= 1) return;
        if (c == null) c = new Counters();
        if (d == null) d = new DepthTracker();
        Random rng = ThreadLocalRandom.current();
        quick(a, 0, a.length - 1, rng, c, d);
    }

    private static void quick(int[] a, int l, int r, Random rng, Counters c, DepthTracker d) {
        while (l < r) {
            int p = partitionRandomPivot(a, l, r, rng, c);
            int leftSize  = p - l;
            int rightSize = r - p;

            if (leftSize < rightSize) {
                try (var g = d.enter()) { quick(a, l, p - 1, rng, c, d); }
                l = p + 1;
            } else {
                try (var g = d.enter()) { quick(a, p + 1, r, rng, c, d); }
                r = p - 1;
            }
        }
    }

    private static int partitionRandomPivot(int[] a, int l, int r, Random rng, Counters c) {
        int pivotIdx = l + rng.nextInt(r - l + 1);
        swap(a, pivotIdx, r, c);
        int pivot = a[r];
        int i = l;
        for (int j = l; j < r; j++) {
            c.comps++;
            if (a[j] <= pivot) {
                swap(a, i, j, c);
                i++;
            }
        }
        swap(a, i, r, c);
        return i;
    }

    private static void swap(int[] a, int i, int j, Counters c) {
        if (i == j) return;
        int t = a[i]; a[i] = a[j]; a[j] = t;
        if (c != null) c.swaps++;
    }
}