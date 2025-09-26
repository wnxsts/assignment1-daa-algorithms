package org.example;

import org.junit.jupiter.api.Test;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

public class ClosestPairTest {

    @Test
    void tinyCase() {
        var pts = new ClosestPair.Point[]{
                new ClosestPair.Point(0, 0),
                new ClosestPair.Point(3, 4),
                new ClosestPair.Point(7, 7)
        };
        assertEquals(5.0, ClosestPair.findClosestPair(pts), 1e-9);
    }

    @Test
    void randomVsNaive() {
        Random rnd = new Random(42);
        for (int n = 2; n < 200; n++) {
            ClosestPair.Point[] pts = new ClosestPair.Point[n];
            for (int i = 0; i < n; i++) {
                pts[i] = new ClosestPair.Point(rnd.nextInt(1000), rnd.nextInt(1000));
            }
            double fast = ClosestPair.findClosestPair(pts);
            double slow = naive(pts);
            assertEquals(slow, fast, 1e-9);
        }
    }

    private static double naive(ClosestPair.Point[] pts) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < pts.length; i++) {
            for (int j = i + 1; j < pts.length; j++) {
                long dx = (long) pts[i].x() - pts[j].x();
                long dy = (long) pts[i].y() - pts[j].y();
                min = Math.min(min, Math.sqrt(dx * dx + dy * dy));
            }
        }
        return min;
    }
    }
