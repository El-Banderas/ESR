package otherServer;
/* ------------------
   otherServer.Servidor
   usage: java otherServer.Servidor [Video file]
   adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
   colocar primeiro o cliente a correr, porque este dispara logo
   ---------------------- */

import Common.InfoNodo;
import otherServer.Bootstrapper.Bootstrapper;
import otherServer.Stream.Stream;

import java.net.*;

/**
 * O Bootstrapper é que trata da comunicações da rede e assim.
 * Por isso, meti um boot a correr no servidor.
 */


public class Servidor //extends JFrame implements ActionListener
 {
  //------------------------------------
  //main  Porta filho | porta atual
  //------------------------------------
  public static void main(String argv[]) throws Exception {
      System.out.println("[Server] Started ");
      InetAddress IP_Bootstrapper = InetAddress.getByName("127.0.0.1");


      if (argv.length < 2 ) {
          Bootstrapper bootstrapper = new Bootstrapper();
          new Thread(bootstrapper).start();
      }
      else {

          InetAddress sonIP = InetAddress.getByName("localhost");
          int sonPort = Integer.parseInt(argv[0]);
          InfoNodo sonInfo = new InfoNodo(sonIP, sonPort);


          InetAddress thisIP = InetAddress.getByName("localhost");
          int thisPort = Integer.parseInt(argv[1]);
          InfoNodo serverInfo = new InfoNodo(thisIP, thisPort);

          CommuncationBetweenThreads shared = new CommuncationBetweenThreads(sonInfo);

            // Precisa: serverInfo; filho; coisa partilhada
          Bootstrapper bootstrapper = new Bootstrapper(serverInfo, sonInfo, shared);
          new Thread(bootstrapper).start();

          Stream stream = new Stream(shared);
          new Thread(stream).start();


      }

  }

   




















    /* 
    //get video filename to request:
    if (argv.length >= 1 ) {
        VideoFileName = argv[0];
        System.out.println("otherServer.Servidor: VideoFileName indicado como parametro: " + VideoFileName);
    } else  {
        VideoFileName = "movie.Mjpeg";
        System.out.println("otherServer.Servidor: parametro não foi indicado. VideoFileName = " + VideoFileName);
    }

    File f = new File(VideoFileName);
    if (f.exists()) {
        //Create a Main object 
        otherServer.Servidor s = new otherServer.Servidor();
        //show GUI: (opcional!)
        //s.pack();
        //s.setVisible(true);
    } else
        System.out.println("Ficheiro de video não existe: " + VideoFileName);

        */
  }

 



