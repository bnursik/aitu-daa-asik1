# Algorithm Lab (Divide-and-Conquer Benchmarks)

This project implements classic divide-and-conquer algorithms in **Java 21**, with safe recursion patterns, metrics collection, and empirical validation against asymptotic theory.

---

## Implemented Algorithms

- **MergeSort**  
  Linear merge, reusable buffer, cutoff to insertion sort.  
  → Master Theorem Case 2.

- **QuickSort**  
  Randomized pivot, recurse only on the smaller side, bounded recursion depth.

- **Deterministic Select (Median-of-Medians, MoM5)**  
  Groups of 5, median-of-medians pivot, in-place partition, recurse into smaller side only.

- **Closest Pair of Points (2D)**  
  Divide-and-conquer with strip check (≤ 7 neighbors).

### Metrics Collected

- Execution time (ns)
- Recursion depth
- Comparisons
- Moves (assignments)
- Array allocations

---

## Recurrence Analyses

### MergeSort

- Recurrence:  
  \[
  T(n) = 2T(n/2) + Θ(n)
  \]
- Master Theorem, Case 2 →  
  \[
  T(n) = Θ(n \log n)
  \]
- Depth ≈ log₂(n).

### QuickSort

- With randomized pivot:  
  \[
  T(n) = T(U) + T(n-U-1) + Θ(n),\quad U \sim \text{Uniform}(0..n-1)
  \]
- Expected: Θ(n log n). Worst: Θ(n²).
- With smaller-side recursion, depth ≤ ~2·log₂(n).

### Deterministic Select (MoM5)

- Pivot is between 30–70th percentile.
- Recurrence:  
  \[
  T(n) = T(\lceil n/5 \rceil) + T(≤7n/10) + Θ(n)
  \]
- By Akra–Bazzi intuition → Θ(n).
- Depth ~ log n.

### Closest Pair of Points (2D)

- Recurrence:  
  \[
  T(n) = 2T(n/2) + Θ(n)
  \]
- Master Theorem, Case 2 → Θ(n log n).
- Strip step checks ≤7 neighbors → constant factor.

---

## Plots (Preliminary)

Generated from CSV output of CLI runner and JMH harness.

### Time vs n

- MergeSort, QuickSort: ~ n log n.
- Deterministic Select: ~ linear.
- Closest Pair: ~ n log n with larger constants.

![time_vs_n](plots/time_vs_n.png)

### Depth vs n

- MergeSort: depth ≈ log₂(n).
- QuickSort: depth ≤ 2·log₂(n).
- Select: log n.
- Closest Pair: log₂(n).

![depth_vs_n](plots/depth_vs_n.png)

---

## Summary

- **Theory matched measurements**: asymptotic growth confirmed.
- **Constant factors matter**: insertion sort cutoff improves small-n; QuickSort outperforms MergeSort despite same asymptotic.
- **MoM5 Select** has higher constants; slower than `Arrays.sort()+k` on small n, but scales linearly.
- **Closest Pair** matches n log n scaling but is constant-heavy.

---

## Usage

### Build & Test

```bash
mvn clean test
```

# CLI Runner & Benchmarks

The **CLI Runner** executes algorithms on synthetic inputs and writes metrics to CSV for plotting.

---

## CLI Runner

### Base Command

```bash
java -cp target/algolab-0.1-SNAPSHOT.jar com.asik1.cli.Main \
  --algo <algo> --n <size> --shape <shape> [--seed <long>] [--trials <int>] [--csv <file>]
```

# Benchmarks (JMH)

This project includes **microbenchmarks** (e.g., Select vs Sort) using the JMH framework.

---

## Build & Run

```bash
mvn -Pbench -DskipTests clean package
java -jar target/benchmarks.jar
``
```
