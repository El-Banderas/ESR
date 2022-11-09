package otherServer;


import Common.Constants;
import TransmitData.SendData;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class MainServer {

  private static byte[] buf = new byte[Constants.arraySize];

  public static void main(String argv[]) throws Exception {

    String msg = "Hello";
    DatagramSocket socket = new DatagramSocket(Constants.portServer);
    System.out.println("otherServer.otherServer.Servidor ativo");

    while(true) {
      buf = msg.getBytes();
      SendData.sendData(socket, buf, InetAddress.getByName(Constants.Node1IP), Constants.portNode1 );

    }
  }

  private byte[] convertIntToByteArray(int x){
    return ByteBuffer.allocate(4).putInt(x).array();
  }

  private void sendTree() throws UnknownHostException {
    byte[] node2IPByte = InetAddress.getByName(Common.Constants.Node2IP).getAddress();
    byte[] bytesPort2 = convertIntToByteArray(Constants.portNode2);

    byte[] node3IPByte = InetAddress.getByName(Common.Constants.Node3IP).getAddress();
    byte[] bytesPort3 = convertIntToByteArray(Constants.portNode3);
  }

}