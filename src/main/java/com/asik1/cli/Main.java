package com.asik1.cli;

import com.asik1.metrics.*;
import com.asik1.sort.MergeSort;
import com.asik1.sort.QuickSort;
import com.asik1.select.DeterministicSelect;
import com.asik1.geometry.ClosestPair2D;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Random;

/**
 * CLI runner for Assignment 1.
 * Example:
 *   java -cp target/algolab-0.1-SNAPSHOT.jar com.asik1.cli.Main \
 *     --algo mergesort --n 10000 --shape random --seed 42 --trials 3 --csv results/out.csv
 */
public class Main {

    public static void main(String[] args) {
        Config cfg = Config.parseArgs(args);
        CsvWriter writer = cfg.csvPath != null ? new CsvWriter(cfg.csvPath) : null;

        for (int trial = 1; trial <= cfg.trials; trial++) {
            int[] arr = makeInput(cfg.n, cfg.shape, cfg.seed + trial);

            Counters ctr = new Counters();
            DepthTracker depth = new DepthTracker();

            long start = System.nanoTime();
            switch (cfg.algo) {
                case "mergesort":
                    MergeSort.sort(arr, ctr, depth);
                    break;
                case "quicksort":
                    QuickSort.sort(arr, ctr, depth);
                    break;
                case "select": {
                    int k = cfg.n / 2; // median for demo
                    DeterministicSelect.select(arr, k, ctr, depth);
                    break;
                }
                case "closest": {
                    int[] ys = Arrays.copyOf(arr, arr.length);
                    ClosestPair2D.closestPair(arr, ys, ctr, depth);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown algo: " + cfg.algo);
            }
            long time = System.nanoTime() - start;

            Metrics m = new Metrics.Builder()
                    .algo(cfg.algo)
                    .n(cfg.n)
                    .shape(cfg.shape)
                    .seed(cfg.seed)
                    .trial(trial)
                    .timeNs(time)
                    .maxDepth(depth.getMaxDepth())
                    .comparisons(ctr.getComparisons())
                    .moves(ctr.getMoves())
                    .arrayAllocs(ctr.getArrayAllocs())
                    .notes("")
                    .build();

            if (writer != null) writer.append(m);

            // For visibility in console
            System.out.println(m.toCsvRow());
        }
    }

    private static int[] makeInput(int n, String shape, long seed) {
        Random rnd = new Random(seed);
        int[] a = new int[n];
        switch (shape) {
            case "random":
                for (int i = 0; i < n; i++) a[i] = rnd.nextInt();
                break;
            case "sorted":
                for (int i = 0; i < n; i++) a[i] = i;
                break;
            case "reverse":
                for (int i = 0; i < n; i++) a[i] = n - i;
                break;
            case "fewuniq":
                for (int i = 0; i < n; i++) a[i] = rnd.nextInt(5);
                break;
            case "allequal":
                Arrays.fill(a, 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown shape: " + shape);
        }
        return a;
    }

    /** Simple config holder + parser */
    private static final class Config {
        final String algo;
        final int n;
        final String shape;
        final long seed;
        final int trials;
        final Path csvPath;

        private Config(String algo, int n, String shape, long seed, int trials, Path csvPath) {
            this.algo = algo; this.n = n; this.shape = shape; this.seed = seed; this.trials = trials; this.csvPath = csvPath;
        }

        static Config parseArgs(String[] args) {
            String algo = "mergesort";
            int n = 1000;
            String shape = "random";
            long seed = 42L;
            int trials = 1;
            Path csv = null;

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "--algo":
                        algo = args[++i];
                        break;
                    case "--n":
                        n = Integer.parseInt(args[++i]);
                        break;
                    case "--shape":
                        shape = args[++i];
                        break;
                    case "--seed":
                        seed = Long.parseLong(args[++i]);
                        break;
                    case "--trials":
                        trials = Integer.parseInt(args[++i]);
                        break;
                    case "--csv":
                        csv = Path.of(args[++i]);
                        break;
                    default:
                        System.err.println("Unknown arg: " + args[i]);
                        usageAndExit();
                }
            }
            return new Config(algo, n, shape, seed, trials, csv);
        }

        static void usageAndExit() {
            System.err.println("Usage: --algo mergesort|quicksort|select|closest "
                    + "--n N --shape random|sorted|reverse|fewuniq|allequal "
                    + "[--seed s] [--trials t] [--csv file]");
            System.exit(1);
        }
    }
}
