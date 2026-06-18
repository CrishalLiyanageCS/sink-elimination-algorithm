package acyclicity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GraphParser {

    public static DirectedGraph parse(String filePath) throws IOException {
        DirectedGraph graph = parseInternal(filePath, true);
        return graph;
    }

    public static DirectedGraph parseSilent(String filePath) throws IOException {
        return parseInternal(filePath, false);
    }

    private static DirectedGraph parseInternal(String filePath, boolean verbose)
            throws IOException {

        DirectedGraph graph = new DirectedGraph();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        int edgeCount = 0;

        String firstLine = reader.readLine();
        if (firstLine == null) {
            reader.close();
            return graph;
        }

        firstLine = firstLine.trim();
        String[] firstParts = firstLine.split("\\s+");

        if (firstParts.length == 1) {
            // Benchmark format: first line is the vertex count
            int vertexCount = Integer.parseInt(firstParts[0]);
            for (int i = 0; i < vertexCount; i++) {
                graph.addVertex(i);
            }
        } else if (firstParts.length >= 2) {
            // Simple format: first line is already an edge
            int u = Integer.parseInt(firstParts[0]);
            int v = Integer.parseInt(firstParts[1]);
            graph.addEdge(u, v);
            edgeCount++;
        }

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split("\\s+");
            if (parts.length >= 2) {
                int u = Integer.parseInt(parts[0]);
                int v = Integer.parseInt(parts[1]);
                graph.addEdge(u, v);
                edgeCount++;
            }
        }

        reader.close();

        if (verbose) {
            System.out.println("Parsed " + graph.vertexCount()
                    + " vertices, " + edgeCount + " edges successfully.");
        }

        return graph;
    }
}