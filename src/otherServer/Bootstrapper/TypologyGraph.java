package otherServer.Bootstrapper;

// Prim's Algorithm in Java

import java.util.*;

public class TypologyGraph {
    // the total graph is represented by a HashTable
    // read integers as nInt and the list has a value representing some sort of distance
    // key is the node/server(0) ; value is list with distances to each index
    Hashtable <Integer, Integer[]> graphConnections;
    // key is origin; value : [destination, distance]
    List<Hashtable<Integer,Integer[]>> prunedConnections;

    public Hashtable<Integer, Integer[]> getGraphConnections() {
        return graphConnections;
    }

    public void setGraphConnections(Hashtable<Integer, Integer[]> graphConnections) {
        this.graphConnections = graphConnections;
    }

    public List<Hashtable<Integer, Integer[]>> getPrunedConnections() {
        return prunedConnections;
    }

    public void setPrunedConnections(List<Hashtable<Integer, Integer[]>> prunedConnections) {
        this.prunedConnections = prunedConnections;
    }

    public TypologyGraph(Hashtable<Integer, Integer[]> graphConnections, List<Hashtable<Integer, Integer[]>> prunedConnections) {
        this.graphConnections = graphConnections;
        this.prunedConnections = prunedConnections;
    }

    public TypologyGraph() {
    }

    @Override
    public String toString() {
        return "TypologyGraph{" +
                "graphConnections=" + graphConnections +
                ", prunedConnections=" + prunedConnections +
                '}';
    }

    public void Prim() {

        List<Hashtable<Integer,Integer[]>> prunedConnections = new ArrayList<>();

        int INF = 9999999;

        int no_edge; // number of edge

        // create a array to track selected vertex
        // selected will become true otherwise false
        boolean[] selected = new boolean[this.graphConnections.size()];

        // set selected false initially
        Arrays.fill(selected, false);

        // set number of edge to 0
        no_edge = 0;

        // the number of egde in minimum spanning tree will be
        // always less than (V -1), where V is number of vertices in
        // graph

        // choose 0th vertex and make it true
        selected[0] = true;

        // print for edge and weight
        System.out.println("Edge : Weight");

        while (no_edge < this.graphConnections.size() - 1) {
            // For every vertex in the set S, find the all adjacent vertices
            // , calculate the distance from the vertex selected at step 1.
            // if the vertex is already in the set S, discard it otherwise
            // choose another vertex nearest to selected vertex at step 1.

            int min = INF;
            int x = 0; // row number
            int y = 0; // col number

            for (int i = 0; i < this.graphConnections.size(); i++) {
                if (selected[i]) {
                    for (int j = 0; j < this.graphConnections.size(); j++) {
                        // not in selected and there is an edge
                        if (!selected[j] && this.graphConnections.get(i)[j] != 0) {
                            if (min > this.graphConnections.get(i)[j]){
                                min = this.graphConnections.get(i)[j];
                                x = i;
                                y = j;
                            }
                        }
                    }
                }
            }
            System.out.println(x + " - " + y + " :  " + this.graphConnections.get(x)[y]);
            Integer[] destDist = {y, this.graphConnections.get(x)[y]};
            Hashtable<Integer,Integer[]> info = new Hashtable<>();
            info.put(x, new Integer[]{y, this.graphConnections.get(x)[y]});
            prunedConnections.add(info);
            selected[y] = true;
            no_edge++;
        }

        this.setPrunedConnections(prunedConnections);
    }

    public static void main(String[] args) {
        TypologyGraph graph = new TypologyGraph();

        //inicialization
        Hashtable <Integer, Integer[]> graphConnections = new Hashtable<>();

        Integer[] serverConnects = {0,0,0,0,0,1,3};
        graphConnections.put(0,serverConnects);

        Integer[] n1 = {0,0,0,1,0,2,0};
        graphConnections.put(1,n1);

        Integer[] n2 = {0,0,0,0,2,5,1};
        graphConnections.put(2,n1);

        Integer[] n3 = {0,1,0,0,0,0,2};
        graphConnections.put(3,n3);

        Integer[] n4 = {0,0,2,0,0,0,3};
        graphConnections.put(4,n4);

        Integer[] n5 = {1,2,5,0,0,0,0};
        graphConnections.put(5,n5);

        Integer[] n6 = {3,0,1,2,3,0,0};
        graphConnections.put(6,n6);

        graph.setGraphConnections(graphConnections);

        System.out.println(graph);

        /*
        // number of vertices in grapj
        int V = 5;

        // create a 2d array of size 5x5
        // for adjacency matrix to represent graph
        int[][] G = { { 0, 9, 75, 0, 0 }, { 9, 0, 95, 19, 42 }, { 75, 95, 0, 51, 66 }, { 0, 19, 51, 0, 31 },
                { 0, 42, 66, 31, 0 } };

        */
        graph.Prim();



        //verify prunedConnection
        System.out.println("\n\n");
        for (Hashtable<Integer,Integer[]> node : graph.prunedConnections){
            Set<Integer> setOfKeys = node.keySet();

            // Iterating through the Hashtable
            // object using for-Each loop
            for (Integer key : setOfKeys) {
                // Print and display the Rank and Name
                System.out.println(key + " - " +
                        + node.get(key)[0] + " : " + node.get(key)[1]);
            }
        }
    }
}
