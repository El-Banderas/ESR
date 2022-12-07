package otherServer.Bootstrapper;

import Common.InfoNodo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class Tipology {

    //Stores the complete tipology
    public Map<String, List<String>> networkString;

    //Stores the list of all the nodes present in the tipology (could be used in some specific case)
    public Map<String,InfoNodo> nodes;




    public Tipology(){
        this.networkString = new HashMap<>();
        this.nodes= new HashMap<>();
    }

    public Tipology(Map<String, List<String>> networkString, Map<String,InfoNodo> nodes) {
        this.networkString = networkString;
        this.nodes = nodes;
    }

    public Map<String, List<String>> getNetwork() {
        return networkString;
    }

    public Map<String,InfoNodo> getNodes() {
        return nodes;
    }


    public void parse(String filename) throws IOException {


        // this method of try with resorces automatically closes buffered reader
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

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


    public Map<InfoNodo, List<InfoNodo>> getCompleteNetwork(){
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
        return completeNetwork;
    }

    public static void main(String[] args) throws IOException {
        Tipology tipologyTest = new Tipology();

        tipologyTest.parse("C:/Users/migue/Desktop/ESR/src/otherServer/topCenario2.txt");

        Map<InfoNodo,List<InfoNodo>> completeLayoutTest = tipologyTest.getCompleteNetwork();

        for (Map.Entry<InfoNodo, List<InfoNodo>> entry : completeLayoutTest.entrySet()) {
            InfoNodo key = entry.getKey();
            List<InfoNodo> value = entry.getValue();

            System.out.println(">>>" + key.toString());

            for(InfoNodo node : value){
                System.out.println(node.toString());
            }
        }




    }

}



