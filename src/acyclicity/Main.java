package acyclicity;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final String BENCH_ACYCLIC = "benchmarks/acyclic/";
    private static final String BENCH_CYCLIC  = "benchmarks/cyclic/";
    private static final int[]  SIZES  = {40, 80, 160, 320, 640, 1280, 2560, 5120, 10240};
    private static final int    TRIALS = 5;

    public static void main(String[] args) throws IOException {

        printHeader();

        // PART 1 — Small hand-crafted example (6 vertices)
        System.out.println("========== PART 1: Small example (6 vertices) ==========\n");

        // Acyclic graph: vertices 0=a, 1=b, 2=c, 3=d, 4=e, 5=f
        // Edges: a->b, a->c, b->c, b->e, c->e, d->e, e->f
        System.out.println("--- Small ACYCLIC graph ---");
        DirectedGraph smallAcyclic = new DirectedGraph();
        smallAcyclic.addEdge(0, 1);
        smallAcyclic.addEdge(0, 2);
        smallAcyclic.addEdge(1, 2);
        smallAcyclic.addEdge(1, 4);
        smallAcyclic.addEdge(2, 4);
        smallAcyclic.addEdge(3, 4);
        smallAcyclic.addEdge(4, 5);
        System.out.println("Vertices: 6  |  Edges: 7");
        smallAcyclic.printAdjacencyList();

        System.out.println("\nRunning Sink Elimination Algorithm...");
        boolean result1 = AcyclicityChecker.check(smallAcyclic, true);
        System.out.println("\nRESULT: " + (result1 ? "YES (Graph is ACYCLIC)" : "NO (Graph CONTAINS A CYCLE)"));

        printDivider();

        // Cyclic graph: same but with added edge c->b creating cycle b->c->b
        System.out.println("--- Small CYCLIC graph ---");
        DirectedGraph smallCyclic = new DirectedGraph();
        smallCyclic.addEdge(0, 1);
        smallCyclic.addEdge(0, 2);
        smallCyclic.addEdge(1, 2);
        smallCyclic.addEdge(2, 1); // creates cycle b -> c -> b
        smallCyclic.addEdge(1, 4);
        smallCyclic.addEdge(3, 4);
        smallCyclic.addEdge(4, 5);
        System.out.println("Vertices: 6  |  Edges: 7");
        smallCyclic.printAdjacencyList();

        System.out.println("\nRunning Sink Elimination Algorithm...");
        boolean result2 = AcyclicityChecker.check(smallCyclic, true);

        System.out.println("\nTracing cycle using DFS...");
        CycleFinder finder2 = new CycleFinder(smallCyclic);
        List<Integer> cycle2 = finder2.findCycle();
        System.out.println("CYCLE FOUND: " + CycleFinder.formatCycle(cycle2));
        System.out.println("\nRESULT: " + (result2 ? "YES (Graph is ACYCLIC)" : "NO (Graph CONTAINS A CYCLE)"));

        // PART 2 — Benchmark file demo
        System.out.println("\n========== PART 2: Benchmark file demo ==========\n");

        String acyclicFile = BENCH_ACYCLIC + "a_40_0.txt";
        System.out.println("Reading graph from: " + acyclicFile);
        DirectedGraph benchAcyclic = GraphParser.parse(acyclicFile);

        System.out.println("\nRunning Sink Elimination Algorithm...");
        DirectedGraph benchAcyclicCopy = benchAcyclic.copy();
        boolean benchResult1 = AcyclicityChecker.check(benchAcyclicCopy, true);
        System.out.println("\nRESULT: " + (benchResult1 ? "YES (The graph is ACYCLIC)" : "NO (The graph CONTAINS A CYCLE)"));

        printDivider();

        String cyclicFile = BENCH_CYCLIC + "c_40_0.txt";
        System.out.println("Reading graph from: " + cyclicFile);
        DirectedGraph benchCyclic = GraphParser.parse(cyclicFile);

        System.out.println("\nRunning Sink Elimination Algorithm...");
        DirectedGraph benchCyclicCopy = benchCyclic.copy();
        boolean benchResult2 = AcyclicityChecker.check(benchCyclicCopy, true);

        System.out.println("\nTracing cycle using DFS on remaining vertices...");
        CycleFinder finderBench = new CycleFinder(benchCyclicCopy);
        List<Integer> cycleBench = finderBench.findCycle();
        System.out.println("CYCLE FOUND: " + CycleFinder.formatCycle(cycleBench));
        System.out.println("\nRESULT: " + (benchResult2 ? "YES (The graph is ACYCLIC)" : "NO (The graph CONTAINS A CYCLE)"));

        // PART 3 — Doubling hypothesis benchmarks
        System.out.println("\n========== PART 3: Performance Benchmarks ==========\n");
        System.out.println("Running doubling hypothesis benchmarks...");
        System.out.println("Each size: " + TRIALS + " acyclic + " + TRIALS + " cyclic trials, averaged.\n");
        runBenchmarks();
    }

    private static void runBenchmarks() throws IOException {
        System.out.printf("%-10s  %-14s  %-10s  %-14s  %-10s%n",
                "Size (V)", "Acyclic (ms)", "Ratio A", "Cyclic (ms)", "Ratio C");
        System.out.println("----------------------------------------------------------");

        double prevAvgAcyclic = -1;
        double prevAvgCyclic  = -1;

        for (int size : SIZES) {

            // Parse all acyclic trial graphs upfront (excludes file I/O from timing)
            DirectedGraph[] acyclicGraphs = new DirectedGraph[TRIALS];
            for (int t = 0; t < TRIALS; t++) {
                String path = BENCH_ACYCLIC + "a_" + size + "_" + t + ".txt";
                acyclicGraphs[t] = GraphParser.parseSilent(path);
            }

            // Time only the algorithm
            double totalAcyclic = 0;
            for (int t = 0; t < TRIALS; t++) {
                DirectedGraph g = acyclicGraphs[t].copy();
                long start = System.nanoTime();
                AcyclicityChecker.check(g, false);
                long end = System.nanoTime();
                totalAcyclic += (end - start) / 1_000_000.0;
            }
            double avgAcyclic = totalAcyclic / TRIALS;

            // Parse all cyclic trial graphs upfront
            DirectedGraph[] cyclicGraphs = new DirectedGraph[TRIALS];
            for (int t = 0; t < TRIALS; t++) {
                String path = BENCH_CYCLIC + "c_" + size + "_" + t + ".txt";
                cyclicGraphs[t] = GraphParser.parseSilent(path);
            }

            // Time only the algorithm
            double totalCyclic = 0;
            for (int t = 0; t < TRIALS; t++) {
                DirectedGraph g = cyclicGraphs[t].copy();
                long start = System.nanoTime();
                AcyclicityChecker.check(g, false);
                long end = System.nanoTime();
                totalCyclic += (end - start) / 1_000_000.0;
            }
            double avgCyclic = totalCyclic / TRIALS;

            String ratioA = (prevAvgAcyclic < 0) ? "—"
                    : String.format("%.2f", avgAcyclic / prevAvgAcyclic);
            String ratioC = (prevAvgCyclic  < 0) ? "—"
                    : String.format("%.2f", avgCyclic  / prevAvgCyclic);

            System.out.printf("%-10d  %-14.3f  %-10s  %-14.3f  %-10s%n",
                    size, avgAcyclic, ratioA, avgCyclic, ratioC);

            prevAvgAcyclic = avgAcyclic;
            prevAvgCyclic  = avgCyclic;
        }
    }

    private static void printHeader() {
        System.out.println("================================================");
        System.out.println("  DIRECTED GRAPH ACYCLICITY CHECKER");
        System.out.println("  Student : W.L. Crishal Shanuka");
        System.out.println("  ID      : w2121314");
        System.out.println("  Module  : 5SENG003W Algorithms");
        System.out.println("================================================\n");
    }

    private static void printDivider() {
        System.out.println("\n------------------------------------------------\n");
    }
}