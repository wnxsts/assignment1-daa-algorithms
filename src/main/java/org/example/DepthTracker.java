package org.example;

public final class DepthTracker {
    private int current = 0;
    private int maxDepth = 0;

    public final class Guard implements AutoCloseable {
        private boolean closed = false;
        private Guard() {
            current++;
            if (current > maxDepth) maxDepth = current;
        }
        @Override public void close() {
            if (!closed) { current--; closed = true; }
        }
    }

    public Guard enter() { return new Guard(); }

    public int currentDepth() { return current; }
    public int maxDepth() { return maxDepth; }
    public void reset() { current = maxDepth = 0; }
}