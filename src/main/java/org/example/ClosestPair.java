package org.example;

import java.util.Arrays;
import java.util.Comparator;

public final class ClosestPair {

    public record Point(int x, int y) {}

    public static double findClosestPair(Point[] points) {
        if (points == null || points.length < 2)
            throw new IllegalArgumentException("Need at least 2 points");

        Point[] pts = points.clone();
        Arrays.sort(pts, Comparator.comparingInt(p -> p.x()));
        return closestRec(pts, 0, pts.length - 1);
    }

    private static double closestRec(Point[] pts, int l, int r) {
        if (r - l <= 3) {
            double min = Double.POSITIVE_INFINITY;
            for (int i = l; i <= r; i++) {
                for (int j = i + 1; j <= r; j++) {
                    min = Math.min(min, dist(pts[i], pts[j]));
                }
            }
            Arrays.sort(pts, l, r + 1, Comparator.comparingInt(p -> p.y()));
            return min;
        }

        int mid = (l + r) / 2;
        int midX = pts[mid].x();

        double d1 = closestRec(pts, l, mid);
        double d2 = closestRec(pts, mid + 1, r);
        double d = Math.min(d1, d2);

        Point[] strip = new Point[r - l + 1];
        mergeByY(pts, l, mid, r, strip);

        int m = 0;
        for (int i = l; i <= r; i++) {
            if (Math.abs(pts[i].x() - midX) < d) {
                for (int j = m - 1; j >= 0 && pts[i].y() - strip[j].y() < d; j--) {
                    d = Math.min(d, dist(pts[i], strip[j]));
                }
                strip[m++] = pts[i];
            }
        }
        return d;
    }

    private static void mergeByY(Point[] pts, int l, int m, int r, Point[] tmp) {
        int i = l, j = m + 1, k = 0;
        while (i <= m && j <= r) {
            if (pts[i].y() <= pts[j].y()) tmp[k++] = pts[i++];
            else tmp[k++] = pts[j++];
        }
        while (i <= m) tmp[k++] = pts[i++];
        while (j <= r) tmp[k++] = pts[j++];
        System.arraycopy(tmp, 0, pts, l, r - l + 1);
    }

    private static double dist(Point a, Point b) {
        long dx = (long) a.x() - b.x();
        long dy = (long) a.y() - b.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}