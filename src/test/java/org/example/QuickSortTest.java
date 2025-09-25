package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class QuickSortTest {

    @Test
    void edgeCases() {
        int[] a = {};
        QuickSort.sort(a);
        assertArrayEquals(new int[]{}, a);

        int[] b = {7};
        QuickSort.sort(b);
        assertArrayEquals(new int[]{7}, b);

        int[] c = {3,3,3,3};
        QuickSort.sort(c);
        assertArrayEquals(new int[]{3,3,3,3}, c);
    }

    @Test
    void sortedAndReverse() {
        int[] s = {1,2,3,4,5,6,7,8,9};
        int[] sc = s.clone();
        QuickSort.sort(sc);
        assertArrayEquals(s, sc);

        int[] r = {9,8,7,6,5,4,3,2,1};
        int[] rc = r.clone();
        QuickSort.sort(rc);
        Arrays.sort(r);
        assertArrayEquals(r, rc);
    }

    @Test
    void randomMany_vsJdkSort() {
        Random rnd = new Random(123);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(5000);
            int[] a = rnd.ints(n, -100_000, 100_000).toArray();
            int[] b = a.clone();
            QuickSort.sort(a);
            Arrays.sort(b);
            assertArrayEquals(b, a, "mismatch at trial " + t + " for n=" + n);
        }
    }

    @Test
    void depthIsLogarithmic_typically() {
        int n = 20_000;
        int[] a = new Random(42).ints(n, -1_000_000, 1_000_000).toArray();
        var c = new Counters();
        var d = new DepthTracker();
        QuickSort.sort(a, c, d);

        // ожидаемая глубина O(log n). Разрешим небольшой запас.
        int log2n = 31 - Integer.numberOfLeadingZeros(n);
        assertTrue(d.maxDepth() <= 2 * log2n + 10,
                "depth too large: " + d.maxDepth() + " for n=" + n);
        assertTrue(isSorted(a));
    }

    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }
}