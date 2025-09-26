package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SelectTest {

    @Test
    void edgeCases() {
        int[] a1 = {42};
        assertEquals(42, Select.selectK(a1, 0));

        int[] a2 = {3,3,3,3};
        assertEquals(3, Select.selectK(a2, 0));
        assertEquals(3, Select.selectK(a2, 1));
        assertEquals(3, Select.selectK(a2, 2));
        assertEquals(3, Select.selectK(a2, 3));
    }

    @Test
    void smallArrays_allK() {
        int[][] cases = {
                {},
                {5},
                {2,1},
                {3,1,2},
                {9,7,5,3},
                {4,1,4,2,4}
        };
        for (int[] a : cases) {
            if (a.length == 0) continue;
            int[] b = a.clone();
            Arrays.sort(b);
            for (int k = 0; k < a.length; k++) {
                int v = Select.selectK(a.clone(), k);
                assertEquals(b[k], v, "k=" + k + " on " + Arrays.toString(a));
            }
        }
    }

    @Test
    void randomMany_compareWithSort() {
        Random rnd = new Random(123);
        for (int t = 0; t < 100; t++) {
            int n = 1 + rnd.nextInt(4000);
            int[] a = rnd.ints(n, -100_000, 100_000).toArray();
            int[] b = a.clone(); Arrays.sort(b);
            int[] ks = {0, n/2, n-1};
            for (int k : ks) {
                int v = Select.selectK(a.clone(), k);
                assertEquals(b[k], v, "trial=" + t + " k=" + k + " n=" + n);
            }
        }
    }

    @Test
    void metricsAndDepth_areReasonable() {
        int n = 50_000;
        int[] a = new Random(42).ints(n, -1_000_000, 1_000_000).toArray();
        var c = new Counters();
        var d = new DepthTracker();
        int k = n/2;
        int v = Select.selectK(a, k, c, d);

        int[] b = a.clone(); Arrays.sort(b);
        assertEquals(b[k], v);
        int log2n = 31 - Integer.numberOfLeadingZeros(n);
        assertTrue(d.maxDepth() <= 6 * log2n + 20, "depth too high: " + d.maxDepth());
    }
}