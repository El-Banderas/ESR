package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;
import TransmitData.SendData;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Typology {

    /*-----COMPLETE TIPOLOGY------*/

    //Stores the complete tipology (after reading :
    public Map<String, List<String>> networkString;
    //Stores the list of all the nodes present in the tipology (could be used in some specific case)
    public Map<String, InfoNodo> nodes;
    //Complete network with desired format
    public Map<InfoNodo, List<InfoNodo>> completeNetwork;


    /*-----ACTIVE NODES----------*/
    public Map<InfoNodo, List<Connection>> activeNetwork;


    /*----BEST PATHS-------------*/
    public Map<InfoNodo, List<Connection>> bestPaths;


    public Typology() {
        this.networkString = new HashMap<>();
        this.nodes = new HashMap<>();
        this.completeNetwork = new HashMap<>();
        this.activeNetwork = new HashMap<>();
        this.bestPaths = new HashMap<>();
    }

    public Typology(Map<String, List<String>> networkString, Map<String, InfoNodo> nodes, Map<InfoNodo, List<InfoNodo>> completeNetwork, Map<InfoNodo, List<Connection>> activeNetwork, Map<InfoNodo, List<Connection>> bestPaths) {
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


    // check for valid IP
    // separate servers, nodes & clients
    public void parse(String configurationFile) throws IOException {


        // this method of try with resorces automatically closes buffered reader
        try (BufferedReader reader = new BufferedReader(new FileReader(configurationFile))) {

            String line = reader.readLine();

            int port = 8000;

            while (line != null) {



                String[] parts = line.split(" *; *");


                // loop through all neighbours of a node
                String[] aux = parts[0].split(" *: *");
                InetAddress ip = InetAddress.getByName(aux[1]);
                if (Constants.Windows==true) {

                    InfoNodo n = new InfoNodo(aux[0], ip, port);
                    port = port + 10;

                    this.nodes.put(aux[0], n);
                } else {
                    InfoNodo n = new InfoNodo(aux[0], ip, Constants.port);
                    this.nodes.put(aux[0], n);
                }


                String[] neighbours = parts[1].split(" *, *");

                List<String> nodosaux = new ArrayList<>();
                for (String v : neighbours) {
                    nodosaux.add(v);
                }


                this.networkString.put(aux[0], nodosaux);

                // read next line
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
        This method not only initializes the entire network, but also
        assumes the server is active for the active network
     */

    public void setCompleteNetwork() throws InterruptedException {
        Map<InfoNodo, List<InfoNodo>> completeNetwork = new HashMap<>();

        Iterator it = this.networkString.entrySet().iterator();

        for (Map.Entry<String, List<String>> entry : networkString.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();

            List<InfoNodo> neighbours = new ArrayList<>();
            for (String node : value) {
                neighbours.add(this.nodes.get(node));
            }

            completeNetwork.put(this.nodes.get(key), neighbours);

        }
        this.completeNetwork = completeNetwork;

        // Server is initialized connecting with himself (but the method will create an empty list if it is server)
        activateConnection(this.nodes.get("s1"), true);
    }

    private List<Connection> getNeibourghs(InfoNodo target){
        for (Map.Entry<InfoNodo, List<Connection>> x : activeNetwork.entrySet()){
            if (x.getKey().portNet == target.portNet && target.ip.equals(x.getKey().ip)){
                return x.getValue();
            }
        }
        System.out.println("NÂO devia aperecer");
        return new ArrayList<>();
    }

    private List<Connection> getAndRemoveNeibourghs(InfoNodo target){
        for (Map.Entry<InfoNodo, List<Connection>> entry : activeNetwork.entrySet()){
            if (entry.getKey().portNet == target.portNet && target.ip.equals(entry.getKey().ip)){
                List<Connection> res = entry.getValue();
                activeNetwork.remove(entry.getKey());
                return res;
            }
        }
        System.out.println("NÂO devia aperecer");
        return new ArrayList<>();
    }

    /*
        Method to add connection to node which had already active connections
     */
    public void addNeighbour(InfoNodo nodeToActivate, Connection newConnection) {
        List<Connection> nodeConnections = getAndRemoveNeibourghs(nodeToActivate);
        nodeConnections.add(newConnection);
        activeNetwork.put(nodeToActivate, nodeConnections);
        //System.out.println("Depois de adicionar vizinho");
        //Typology.printInfoFromMap(activeNetwork);
    }

    public void addConection(InfoNodo from, InfoNodo to, double delay, int numHops, DatagramSocket socket, InfoNodo destMSG) {
        System.out.println("Add connection");
        System.out.println(from);
        System.out.println(to);
        addNeighbour(from, new Connection(from, to, delay, numHops));
        addNeighbour(to, new Connection(to, from, delay, numHops));

        recalculateBestPathsTree(socket, destMSG);

    }


    /*
        Method for node activation (Populates the active Network Map)
     */
    public void activateConnection(InfoNodo node, boolean isServer) throws InterruptedException {
        /*
            To make it easier the server is neighbour of itself
         */

        //List<InfoConnection> activeNeighbours= new ArrayList<>();

        //List<InfoNodo> allNeighbours = this.completeNetwork.get(node.idNodo);


        this.activeNetwork.put(node, new ArrayList<>());


        /*
        //If the active network already has neighbours active, add to list / else create the new list
        if(this.activeNetwork.get(node) == null){
            List<Connection> neighbours = new ArrayList<>();
            if(!isServer){ //only adding the new connection when it is not a server
                neighbours.add(newConnection);
            }
            this.activeNetwork.put(node, neighbours);
        }else{
            addNeighbour(fromToActivate,newConnection);
        }

        // probably will set a small sleep here (for now: 100 ms)
        //Thread.sleep(100);
        recalculateBestPathsTree();*/

    }


    /*
        Method to recalculate the best Paths Tree and also populates it
        Uses the Prim's algorithm
     */

    public List<Connection> getConnectedToServer(InfoNodo server){
        List<Connection> connectedToServer = null;

        for(InfoNodo node : activeNetwork.keySet().stream().collect(Collectors.toList())){
            if (node.portNet == server.portNet && node.getIp().equals(server.getIp())){
                connectedToServer = activeNetwork.get(node);
            }
        }

        return connectedToServer;
    }


    public void recalculateBestPathsTree(DatagramSocket socket, InfoNodo destMSG) {

        // to store the minimum spanning tree (best Paths Trees)
        Map<InfoNodo, List<Connection>> mst = new HashMap<>();

        // to store the vertices already visited
        List<InfoNodo> visited = new ArrayList<>();

        // priority queue to order edges by
        PriorityQueue<Connection> priorityQueue = new PriorityQueue<>();

        // starting at the server
        visited.add(nodes.get("s1"));

        List<Connection> connectedToServer = getConnectedToServer(nodes.get("s1"));
        for (Connection connection : connectedToServer) {
            priorityQueue.add(connection);
        }

        while (!priorityQueue.isEmpty()) {

            // get the edge with the smallest weight
            Connection toAdd = priorityQueue.poll();


            // Skip if the destination(otherNode) has already been visited
            if (visited.contains(toAdd.to)) {
                continue;
            }

            // Add the edge to the minimum spanning tree
            List<Connection> listCon ;
            listCon = getConnections(mst, toAdd.from);
            if (listCon == null) {
                listCon = new ArrayList<>();
            }
            listCon.add(toAdd);
            mst.put(toAdd.from, listCon);

            // Add the destination to the visited set
            visited.add(toAdd.to);


            //Add all edges incident to the destination to the priority queue
            for (Connection nextConnection : getIncident(toAdd.to)) {
                priorityQueue.add(nextConnection);
            }


        }

        XMLParser temp = new XMLParser();
        String xml = temp.generateXML(this.nodes, mst);
        String res = temp.prettyPrintByTransformer(xml, 1, false);
        System.out.println("àrvore");
        System.out.println(res);
        this.bestPaths = mst;

        try {
            SendData.sendXML(socket, destMSG, xml);
        } catch (IOException e) {
            System.out.println("Error sending XML");
            throw new RuntimeException(e);
        }
    }



    public List<Connection> getConnections(Map<InfoNodo,List<Connection>> mst, InfoNodo toGet){
        List<Connection> con = null;
        for(InfoNodo n : mst.keySet().stream().collect(Collectors.toList())){
            if (n.getIp().equals(toGet.getIp()) && n.portNet==toGet.portNet){
                con = mst.get(n);
            }
        }

        return con;

    }


    public List<Connection> getIncident(InfoNodo node) {

        List<Connection> incident = new ArrayList<>();

        for (Map.Entry<InfoNodo, List<Connection>> entry : activeNetwork.entrySet()) {

            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            //if (InfoNodo.compareInfoNodes(key, node)) {
            if ((key.ip.equals(node.ip)) && key.portNet == node.portNet){
                for (Connection connection : value) {

                    incident.add(connection);

                }
            }

        }

        return incident;


    }


    public InfoNodo getFather(InfoNodo son) {

        InfoNodo father = null;

        for (Map.Entry<InfoNodo, List<Connection>> entry : bestPaths.entrySet()) {
            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            for (Connection con : value) {
                if (con.to.equals(son)) {
                    father = con.from;
                }
            }
        }

        return father;
    }


    public List<InfoNodo> getAllNeighbours(InfoNodo i){
        List<InfoNodo> entries = this.completeNetwork.keySet().stream().collect(Collectors.toList());

        List<InfoNodo> allNeighbours = null;

        for(InfoNodo entry : entries){
            if(entry.getIp().equals(i.getIp()) && entry.portNet == i.portNet){
                allNeighbours = completeNetwork.get(entry);
            }
        }
        return allNeighbours;
    }



    public List<InfoNodo> getNeighbours(InfoNodo node) {


        List<InfoNodo> allNeighbours = getAllNeighbours(node);
        System.out.println("1");
        System.out.println(allNeighbours);
        System.out.println("2");
        List<InfoNodo> activeNodes = this.activeNetwork.keySet().stream().collect(Collectors.toList());
        System.out.println(activeNodes);
        List<InfoNodo> res = activeNodes.stream().distinct().filter(allNeighbours::contains).collect(Collectors.toList());
        System.out.println("Result getNeig");
        System.out.println(res);
        return res;




        /*
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


        }*/


    }


    public static void main(String[] args) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        Typology typologyTest = new Typology();
        typologyTest.parse("C:\\Users\\migue\\Desktop\\ESR\\src\\otherServer\\Config\\biggerConfiguration.txt");
        typologyTest.setCompleteNetwork();

        // Print the complete network
        System.out.println("Complete Network");
        Map<InfoNodo, List<InfoNodo>> completeLayoutTest = typologyTest.getCompleteNetwork();
        for (Map.Entry<InfoNodo, List<InfoNodo>> entry : completeLayoutTest.entrySet()) {
            InfoNodo key = entry.getKey();
            List<InfoNodo> value = entry.getValue();

            System.out.println(">>>" + key.toStringCon());

            for (InfoNodo node : value) {
                System.out.println(node.toStringCon());
            }
        }
        System.out.println("\n\n");

        /*
            The following code is not yet a formal test: the delay values were hand crafted
         */

        // Activate some nodes

        typologyTest.activateConnection(typologyTest.getNodes().get("n1"), false);
        typologyTest.activateConnection(typologyTest.getNodes().get("n2"), false);

        //typologyTest.activateConnection(typologyTest.getNodes().get("n1"),new Connection(typologyTest.getNodes().get("n1") ,typologyTest.getNodes().get("s1"),1,2), false );
        /*
        typologyTest.activateConnection(typologyTest.getNodes().get("n2"),new Connection(typologyTest.getNodes().get("n2") ,typologyTest.getNodes().get("c2"),2,3), false );
        //typologyTest.activateConnection(typologyTest.getNodes().get("n2"),new Connection(typologyTest.getNodes().get("n2") ,typologyTest.getNodes().get("n3"),3,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n3"),new Connection(typologyTest.getNodes().get("n3") ,typologyTest.getNodes().get("n2"),3,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n3"),new Connection(typologyTest.getNodes().get("n3") ,typologyTest.getNodes().get("n4"),2,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n3"),new Connection(typologyTest.getNodes().get("n3") ,typologyTest.getNodes().get("n5"),5,3), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n4"),new Connection(typologyTest.getNodes().get("n4") ,typologyTest.getNodes().get("n5"),2,4), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n5"),new Connection(typologyTest.getNodes().get("n5") ,typologyTest.getNodes().get("n2"),2,4), false );
        typologyTest.activateConnection(typologyTest.getNodes().get("n5"),new Connection(typologyTest.getNodes().get("n5") ,typologyTest.getNodes().get("c1"),2,4), false );

         */


        // Print the active nodes
        System.out.println("Active Network");
        Map<InfoNodo, List<Connection>> activeLayoutTest = typologyTest.getActiveNetwork();
        printInfoFromMap(activeLayoutTest);

        // Print the best paths tree (it was setted during the activation of the nodes)
        System.out.println("\n\n");
        System.out.println("Best Paths Tree");
        Map<InfoNodo, List<Connection>> bestPaths = typologyTest.getBestPaths();
        printInfoFromMap(bestPaths);

        /*
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
        }*/


        // Test XML
        System.out.println("\n\n");
        XMLParser xmlParser = new XMLParser();
        String xml = xmlParser.generateXML(typologyTest.nodes, typologyTest.bestPaths);
        System.out.println(xmlParser.prettyPrintByTransformer(xml, 1, false));
        System.out.println("\n\n");
        xmlParser.parseXML(xml);

        byte[] xmlBytes = xmlParser.fromStringToBytes(xml);
        String xml2 = xmlParser.fromBytesToString(xmlBytes);
        System.out.println("\n\n");
        System.out.println(xmlParser.prettyPrintByTransformer(xml2, 1, false));
        //System.out.println(xml.getXMLString());

    }

    public static void printInfoFromMap(Map<InfoNodo, List<Connection>> bestPaths) {
        for (Map.Entry<InfoNodo, List<Connection>> entry : bestPaths.entrySet()) {
            InfoNodo key = entry.getKey();
            List<Connection> value = entry.getValue();

            System.out.println(">>>" + key.toString());

            for (Connection connection : value) {
                System.out.println(connection.toString());
            }
        }
    }


    // função de update à aresta

}



