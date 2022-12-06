package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

import static Common.Constants.portBootSendNeighbours;

public class SendNeighbours implements Runnable {
    public int thisPort = 0; // porta do nodo inicializa
    public InetAddress ip ;
    private DatagramSocket socket;


    public SendNeighbours(int thisPort , InetAddress ip )  {
        this.thisPort=thisPort;
        this.ip = ip;
    }

        public void run() {

            try {
                if (this.thisPort > 0)
                    socket = new DatagramSocket(this.thisPort);
                else
                    socket = new DatagramSocket();
                socket.setSoTimeout(Constants.timeoutSockets);
            } catch (SocketException e) {
                e.printStackTrace();
                System.out.println("[Client] Error creating socket");
            }

            System.out.println("Node on");
            byte[] buf = new byte[100];
            DatagramPacket receivePKT = new DatagramPacket(buf, buf.length);

            while(true) {
                try {

                    int messageType = Constants.hellomesage;
          //      DatagramSocket socket,InetAddress srcIp, int srcPort, InetAddress destIP, int destPort, int messageType
                    InetAddress IP_Bootstrapper = InetAddress.getByName("127.0.0.1");
                    SendData.sendHelloMsg(socket,this.ip,this.thisPort,IP_Bootstrapper,portBootSendNeighbours,messageType);
                    System.out.println(" Send hello msg, type: " + Constants.convertMessageType(messageType));
                    // receber resposta servidor
                    MessageAndType received = ReceiveData.receiveData(socket);
                    receivedSNeigbours(received.packet);
                } catch (IOException e) {
                    System.out.println("[Node] Timeout");
                }

            }


        }




    private void receivedSNeigbours(DatagramPacket packet) throws IOException {

        System.out.println("OS TEUS VIZINHOS VAO ESTAR AQUI \n ");
// funcao para ler um packet com os vizinhos e imprimi -los







      /*
       StillAliveMsgContent time = ReceiveData.receiveStillAliveMSG(packet);
        InfoConnection info = ReceiveData.receiveStillAliveMSG(packet);

        if (info.delay > Constants.minDelayToTrigger){
            sendTooMuchDelay(info.otherNode);
        }

        int portOther = info.otherNode.port;
        InetAddress ipOther = info.otherNode.ip;

        System.out.println("\nReceived still alive msg (interested?): " + info.interested);
        System.out.println("From: " + ipOther + " " + portOther+ " port.");

        List otherSons = sons.stream().filter(son -> (son.otherNode.ip != ipOther && son.otherNode.port != portOther)).collect(Collectors.toList());
        System.out.println("Delay = " + info.delay+ "\n");
        // Remove the current node from the sons list.
        sons = new ArrayList<InfoConnection>(otherSons);
        // And add the new information to sons list, with updated info.
        sons.add(info);
        */
    }


}


