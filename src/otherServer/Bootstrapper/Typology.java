package otherServer.Bootstrapper;

import Common.InfoNodo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class Typology {

    /*-----COMPLETE TIPOLOGY------*/

    //Stores the complete tipology (after reading :
    public Map<String, List<String>> networkString;
    //Stores the list of all the nodes present in the tipology (could be used in some specific case)
    public Map<String,InfoNodo> nodes;
    //Complete network with desired format
    public Map<InfoNodo, List<InfoNodo>> completeNetwork;


    /*-----ACTIVE NODES----------*/
    public Map<InfoNodo, List<InfoConnection>> activeNetwork;


    /*----BEST PATHS-------------*/
    public  List<InfoConnection> bestPaths;



    public Typology(){
        this.networkString = new HashMap<>();
        this.nodes= new HashMap<>();
        this.completeNetwork = new HashMap<>();
        this.activeNetwork = new HashMap<>();
        this.bestPaths = new ArrayList<>();
    }

    public Typology(Map<String, List<String>> networkString, Map<String, InfoNodo> nodes, Map<InfoNodo, List<InfoNodo>> completeNetwork, Map<InfoNodo, List<InfoConnection>> activeNetwork,  List<InfoConnection> bestPaths) {
        this.networkString = networkString;
        this.nodes = nodes;
        this.completeNetwork = completeNetwork;
        this.activeNetwork = activeNetwork;
        this.bestPaths = bestPaths;
    }

    public Typology(Map<String, List<String>> networkString, Map<String,InfoNodo> nodes) {
        this.networkString = networkString;
        this.nodes = nodes;
    }

    public Map<String, List<String>> getNetworkString() {
        return networkString;
    }

    public Map<String,InfoNodo> getNodes() {
        return nodes;
    }

    public Map<InfoNodo, List<InfoNodo>> getCompleteNetwork() {
        return completeNetwork;
    }

    public Map<InfoNodo, List<InfoConnection>> getActiveNetwork() {
        return activeNetwork;
    }

    public List<InfoConnection> getBestPaths() {
        return bestPaths;
    }

    public void parse(String configurationFile) throws IOException {


        // this method of try with resorces automatically closes buffered reader
        try (BufferedReader reader = new BufferedReader(new FileReader(configurationFile))) {

            String line = reader.readLine();

            while (line != null) {

                String[] parts = line.split(" *; *");
               

                // loop through all neighbours of a node
            String[] aux = parts[0].split(" *: *");
            InetAddress ip = InetAddress.getByName(aux[1]);
            InfoNodo n = new InfoNodo(aux[0],ip, 8000);
            this.nodes.put(aux[0],n);

            String[] vizinhos = parts[1].split(" *, *");

            List<String> nodosaux = new ArrayList<>();
            for(String v : vizinhos){
                nodosaux.add(v);
            }


             this.networkString.put(aux[0],nodosaux);

                // read next line
                line = reader.readLine();
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        This method not only initializes the entire network, but also
        assumes the server is active for the active network
     */

    public void setCompleteNetwork() throws InterruptedException {
        Map<InfoNodo,List<InfoNodo>> completeNetwork =  new HashMap<>();

        Iterator it = this.networkString.entrySet().iterator();

        for (Map.Entry<String, List<String>> entry : networkString.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();

            List<InfoNodo> neighbours = new ArrayList<>();
            for (String node : value){
                neighbours.add(this.nodes.get(node));
            }

            completeNetwork.put(this.nodes.get(key),neighbours);

        }
        this.completeNetwork = completeNetwork;

        // Server is initialized connecting with himself (but the method will create an empty list if it is server)
        activateConnection(this.nodes.get("s1"), new InfoConnection(this.nodes.get("s1"),0.0,  0.0, false), true);
    }


    /*
        Method to add connection to node which had already active connections
     */
    public void addNeighbour(InfoNodo nodeToActivate, InfoConnection newConnection){
        List<InfoConnection> nodeConnections = activeNetwork.get(nodeToActivate);
        nodeConnections.add(newConnection);
        activeNetwork.put(nodeToActivate,nodeConnections);
    }


    /*
        Method for node activation (Populates the active Network Map)
     */
    public void activateConnection(InfoNodo nodeToActivate, InfoConnection newConnection, boolean isServer) throws InterruptedException {
        /*
            To make it easier the server is neighbour of itself
         */

        List<InfoConnection> activeNeighbours= new ArrayList<>();

        List<InfoNodo> allNeighbours = this.completeNetwork.get(nodeToActivate.idNodo);

        //If the active network already has neighbours active, add to list / else create the new list
        if(this.activeNetwork.get(nodeToActivate) == null){
            List<InfoConnection> neighbours = new ArrayList<>();
            if(!isServer){ //only adding the new connection when it is not a server
                neighbours.add(newConnection);
            }
            this.activeNetwork.put(nodeToActivate, neighbours);
        }else{
            addNeighbour(nodeToActivate,newConnection);
        }

        // probably will set a small sleep here (for now: 100 ms)
        Thread.sleep(100);
        //recalculateBestPathsTree(activeNetwork);

    }

    /*
        Method to recalculate the best Paths Tree and also populates it
        Uses the Prim's algorithm
     */
    public void recalculateBestPathsTree(Map<InfoNodo,List<InfoConnection>> completeNetwork){

        // to store the minimum spanning tree (best Paths Trees)
        List<InfoConnection> mst = new ArrayList<>();

        // to store the vertices already visited
        Set<InfoNodo> visited = new HashSet<>();

        // priority queue to order edges by
        PriorityQueue<InfoConnection> priorityQueue = new PriorityQueue<>();

        // starting at the server
        visited.add(nodes.get("s1"));

        for (InfoConnection connection : completeNetwork.get(nodes.get("s1"))){
            priorityQueue.add(connection);
        }

        while (!priorityQueue.isEmpty()){
            // get the edge with the smallest weight
            //InfoConnection connection = priorityQueue.poll();

            // Skip if the destination(otherNode) has already been visited
            if(visited.contains(priorityQueue.poll().otherNode)){
                continue;
            }

            // Add the edge to the minimum spanning tree
            mst.add(priorityQueue.poll());

            // Add the destination to the visited set
            visited.add(priorityQueue.poll().otherNode);



            //Add all edges incident to the destination to the priority queue
            for(InfoConnection nextConnection : activeNetwork.get(priorityQueue.poll().otherNode)){
                priorityQueue.add(nextConnection);
            }


        }


        this.bestPaths = mst;



    }

    public List<InfoNodo> getVizinhos(InfoNodo i){

        return null;
    }



    public static void main(String[] args) throws IOException, InterruptedException {
        Typology typologyTest = new Typology();
        typologyTest.parse("C:/Users/migue/Desktop/ESR/src/otherServer/topCenario2.txt");
        typologyTest.setCompleteNetwork();

        // Print the complete network
        System.out.println("Complete Network");
        Map<InfoNodo,List<InfoNodo>> completeLayoutTest = typologyTest.getCompleteNetwork();
        for (Map.Entry<InfoNodo, List<InfoNodo>> entry : completeLayoutTest.entrySet()) {
            InfoNodo key = entry.getKey();
            List<InfoNodo> value = entry.getValue();

            System.out.println(">>>" + key.toString());

            for(InfoNodo node : value){
                System.out.println(node.toString());
            }
        }
        System.out.println("\n\n");

        /*
            The following code is not yet a formal test: the delay values were hand crafted
         */

        // Activate some nodes
        typologyTest.activateConnection(typologyTest.getNodes().get("s1"),new InfoConnection(typologyTest.getNodes().get("n1"),2,2,false), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n1"),new InfoConnection(typologyTest.getNodes().get("n2"),1,2,false), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n1"),new InfoConnection(typologyTest.getNodes().get("n3"),5,2,false), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n2"),new InfoConnection(typologyTest.getNodes().get("c2"),3,2,false), false );


        // Print the active nodes
        System.out.println("Activate Network");
        Map<InfoNodo,List<InfoConnection>> activeLayoutTest = typologyTest.getActiveNetwork();
        for (Map.Entry<InfoNodo, List<InfoConnection>> entry : activeLayoutTest.entrySet()) {
            InfoNodo key = entry.getKey();
            List<InfoConnection> value = entry.getValue();

            System.out.println(">>>" + key.toString());

            for(InfoConnection connection : value){
                System.out.println(connection.toString());
            }
        }

        System.out.println("\n\n");
        typologyTest.recalculateBestPathsTree(typologyTest.activeNetwork);
        // Print the best path
        List <InfoConnection> bestPaths = typologyTest.getBestPaths();
        for(InfoConnection con : bestPaths){
            System.out.println(con.toString());
        }



    }

}



