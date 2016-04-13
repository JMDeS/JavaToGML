package hw6;

/**
 * Created by JMDeS on 4/13/2016.
 */
public class Graph {
    private int edgeCount = 0;
    private boolean adj[][];    // the adjacency matrix

    public Graph(int n) { // pass total number of nodes (actors) ** n = 3411 **
        adj = new boolean[n][n];

        // initially no nodes are adjacent to each other
        for (int i = 0 ; i < n ; i++)
            for(int j = 0 ; j < n ; j++)
                adj[i][j] = false;
    } // end interfaces.Graph constructor

    public int edgeCounter(){
        return edgeCount/2;
    }

    public void setEdge(int node1, int node2) {
        if (!adj[node1][node2]) edgeCount++;
        // add an edge from node1 to node2
        adj[node1][node2] = true;

    } // end join

    public void removeEdge(int node1, int node2) {
        // delete edge from node1 to node2 if one exists
        adj[node1][node2] = false;
    } // end remove

    boolean adjacent(int node1, int node2) {
        return ((adj[node1][node2] == true) ? true : false);
    } // end adjacent

}
