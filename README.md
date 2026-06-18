# Sink Elimination Algorithm

Java implementation of the **Sink Elimination Algorithm** to determine whether a directed graph is acyclic (a DAG), with DFS-based cycle tracing and empirical performance benchmarking using the doubling hypothesis.

---

## Table of Contents

- [Overview](#overview)
- [Algorithm](#algorithm)
- [Classes](#classes)
- [Sample Output](#sample-output)
- [Performance Benchmarks](#performance-benchmarks)
- [Time Complexity](#time-complexity)

---

## Overview

A **Directed Acyclic Graph (DAG)** is a directed graph with no cycles. Detecting whether a directed graph contains a cycle is a fundamental problem in computer science with applications in scheduling, dependency resolution, and compiler design.

This project implements the **Sink Elimination Algorithm**, which works by repeatedly removing sink vertices (vertices with no outgoing edges) from the graph. If the graph empties completely, it is acyclic. If at any point no sink can be found among the remaining vertices, a cycle must exist.

When a cycle is detected, a secondary **Depth-First Search (DFS)** algorithm traces and reports the exact vertices forming the cycle.

---

## Algorithm

### Sink Elimination

A **sink** is a vertex with no outgoing edges (out-degree = 0).

**Steps:**
1. Find a sink vertex in the graph.
2. If no sink exists → a cycle is present among the remaining vertices. Stop.
3. Remove the sink and all edges pointing to it.
4. Repeat from step 1 until the graph is empty.
5. If the graph empties → the graph is acyclic (a DAG).

**Why it works:**  
Every DAG must have at least one sink. If no sink exists, every vertex has at least one outgoing edge, which means the graph must contain a cycle (by the pigeonhole principle on a finite graph).

### Cycle Tracing (DFS)

When a cycle is detected, a DFS-based `CycleFinder` is run on the remaining vertices to trace the exact cycle path using parent pointers and a recursion stack.

---

## Classes

### `DirectedGraph`
Represents a directed graph using a `HashMap<Integer, LinkedList<Integer>>` adjacency list. Supports adding vertices and edges, finding and removing sink vertices, copying the graph, and printing the adjacency list.

### `AcyclicityChecker`
Contains the static `check(DirectedGraph graph, boolean verbose)` method. Implements the sink elimination algorithm — iteratively finds and removes sinks until either the graph is empty (acyclic) or no sink exists (cyclic).

### `CycleFinder`
Uses recursive DFS with a parent map and an active recursion stack (`onStack`) to detect back edges. When a back edge is found, it reconstructs the cycle path using parent pointers.

### `GraphParser`
Reads directed graphs from `.txt` benchmark files. Supports two formats — a vertex-count header format (benchmark files) and a direct edge-list format.

### `Main`
Entry point that runs three parts:
- **Part 1** — Small hand-crafted 6-vertex acyclic and cyclic examples
- **Part 2** — Benchmark file demo (40-vertex graphs)
- **Part 3** — Doubling hypothesis performance benchmarks across 9 graph sizes

---

## Sample Output

### Part 1 — Small Acyclic Graph (6 vertices)

```
Adjacency list (HashMap<Integer, LinkedList<Integer>>):
  0 -> [1, 2]
  1 -> [2, 4]
  2 -> [4]
  3 -> [4]
  4 -> [5]
  5 -> []  <- sink

Running Sink Elimination Algorithm...
  Step   1: Sink found -> vertex 5     | Vertices remaining: 5
  Step   2: Sink found -> vertex 4     | Vertices remaining: 4
  ...
  All 6 vertices successfully eliminated.

RESULT: YES (Graph is ACYCLIC)
```

### Part 1 — Small Cyclic Graph (6 vertices)

```
Running Sink Elimination Algorithm...
  Step   1: Sink found -> vertex 5     | Vertices remaining: 5
  Step   2: Sink found -> vertex 4     | Vertices remaining: 4
  Step   3: Sink found -> vertex 3     | Vertices remaining: 3
  Step 4: No sink found! Stopping early.
  Remaining vertices (contain cycle): [0, 1, 2]

Tracing cycle using DFS...
CYCLE FOUND: 1 -> 2 -> 1

RESULT: NO (Graph CONTAINS A CYCLE)
```

---

## Performance Benchmarks

Benchmarks were run using the **doubling hypothesis** — graph size doubles at each step. Each size was tested with 5 acyclic and 5 cyclic trials, averaged. File I/O was excluded from timing.

| Size (V) | Acyclic (ms) | Ratio A | Cyclic (ms) | Ratio C |
|----------|-------------|---------|------------|---------|
| 40       | 0.477       | —       | 0.064      | —       |
| 80       | 0.300       | 0.63    | 0.149      | 2.32    |
| 160      | 1.216       | 4.05    | 0.348      | 2.34    |
| 320      | 13.922      | 11.44   | 1.677      | 4.82    |
| 640      | 12.057      | 0.87    | 7.501      | 4.47    |
| 1280     | 32.254      | 2.68    | 21.992     | 2.93    |
| 2560     | 105.038     | 3.26    | 60.880     | 2.77    |
| 5120     | 422.992     | 4.03    | 372.988    | 6.13    |
| 10240    | 1777.822    | 4.20    | 1387.507   | 3.72    |

The ratios stabilising around **2–4x** as size doubles is consistent with the expected **O(V + E)** linear time complexity.

---

## Time Complexity

| Operation         | Complexity |
|-------------------|------------|
| Sink Elimination  | O(V + E)   |
| Cycle Tracing DFS | O(V + E)   |
| Overall           | O(V + E)   |

- **V** = number of vertices
- **E** = number of edges

The sink elimination algorithm visits each vertex at most once and removes edges incrementally, giving linear time performance relative to the size of the graph.