package org.example;

import static org.example.AlgoUtils.partitionLomuto;
import static org.example.AlgoUtils.swap;
public final class Select {
    private static final int CUTOFF = 16;
    public static int selectK(int[] a, int k) { return selectK(a, k, null, null); }

    public static int selectK(int[] a, int k, Counters c, DepthTracker d) {
        if (a == null) throw new IllegalArgumentException("array is null");
        if (k < 0 || k >= a.length) throw new IllegalArgumentException("k out of range");
        if (c == null) c = new Counters();
        if (d == null) d = new DepthTracker();
        return selectIdx(a, 0, a.length - 1, k, c, d);
    }
    private static int selectIdx(int[] a, int l, int r, int k, Counters c, DepthTracker d) {
        while (true) {
            int n = r - l + 1;
            if (n <= CUTOFF) { insertion(a, l, r, c); return a[k]; }

            int pivotIdx = medianOfMedians(a, l, r, c, d);

            int p = partitionLomuto(a, l, r, pivotIdx, c);

            if (k == p) return a[p];

            if (k < p) {

                int leftSize = p - 1 - l + 1;
                int rightSize = r - (p + 1) + 1;
                if (leftSize <= rightSize) {
                    try (var g = d.enter()) { return selectIdx(a, l, p - 1, k, c, d); }
                } else {

                    try (var g = d.enter()) { selectIdx(a, p + 1, r, k, c, d); }
                    r = p - 1;
                }
            } else { // k > p
                int leftSize = p - 1 - l + 1;
                int rightSize = r - (p + 1) + 1;
                if (rightSize <= leftSize) {
                    try (var g = d.enter()) { return selectIdx(a, p + 1, r, k, c, d); }
                } else {
                    try (var g = d.enter()) { selectIdx(a, l, p - 1, k, c, d); }
                    l = p + 1;
                }
            }
        }
    }

    private static int medianOfMedians(int[] a, int l, int r, Counters c, DepthTracker d) {
        int n = r - l + 1;
        int groups = (n + 4) / 5;

        for (int g = 0; g < groups; g++) {
            int gl = l + g * 5;
            int gr = Math.min(gl + 4, r);
            int mIdx = medianOfFiveIndex(a, gl, gr, c);
            swap(a, l + g, mIdx, c);
        }

        int medBlockL = l;
        int medBlockR = l + groups - 1;
        int medK = medBlockL + (groups - 1) / 2;

        return selectPosition(a, medBlockL, medBlockR, medK, c, d);
    }

    private static int selectPosition(int[] a, int l, int r, int k, Counters c, DepthTracker d) {
        while (true) {
            int n = r - l + 1;
            if (n <= CUTOFF) { insertion(a, l, r, c); return k; }

            int pivotIdx = medianOfMedians(a, l, r, c, d);
            int p = partitionLomuto(a, l, r, pivotIdx, c);

            if (k == p) return p;
            if (k < p) { try (var g = d.enter()) { r = p - 1; continue; } }
            else       { try (var g = d.enter()) { l = p + 1; continue; } }
        }
    }


    private static void insertion(int[] a, int l, int r, Counters c) {
        for (int i = l + 1; i <= r; i++) {
            int x = a[i], j = i - 1;
            while (j >= l) {
                if (c != null) c.comps++;
                if (a[j] > x) { a[j + 1] = a[j]; if (c != null) c.swaps++; j--; }
                else break;
            }
            a[j + 1] = x;
        }
    }

    private static int medianOfFiveIndex(int[] a, int l, int r, Counters c) {
        insertion(a, l, r, c);
        return l + (r - l) / 2;
    }
}