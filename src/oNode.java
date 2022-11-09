import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.Iterator;


public class oNode {

  
    

    public static void main(String[] args) {

      Socket socket = null;
      InputStreamReader input = null;
      OutputStreamWriter output = null;
      BufferedReader br = null;
      BufferedWriter bw = null;


    try{

        socket = new Socket("localhost", 1234);
        
        input = new InputStreamReader(socket.getInputStream());
        output = new OutputStreamWriter(socket.getOutputStream());

        br = new BufferedReader(input);
        bw = new BufferedWriter(output);

        Scanner scanner = new Scanner(System.in);

         while( true){ 
            
          String msgtoSend = args[0] + "/" + scanner.nextLine();


          bw.write(msgtoSend);
          bw.newLine();
          bw.flush();

          System.out.println("Server: " + br.readLine());
          break;


         }
         
    

        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }     



   }

  }




