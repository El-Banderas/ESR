package otherServer.Bootstrapper;

import Common.InfoNodo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Layout {
    
    private Map<String, List<String>> rede;
    private List<InfoNodo> nodos;




    public Layout(){
        this.rede = new HashMap<>();
        this.nodos= new ArrayList<>();
    }

    public Map<String,List<String>> getRede(){
        return this.rede;
    }


    public List<InfoNodo> getNodos(){
        return this.nodos;
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
            nodos.add(n);

            String[] vizinhos = parts[1].split(" *, *");

            List<String> nodosaux = new ArrayList<>();
            for(String v : vizinhos){
                nodosaux.add(v);
            }


             this.rede.put(aux[0],nodosaux);

                // read next line
                line = reader.readLine();
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

}



