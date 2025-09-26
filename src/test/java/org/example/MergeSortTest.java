package org.example;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class MergeSortTest {

    @Test
    void edgeCases() {
        int[] a = {};
        MergeSort.sort(a);
        assertArrayEquals(new int[]{}, a);

        int[] b = {5};
        MergeSort.sort(b);
        assertArrayEquals(new int[]{5}, b);

        int[] c = {3,3,3,3};
        MergeSort.sort(c);
        assertArrayEquals(new int[]{3,3,3,3}, c);
    }

    @Test
    void alreadySortedAndReverse() {
        int[] s = {1,2,3,4,5,6,7,8,9};
        int[] sc = s.clone();
        MergeSort.sort(sc);
        assertArrayEquals(s, sc);

        int[] r = {9,8,7,6,5,4,3,2,1};
        int[] rc = r.clone();
        MergeSort.sort(rc);
        Arrays.sort(r);
        assertArrayEquals(r, rc);
    }

    @Test
    void randomMany_vsJdkSort() {
        Random rnd = new Random(42);
        for (int t = 0; t < 30; t++) {
            int n = 1 + rnd.nextInt(3000);
            int[] a = rnd.ints(n, -10000, 10000).toArray();
            int[] b = a.clone();
            MergeSort.sort(a);
            Arrays.sort(b);
            assertArrayEquals(b, a);
        }
    }

    @Test
    void metricsCollected_depthAndAlloc() {
        int[] a = {5,4,3,2,1,0,7,6,9,8,10,11,12,13,14,15,16,17};
        var c = new Counters();
        var d = new DepthTracker();
        MergeSort.sort(a, c, d);
        assertEquals(1, c.allocs);
        assertTrue(d.maxDepth() > 0);
        assertTrue(isSorted(a));
    }

    private static boolean isSorted(int[] a){
        for (int i=1;i<a.length;i++) if (a[i-1] > a[i]) return false;
        return true;
    }
}