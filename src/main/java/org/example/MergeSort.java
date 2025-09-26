package org.example;
public final class MergeSort {
    private static final int CUTOFF = 16;

    public static void sort(int[] a) { sort(a, null, null); }
    public static void sort(int[] a, Counters c, DepthTracker d) {
        if (a == null || a.length < 2) return;
        if (c == null) c = new Counters();
        if (d == null) d = new DepthTracker();
        int[] tmp = new int[a.length];
        c.allocs++;
        sortRec(a, 0, a.length - 1, tmp, c, d);
    }

    private static void sortRec(int[] a, int l, int r, int[] tmp, Counters c, DepthTracker d) {
        try (var g = d.enter()) {
            if (r - l + 1 <= CUTOFF) { insertion(a, l, r, c); return; }
            int m = (l + r) >>> 1;
            sortRec(a, l, m, tmp, c, d);
            sortRec(a, m + 1, r, tmp, c, d);
            c.comps++;
            if (a[m] <= a[m + 1]) return;
            merge(a, l, m, r, tmp, c);
        }
    }
    private static void insertion(int[] a, int l, int r, Counters c) {
        for (int i = l + 1; i <= r; i++) {
            int x = a[i], j = i - 1;
            while (j >= l) {
                c.comps++;
                if (a[j] > x) { a[j + 1] = a[j]; c.swaps++; j--; }
                else break;
            }
            a[j + 1] = x;
        }
    }

    private static void merge(int[] a, int l, int m, int r, int[] tmp, Counters c) {
        int i = l, j = m + 1, k = l;
        while (i <= m && j <= r) {
            c.comps++;
            if (a[i] <= a[j]) tmp[k++] = a[i++];
            else              tmp[k++] = a[j++];
        }
        while (i <= m) tmp[k++] = a[i++];
        while (j <= r) tmp[k++] = a[j++];
        for (int t = l; t <= r; t++) a[t] = tmp[t];
    }
}