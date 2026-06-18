package acyclicity;

import java.util.ArrayList;
import java.util.List;

public class AcyclicityChecker {

    public static boolean check(DirectedGraph graph, boolean verbose) {
        int step = 1;

        while (!graph.isEmpty()) {
            int sink = graph.findSink();

            if (sink == -1) {
                if (verbose) {
                    System.out.println("  Step " + step
                            + ": No sink found! Stopping early.");
                    List<Integer> remaining = new ArrayList<>(graph.getVertices());
                    System.out.println("  Remaining vertices (contain cycle): " + remaining);
                }
                return false;
            }

            if (verbose) {
                System.out.printf("  Step %3d: Sink found -> vertex %-5d | Vertices remaining: %d%n",
                        step, sink, graph.vertexCount() - 1);
            }

            graph.removeVertex(sink);
            step++;
        }

        if (verbose) {
            System.out.println("  All " + (step - 1)
                    + " vertices successfully eliminated.");
        }
        return true;
    }
}