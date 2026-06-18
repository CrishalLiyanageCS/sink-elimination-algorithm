package acyclicity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CycleFinder {

    private DirectedGraph             graph;
    private HashMap<Integer, Boolean> visited;
    private HashMap<Integer, Boolean> onStack;
    private HashMap<Integer, Integer> parent;
    private List<Integer>             cycle;

    public CycleFinder(DirectedGraph graph) {
        this.graph   = graph;
        this.visited = new HashMap<>();
        this.onStack = new HashMap<>();
        this.parent  = new HashMap<>();
        this.cycle   = null;

        for (int v : graph.getVertices()) {
            visited.put(v, false);
            onStack.put(v, false);
            parent.put(v, -1);
        }
    }

    public List<Integer> findCycle() {
        for (int v : graph.getVertices()) {
            if (!visited.get(v)) {
                dfs(v);
                if (cycle != null) return cycle;
            }
        }
        return null;
    }

    private void dfs(int v) {
        visited.put(v, true);
        onStack.put(v, true);

        for (int neighbour : graph.getNeighbours(v)) {

            if (!visited.containsKey(neighbour)) continue;

            if (!visited.get(neighbour)) {
                parent.put(neighbour, v);
                dfs(neighbour);
                if (cycle != null) return;

            } else if (Boolean.TRUE.equals(onStack.get(neighbour))) {
                buildCycle(v, neighbour);
                return;
            }
        }

        onStack.put(v, false);
    }

    private void buildCycle(int v, int neighbour) {
        cycle = new ArrayList<>();

        int current = v;
        cycle.add(current);
        while (current != neighbour) {
            current = parent.get(current);
            if (current == -1) break;
            cycle.add(current);
        }

        for (int i = 0, j = cycle.size() - 1; i < j; i++, j--) {
            int tmp = cycle.get(i);
            cycle.set(i, cycle.get(j));
            cycle.set(j, tmp);
        }

        cycle.add(neighbour);
    }

    public static String formatCycle(List<Integer> cycle) {
        if (cycle == null || cycle.isEmpty()) {
            return "No cycle found.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cycle.size(); i++) {
            sb.append(cycle.get(i));
            if (i < cycle.size() - 1) sb.append(" -> ");
        }
        return sb.toString();
    }
}