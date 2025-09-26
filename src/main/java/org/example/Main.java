package org.example;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

public final class Main {

    // -------- аргументы CLI --------
    private static final class Args {
        String algo = null;          // mergesort | quicksort | select | closest
        int n = 0;                   // размер массива / число точек
        int k = -1;                  // индекс для select
        int trials = 1;              // сколько прогонов делать
        long seed = System.nanoTime();
        Path csv = Path.of("results.csv");
        int range = 1_000_000;       // значения/координаты лежат в [-range/2 .. range/2]
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length == 0) { printHelp(); return; }
        Args a = parseArgs(argv);

        try (CsvWriter csv = new CsvWriter(a.csv)) {
            // порядок колонок стабильный для отчётов
            csv.writeHeader("ts","algo","n","trial","millis","comps","swaps","allocs","depth","extra");

            Random rnd = new Random(a.seed);
            for (int t = 1; t <= a.trials; t++) {
                var c = new Counters();
                var d = new DepthTracker();

                long startNs, ms;
                String extra = "";

                switch (a.algo) {
                    case "mergesort" -> {
                        int[] arr = rnd.ints(a.n, -a.range, a.range).toArray();
                        startNs = System.nanoTime();
                        MergeSort.sort(arr, c, d);
                        ms = (System.nanoTime() - startNs) / 1_000_000;
                        if (!isSorted(arr)) throw new AssertionError("mergesort failed");
                    }

                    case "quicksort" -> {
                        int[] arr = rnd.ints(a.n, -a.range, a.range).toArray();
                        startNs = System.nanoTime();
                        QuickSort.sort(arr, c, d);
                        ms = (System.nanoTime() - startNs) / 1_000_000;
                        if (!isSorted(arr)) throw new AssertionError("quicksort failed");
                    }

                    case "select" -> {
                        if (a.k < 0 || a.k >= a.n)
                            throw new IllegalArgumentException("--k must be in [0..n-1]");
                        int[] arr = rnd.ints(a.n, -a.range, a.range).toArray();

                        startNs = System.nanoTime();
                        int val = Select.selectK(arr, a.k, c, d);    // твоя реализация deterministic select (MoM5)
                        ms = (System.nanoTime() - startNs) / 1_000_000;

                        // верификация
                        int[] copy = arr.clone();
                        Arrays.sort(copy);
                        if (val != copy[a.k]) throw new AssertionError("select failed");
                        extra = Integer.toString(val);
                    }

                    case "closest" -> {
                        ClosestPair.Point[] pts = new ClosestPair.Point[a.n];
                        for (int i = 0; i < a.n; i++) {
                            pts[i] = new ClosestPair.Point(
                                    rnd.nextInt(a.range) - a.range/2,
                                    rnd.nextInt(a.range) - a.range/2);
                        }
                        startNs = System.nanoTime();
                        double dist = ClosestPair.findClosestPair(pts);  // твоя реализация O(n log n)
                        ms = (System.nanoTime() - startNs) / 1_000_000;
                        extra = Double.toString(dist);
                    }

                    default -> throw new IllegalArgumentException("Unknown --algo: " + a.algo);
                }

                // Пишем строку метрик
                csv.writeRow(
                        System.currentTimeMillis(), a.algo, a.n, t, ms,
                        c.comps, c.swaps, c.allocs, d.maxDepth(), extra
                );
            }
        }

        System.out.println("Done. CSV -> " + Path.of("results.csv").toAbsolutePath());
    }

    // ---------- утилиты ----------
    private static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i-1] > a[i]) return false;
        return true;
    }

    private static Args parseArgs(String[] argv) {
        Args a = new Args();
        for (int i = 0; i < argv.length; i++) {
            String s = argv[i];
            switch (s) {
                case "--algo"   -> a.algo  = need(argv, ++i, "--algo");     // mergesort|quicksort|select|closest
                case "--n"      -> a.n     = Integer.parseInt(need(argv, ++i, "--n"));
                case "--k"      -> a.k     = Integer.parseInt(need(argv, ++i, "--k"));
                case "--trials" -> a.trials= Integer.parseInt(need(argv, ++i, "--trials"));
                case "--seed"   -> a.seed  = Long.parseLong(need(argv, ++i, "--seed"));
                case "--csv"    -> a.csv   = Path.of(need(argv, ++i, "--csv"));
                case "--range"  -> a.range = Integer.parseInt(need(argv, ++i, "--range"));
                case "-h", "--help" -> { printHelp(); System.exit(0); }
                default -> throw new IllegalArgumentException("Unknown arg: " + s);
            }
        }
        if (a.algo == null) throw new IllegalArgumentException("Missing --algo");
        if (a.n <= 0)        throw new IllegalArgumentException("Missing/invalid --n");
        if (a.algo.equals("select") && (a.k < 0 || a.k >= a.n))
            throw new IllegalArgumentException("For --algo select you must pass valid --k");
        return a;
    }

    private static String need(String[] a, int i, String key) {
        if (i >= a.length) throw new IllegalArgumentException("Value for " + key + " is missing");
        return a[i];
    }

    private static void printHelp() {
        System.out.println("""
            Usage:
              --algo {mergesort|quicksort|select|closest}
              --n <size>            input size (array length or number of points)
              --trials <m>          repeat runs (default 1)
              --seed <long>         RNG seed (default nanoTime)
              --csv <path>          output CSV file (default results.csv)
              --k <idx>             only for --algo select (0..n-1)
              --range <R>           values/coords in [-R/2..R/2] (default 1_000_000)

            Examples:
              --algo mergesort --n 200000 --trials 3 --csv out.csv
              --algo quicksort --n 200000 --trials 3 --csv out.csv
              --algo select --n 100000 --k 500 --trials 5 --csv out.csv
              --algo closest --n 50000 --trials 5 --csv out.csv
            """);
    }
}