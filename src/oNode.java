import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLDocument.Iterator;


public class oNode {
    

    public static void main(String[] args) {


    try{


         String ip = args[0];
         Nodo novo = new Nodo("bootstrapper", InetAddress.getByName(ip));


          Layout p = new Layout();

          p.parse("config.txt");

          
          Map<String, List<String>> rede = new HashMap<>();
           List<Nodo> nodos = new ArrayList<>();


          rede = p.getRede();
          nodos = p.getNodos();

          nodos.add(novo);

          
          for(int i=0;i<nodos.size();i++){
            System.out.println(nodos.get(i).toString());
          }
        

      
        for (String name: rede.keySet()) {
          String key = name.toString();
          String value = rede.get(name).toString();
          System.out.println(key +  "-  " + value);
      }

    

        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }     



   }

  }




