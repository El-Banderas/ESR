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
    public Map<InfoNodo, List<Connection>> activeNetwork;


    /*----BEST PATHS-------------*/
    public Map<InfoNodo, List<Connection>> bestPaths;



    public Typology(){
        this.networkString = new HashMap<>();
        this.nodes= new HashMap<>();
        this.completeNetwork = new HashMap<>();
        this.activeNetwork = new HashMap<>();
        this.bestPaths = new HashMap<>();
    }

    public Typology(Map<String, List<String>> networkString, Map<String, InfoNodo> nodes, Map<InfoNodo, List<InfoNodo>> completeNetwork, Map<InfoNodo, List<Connection>> activeNetwork,  Map<InfoNodo, List<Connection>> bestPaths) {
        this.networkString = networkString;
        this.nodes = nodes;
        this.completeNetwork = completeNetwork;
        this.activeNetwork = activeNetwork;
        this.bestPaths = bestPaths;
    }

    public Map<String, List<String>> getNetworkString() {
        return networkString;
    }

    public void setNetworkString(Map<String, List<String>> networkString) {
        this.networkString = networkString;
    }

    public Map<String, InfoNodo> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, InfoNodo> nodes) {
        this.nodes = nodes;
    }

    public Map<InfoNodo, List<InfoNodo>> getCompleteNetwork() {
        return completeNetwork;
    }

    public void setCompleteNetwork(Map<InfoNodo, List<InfoNodo>> completeNetwork) {
        this.completeNetwork = completeNetwork;
    }

    public Map<InfoNodo, List<Connection>> getActiveNetwork() {
        return activeNetwork;
    }

    public void setActiveNetwork(Map<InfoNodo, List<Connection>> activeNetwork) {
        this.activeNetwork = activeNetwork;
    }

    public Map<InfoNodo, List<Connection>> getBestPaths() {
        return bestPaths;
    }

    public void setBestPaths(Map<InfoNodo, List<Connection>> bestPaths) {
        this.bestPaths = bestPaths;
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

            String[] neighbours = parts[1].split(" *, *");

            List<String> nodosaux = new ArrayList<>();
            for(String v : neighbours){
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
        activateConnection(this.nodes.get("s1"), new Connection(this.nodes.get("s1"),this.nodes.get("s1") , 0.0,  0), true);
    }


    /*
        Method to add connection to node which had already active connections
     */
    public void addNeighbour(InfoNodo nodeToActivate, Connection newConnection){
        List<Connection> nodeConnections = activeNetwork.get(nodeToActivate);
        nodeConnections.add(newConnection);
        activeNetwork.put(nodeToActivate,nodeConnections);
    }


    /*
        Method for node activation (Populates the active Network Map)
     */
    public void activateConnection(InfoNodo nodeToActivate, Connection newConnection, boolean isServer) throws InterruptedException {
        /*
            To make it easier the server is neighbour of itself
         */

        List<InfoConnection> activeNeighbours= new ArrayList<>();

        List<InfoNodo> allNeighbours = this.completeNetwork.get(nodeToActivate.idNodo);

        //If the active network already has neighbours active, add to list / else create the new list
        if(this.activeNetwork.get(nodeToActivate) == null){
            List<Connection> neighbours = new ArrayList<>();
            if(!isServer){ //only adding the new connection when it is not a server
                neighbours.add(newConnection);
            }
            this.activeNetwork.put(nodeToActivate, neighbours);
        }else{
            addNeighbour(nodeToActivate,newConnection);
        }

        // probably will set a small sleep here (for now: 100 ms)
        Thread.sleep(100);
        recalculateBestPathsTree();

    }

    /*
        Method to recalculate the best Paths Tree and also populates it
        Uses the Prim's algorithm
     */
    public void recalculateBestPathsTree(){

        // to store the minimum spanning tree (best Paths Trees)
        Map<InfoNodo,List<Connection>> mst = new HashMap<>();

        // to store the vertices already visited
        List<InfoNodo> visited = new ArrayList<>();

        // priority queue to order edges by
        PriorityQueue<Connection> priorityQueue = new PriorityQueue<>();

        // starting at the server
        visited.add(nodes.get("s1"));

        for (Connection connection : activeNetwork.get(nodes.get("s1"))){
            priorityQueue.add(connection);
        }

        while (!priorityQueue.isEmpty()){

            // get the edge with the smallest weight
            Connection toAdd = priorityQueue.poll();



            // Skip if the destination(otherNode) has already been visited
            if(visited.contains(toAdd.to)){
                continue;
            }

            // Add the edge to the minimum spanning tree
            List<Connection> listCon;
            if(mst.get(toAdd.from) != null){
                listCon = mst.get(toAdd.from);

            }else{
                listCon = new ArrayList<>();
            }
            listCon.add(toAdd);
            mst.put(toAdd.from, listCon);

            // Add the destination to the visited set
            visited.add(toAdd.to);



            //Add all edges incident to the destination to the priority queue
            for(Connection nextConnection : getIncident(toAdd.to)){
                priorityQueue.add(nextConnection);
            }


        }


        this.bestPaths = mst;



    }

    public List<InfoNodo> getVizinhos(InfoNodo i){

        return null;
    }


    public List<Connection> getIncident(InfoNodo node){

        List<Connection> incident = new ArrayList<>();

        for (Map.Entry<InfoNodo, List<Connection>> entry : activeNetwork.entrySet()) {

            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            if (InfoNodo.compareInfoNodes(key,node)){
                for(Connection connection : value){

                    incident.add(connection);

                }
            }

        }

        return incident;



    }


    public InfoNodo getFather(InfoNodo son){

        InfoNodo father = null;

        for (Map.Entry<InfoNodo, List<Connection>> entry : bestPaths.entrySet()) {
            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            for(Connection con : value){
                if(con.to.equals(son)){
                    father = con.from;
                }
            }
        }

        return father;
    }


    public List<InfoNodo> getNeighbours(InfoNodo node){
        List<InfoNodo> neighbours = new ArrayList<>();

        for (Map.Entry<InfoNodo, List<Connection>> entry : activeNetwork.entrySet()) {
            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            // First checks whenever the node is key
            if(key.equals(node)){
                for(Connection con : value){
                    if(!neighbours.contains(con.to)){
                        neighbours.add(con.to);
                    }
                }
            }else{
                for(Connection con : value){
                    if(con.to.equals(node)){
                        if(!neighbours.contains(key)){
                            neighbours.add(key);
                        }
                    }
                }
            }


        }

        return neighbours;

    }




    public static void main(String[] args) throws IOException, InterruptedException {
        Typology typologyTest = new Typology();
        typologyTest.parse("C:/Users/migue/Desktop/ESR/src/otherServer/biggerConfiguration.txt");
        typologyTest.setCompleteNetwork();

        // Print the complete network
        System.out.println("Complete Network");
        Map<InfoNodo,List<InfoNodo>> completeLayoutTest = typologyTest.getCompleteNetwork();
        for (Map.Entry<InfoNodo, List<InfoNodo>> entry : completeLayoutTest.entrySet()) {
            InfoNodo key = entry.getKey();
            List<InfoNodo> value = entry.getValue();

            System.out.println(">>>" + key.toStringCon());

            for(InfoNodo node : value){
                System.out.println(node.toStringCon());
            }
        }
        System.out.println("\n\n");

        /*
            The following code is not yet a formal test: the delay values were hand crafted
         */

        // Activate some nodes
        typologyTest.activateConnection(typologyTest.getNodes().get("s1"),new Connection(typologyTest.getNodes().get("s1") ,typologyTest.getNodes().get("n1"),2,1), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n1"),new Connection(typologyTest.getNodes().get("n2") ,typologyTest.getNodes().get("n2"),11,2), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n1"),new Connection(typologyTest.getNodes().get("n1") ,typologyTest.getNodes().get("n3"),1,2), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n2"),new Connection(typologyTest.getNodes().get("n2") ,typologyTest.getNodes().get("c2"),2,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n2"),new Connection(typologyTest.getNodes().get("n2") ,typologyTest.getNodes().get("n3"),3,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n3"),new Connection(typologyTest.getNodes().get("n3") ,typologyTest.getNodes().get("n2"),1,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n3"),new Connection(typologyTest.getNodes().get("n3") ,typologyTest.getNodes().get("n4"),2,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n3"),new Connection(typologyTest.getNodes().get("n3") ,typologyTest.getNodes().get("n5"),5,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n4"),new Connection(typologyTest.getNodes().get("n4") ,typologyTest.getNodes().get("n5"),2,4), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n5"),new Connection(typologyTest.getNodes().get("n5") ,typologyTest.getNodes().get("n2"),2,4), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n5"),new Connection(typologyTest.getNodes().get("n5") ,typologyTest.getNodes().get("c1"),2,4), false );



        // Print the active nodes
        System.out.println("Active Network");
        Map<InfoNodo,List<Connection>> activeLayoutTest = typologyTest.getActiveNetwork();
        printInfoFromMap(activeLayoutTest);

        // Print the best paths tree (it was setted during the activation of the nodes)
        System.out.println("\n\n");
        System.out.println("Best Paths Tree");
        Map<InfoNodo,List <Connection>> bestPaths = typologyTest.getBestPaths();
        printInfoFromMap(bestPaths);

        // Test getFather
        // When invoking the function, checking for null elements needs to be done
        System.out.println("\n\n");
        System.out.println("Test getFather");
        if(typologyTest.getFather(typologyTest.getNodes().get("n2")) != null) {
            System.out.println(typologyTest.getFather(typologyTest.getNodes().get("n2")).toString());
        }
        if(typologyTest.getFather(typologyTest.getNodes().get("n7")) != null) {
            System.out.println(typologyTest.getFather(typologyTest.getNodes().get("n7")).toString());
        }

        // Test getNeighbours
        // When invoking the function, checking for empty list
        System.out.println("\n\n");
        System.out.println("Test getNeighbours");
        List<InfoNodo> neighboursN2 = typologyTest.getNeighbours(typologyTest.getNodes().get("n2"));
        for (InfoNodo neighbour : neighboursN2){
            System.out.println(neighbour.toStringCon());
        }


    }

    public static void printInfoFromMap(Map<InfoNodo, List<Connection>> bestPaths) {
        for (Map.Entry<InfoNodo, List<Connection>> entry : bestPaths.entrySet()) {
            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            System.out.println(">>>" + key.toString());

            for(Connection connection : value){
                System.out.println(connection.toString());
            }
        }
    }

}



