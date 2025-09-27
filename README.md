# Algorithm Lab (Assignment 1)

## Overview

Java 21 implementations of classic divide-and-conquer algorithms with safe recursion patterns, runtime analysis, and metrics collection.

## Algorithms

- MergeSort (with buffer reuse, cutoff to insertion sort)
- QuickSort (random pivot, smaller-first recursion)
- Deterministic Select (Median-of-Medians)
- Closest Pair of Points (2D)

## Metrics

- Time
- Recursion depth
- Comparisons, moves
- Allocations (controlled arrays)

## Structure

- `common/` helpers
- `metrics/` counters, depth tracker, CSV writer
- `sort/`, `select/`, `geometry/`
- `cli/` for command-line execution

## Build & Test

```bash
mvn clean test
```
