package org.example;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
public final class AlgoUtils {
    private AlgoUtils() {}

    /* ----------------------- guards ----------------------- */

    public static void guardRange(int l, int r, int n) {
        if (l < 0 || r < 0 || l >= n || r >= n || l > r) {
            throw new IllegalArgumentException("Bad range: [" + l + "," + r + "] for n=" + n);
        }
    }

    /* ----------------------- swap ------------------------- */
    public static void swap(int[] a, int i, int j, Counters c) {
        if (i == j) return;
        int t = a[i]; a[i] = a[j]; a[j] = t;
        if (c != null) c.swaps++;
    }

    /* -------------------- partition ----------------------- */

    public static int partitionLomuto(int[] a, int l, int r, int pivotIdx, Counters c) {
        swap(a, pivotIdx, r, c);
        int pivot = a[r];
        int i = l;
        for (int j = l; j < r; j++) {
            if (c != null) c.comps++;
            if (a[j] <= pivot) {
                swap(a, i, j, c);
                i++;
            }
        }
        swap(a, i, r, c);
        return i;
    }

    public static int partitionRandomLomuto(int[] a, int l, int r, Random rng, Counters c) {
        int pivotIdx = l + rng.nextInt(r - l + 1);
        return partitionLomuto(a, l, r, pivotIdx, c);
    }

    /* ---------------------- shuffle ----------------------- */

    public static void shuffle(int[] a, Random rng, Counters c) {
        for (int i = a.length - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            swap(a, i, j, c);
        }
    }

    public static void shuffle(int[] a, Counters c) {
        shuffle(a, ThreadLocalRandom.current(), c);
    }
}