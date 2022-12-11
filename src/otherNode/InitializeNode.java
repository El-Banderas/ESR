package otherNode;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.Bootstrapper.Bootstrapper;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class InitializeNode {

    private DatagramSocket socket;
    private InfoNodo boot;


    public InitializeNode(DatagramSocket s, InfoNodo b) {
        this.socket = s;
        this.boot = b;
    }

    public void start() {
        try {
            SendData.sendHelloMsg(this.socket, this.boot.ip, boot.port);
            MessageAndType neigbours =ReceiveData.receiveData(this.socket);
            String neighboursList = findNeighbours(neigbours);
             InfoNodo[] Nlist = parseVizinhos(neighboursList);
             /*
             for(int i=0;i<Nlist.length-1;i++){
                 System.out.println(Nlist[i].toString());
             }
             */
            // envia msg aos vizinhos
             for(int i=0; i<Nlist.length-1;i++) {
              SendData.sendTimeStamp(this.socket,Nlist[i].ip,Nlist[i].port);
             }

            //recebe msg dos vizinhos


            //envia servidor info dos vizinhos ativos



        } catch (
                IOException e) {
            System.out.println("[Node] Timeout");
        }

    }


    public String findNeighbours(MessageAndType neigbours){

        String word = new String(neigbours.packet.getData());
        System.out.println("Server said: \n Your neighbours: \n");
        String[] neighboursList = word.split( "END");
        return neighboursList[0];
    }


    public InfoNodo[] parseVizinhos(String vizinhos) throws UnknownHostException {


        String[] neighboursList = vizinhos.split( "/");
        InfoNodo[] nodos= new InfoNodo[neighboursList.length];


        String[] ips = new String[neighboursList.length-1];
        int[] portas = new int[neighboursList.length-1];

        for(int i=1; i<neighboursList.length; i++){
            String aux1[] = neighboursList[i].split("\\s+");
            ips[i-1]= aux1[0];
            String aux2[] = neighboursList[i].split("-",2);
            String aux3[] = aux2[1].split("\\s+");
            String aux4[] = aux3[1].split("}");
            portas[i-1]=  Integer.parseInt(aux4[0]);
        }

        for(int i =0; i< ips.length; i++){
            nodos[i] = new InfoNodo(InetAddress.getByName(ips[i]),portas[i]);
        }

        return nodos;
    }
}
