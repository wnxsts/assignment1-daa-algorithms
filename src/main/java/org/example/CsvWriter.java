package org.example;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public final class CsvWriter implements Closeable, Flushable {
    private final BufferedWriter out;
    private boolean headerWritten = false;

    public CsvWriter(Path path) throws IOException {
        Path dir = path.getParent();
        if (dir != null) Files.createDirectories(dir);
        boolean exists = Files.exists(path);
        out = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        headerWritten = exists && fileNotEmpty(path);
    }

    private static boolean fileNotEmpty(Path p) {
        try { return Files.size(p) > 0; } catch (IOException e) { return false; }
    }

    public void writeHeader(String... cols) throws IOException {
        if (!headerWritten) {
            writeRow((Object[]) cols);
            headerWritten = true;
        }
    }

    public void writeRow(Object... cols) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(String.valueOf(cols[i])));
        }
        sb.append('\n');
        out.write(sb.toString());
    }

    private static String escape(String s) {
        boolean needQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String v = s.replace("\"", "\"\"");
        return needQuotes ? "\"" + v + "\"" : v;
    }

    @Override public void flush() throws IOException { out.flush(); }
    @Override public void close() throws IOException { out.flush(); out.close(); }
}