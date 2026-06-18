package acyclicity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class DirectedGraph {

    private HashMap<Integer, LinkedList<Integer>> adj;

    public DirectedGraph() {
        adj = new HashMap<>();
    }

    public void addVertex(int v) {
        if (!adj.containsKey(v)) {
            adj.put(v, new LinkedList<>());
        }
    }

    public void addEdge(int u, int v) {
        addVertex(u);
        addVertex(v);
        adj.get(u).add(v);
    }

    public int findSink() {
        for (int v : adj.keySet()) {
            if (adj.get(v).isEmpty()) {
                return v;
            }
        }
        return -1;
    }

    public void removeVertex(int v) {
        adj.remove(v);
        for (int u : adj.keySet()) {
            adj.get(u).remove(Integer.valueOf(v));
        }
    }

    public boolean isEmpty() {
        return adj.isEmpty();
    }

    public Set<Integer> getVertices() {
        return adj.keySet();
    }

    public LinkedList<Integer> getNeighbours(int v) {
        if (adj.containsKey(v)) {
            return adj.get(v);
        }
        return new LinkedList<>();
    }

    public int vertexCount() {
        return adj.size();
    }

    public void printAdjacencyList() {
        System.out.println("Adjacency list (HashMap<Integer, LinkedList<Integer>>):");
        java.util.List<Integer> sorted = new java.util.ArrayList<>(adj.keySet());
        java.util.Collections.sort(sorted);
        for (int v : sorted) {
            String suffix = adj.get(v).isEmpty() ? "  <- sink" : "";
            System.out.println("  " + v + " -> " + adj.get(v) + suffix);
        }
    }

    public DirectedGraph copy() {
        DirectedGraph clone = new DirectedGraph();
        for (Map.Entry<Integer, LinkedList<Integer>> entry : adj.entrySet()) {
            clone.adj.put(entry.getKey(), new LinkedList<>(entry.getValue()));
        }
        return clone;
    }
}