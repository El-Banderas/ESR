package otherServer;
/* ------------------
   otherServer.Servidor
   usage: java otherServer.Servidor [Video file]
   adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
   colocar primeiro o cliente a correr, porque este dispara logo
   ---------------------- */

import Common.InfoNodo;

import java.net.*;



public class Servidor //extends JFrame implements ActionListener
 {
  //------------------------------------
  //main
  //------------------------------------
  public static void main(String argv[]) throws Exception {
      InetAddress IP_Bootstrapper = InetAddress.getByName("127.0.0.1");

    ServerData th_ServerData = new ServerData();
    new Thread(th_ServerData).start();

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

 



