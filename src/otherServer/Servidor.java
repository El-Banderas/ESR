package otherServer;
/* ------------------
   otherServer.Servidor
   usage: java otherServer.Servidor [Video file]
   adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
   colocar primeiro o cliente a correr, porque este dispara logo
   ---------------------- */

import Common.InfoNodo;
import otherServer.Bootstrapper.Bootstrapper;
import otherServer.SendStream.SendStream;

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
    public static void main(String[] argv) throws Exception {
        System.out.println("[Server] Started ");
        InetAddress IP_Bootstrapper = InetAddress.getByName("127.0.0.1");


            boolean separateNodes = false;
            if (separateNodes) {
                InetAddress sonIP = InetAddress.getByName("localhost");
                int sonPort = Integer.parseInt(argv[0]);
                InfoNodo sonInfo = new InfoNodo(sonIP, sonPort);


                InetAddress thisIP = InetAddress.getByName("localhost");
                int thisPort = Integer.parseInt(argv[1]);
                InfoNodo serverInfo = new InfoNodo(thisIP, thisPort);

                CommuncationBetweenThreads shared = new CommuncationBetweenThreads(sonInfo);
                System.out.println("OLÀÀÀÀ");
                // Precisa: serverInfo; filho; coisa partilhada
                Bootstrapper bootstrapper = new Bootstrapper(serverInfo, sonInfo, shared, true);
                new Thread(bootstrapper).start();

                SendStream stream = new SendStream(shared);
                new Thread(stream).start();
            } else {
                System.out.println("Versão correta");
                InetAddress sonIP = InetAddress.getByName("localhost");
                int sonPortNet = Integer.parseInt(argv[0]);
                int sonPortStream = Integer.parseInt(argv[1]);

                InfoNodo sonInfo = new InfoNodo(sonIP, sonPortNet, sonPortStream);

                InetAddress thisIP = InetAddress.getByName("localhost");
                int thisPort = Integer.parseInt(argv[1]);
                InfoNodo serverInfo = new InfoNodo(thisIP, thisPort);

                CommuncationBetweenThreads shared = new CommuncationBetweenThreads(sonInfo);

                // Precisa: serverInfo; filho; coisa partilhada
                Bootstrapper bootstrapper = new Bootstrapper(serverInfo, sonInfo, shared, true);
                new Thread(bootstrapper).start();

                SendStream stream = new SendStream(shared);
                new Thread(stream).start();
            }

        }

    public static void runServer (InfoNodo serverInfo, boolean isPrinciple){

        CommuncationBetweenThreads shared = new CommuncationBetweenThreads();

        // Precisa: serverInfo; filho; coisa partilhada
        Bootstrapper bootstrapper = new Bootstrapper(serverInfo, shared, isPrinciple);
        new Thread(bootstrapper).start();

        SendStream stream = new SendStream(shared);
        new Thread(stream).start();

    }

    public static void runServer (InfoNodo bootInfo, InfoNodo altBootInfo){

        CommuncationBetweenThreads shared = new CommuncationBetweenThreads();

        // Precisa: serverInfo; filho; coisa partilhada
        Bootstrapper bootstrapper = new Bootstrapper(bootInfo, altBootInfo, shared);
        new Thread(bootstrapper).start();

        SendStream stream = new SendStream(shared);
        new Thread(stream).start();

    }

    public static void runServer (){

        CommuncationBetweenThreads shared = new CommuncationBetweenThreads();

        // Precisa: serverInfo; filho; coisa partilhada
        Bootstrapper bootstrapper = new Bootstrapper(shared, true);
        new Thread(bootstrapper).start();

        SendStream stream = new SendStream(shared);
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

 



