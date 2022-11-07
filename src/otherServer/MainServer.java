package otherServer;


import Common.Constants;
import TransmitData.SendData;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainServer {

  private static byte[] buf = new byte[Constants.arraySize];

  public static void main(String argv[]) throws Exception {

    String msg = "Hello";
    DatagramSocket socket = new DatagramSocket(Constants.portServer);
    System.out.println("Servidor ativo");

    while(true) {
      buf = msg.getBytes();
      SendData.sendData(socket, buf, InetAddress.getByName(Constants.Node1IP), Constants.portNode1 );

    }
  }

  
  private void sendTree(){

  }

}