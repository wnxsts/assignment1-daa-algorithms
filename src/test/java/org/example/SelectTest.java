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
        for (int k=0;k<a2.length;k++) assertEquals(3, Select.selectK(a2.clone(), k));
    }

    @Test
    void smallArrays_allK() {
        int[][] cases = { {5}, {2,1}, {3,1,2}, {9,7,5,3}, {4,1,4,2,4} };
        for (int[] a : cases) {
            int[] b = a.clone(); Arrays.sort(b);
            for (int k = 0; k < a.length; k++) {
                int v = Select.selectK(a.clone(), k);
                assertEquals(b[k], v, "k="+k+" on "+Arrays.toString(a));
            }
        }
    }

    @Test
    void randomMany_compareWithSort() {
        Random rnd = new Random(123);
        for (int t = 0; t < 50; t++) {
            int n = 1 + rnd.nextInt(4000);
            int[] a = rnd.ints(n, -100_000, 100_000).toArray();
            int[] b = a.clone(); Arrays.sort(b);
            int[] ks = {0, n/2, n-1};
            for (int k: ks) assertEquals(b[k], Select.selectK(a.clone(), k));
        }
    }

    @Test
    void metricsAndDepthReasonable() {
        int n = 30_000;
        int[] a = new Random(42).ints(n, -1_000_000, 1_000_000).toArray();
        var c = new Counters();
        var d = new DepthTracker();
        int k = n/2;
        int v = Select.selectK(a, k, c, d);
        int[] b = a.clone(); Arrays.sort(b);
        assertEquals(b[k], v);
        int log2n = 31 - Integer.numberOfLeadingZeros(n);
        assertTrue(d.maxDepth() <= 6*log2n + 20);
    }
}