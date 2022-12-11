package TransmitData;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import otherServer.Bootstrapper.Connection;
import otherServer.Bootstrapper.InfoConnection;
import otherServer.Bootstrapper.Typology;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;

public class ReceiveData {

    public static InfoConnection receiveStillAliveMSG(DatagramPacket packet) {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        InfoNodo other = new InfoNodo(packet.getAddress(), packet.getPort());


        double msgTime = msg.getDouble();
        double now = Constants.getCurrentTime();
        double delay = now-msgTime;
// Estou a meter o interested como falso, o que não é necessariamente verdade.
        // O still alive agora é sobre o pai, e ele não tem "interesse"
        return new InfoConnection(other, delay, now, false);
    }





    public static void receivedHelloMsg(DatagramPacket packet, DatagramSocket s, Typology t) throws IOException {
        // get Vizinhos na TypologyGraph
        // vizinhos imaginarios para teste

        InfoNodo i = new InfoNodo(packet.getAddress(),packet.getPort());
        List<InfoNodo> vizinhos = new ArrayList<InfoNodo>();
        vizinhos = t.getNeighbours(i);


        /*
        InfoNodo[] vizinhos = new InfoNodo[5];
        InfoNodo v1 = new InfoNodo(InetAddress.getByName("localhost"),2000);
        InfoNodo v2 = new InfoNodo(InetAddress.getByName("localhost"),2001);
        InfoNodo v3 = new InfoNodo(InetAddress.getByName("localhost"),2002);
        InfoNodo v4 = new InfoNodo(InetAddress.getByName("localhost"),2003);
        InfoNodo v5 = new InfoNodo(InetAddress.getByName("localhost"),2004);
        vizinhos[0]=v1;
        vizinhos[1]=v2;
        vizinhos[2]=v3;
        vizinhos[3]=v4;
        vizinhos[4]=v5;

         */
        // converter a lista de vizinhos num pacote
        StringBuilder v = new StringBuilder() ;
        for(InfoNodo nodo :vizinhos) {
            v.append(nodo.toString());
        }

        String vs = v.toString();
        //String v = String.valueOf(v1) + v2 + v3 + v4 + v5 + "END";

        byte[] bytes = ByteBuffer.allocate(18+(2*vs.length())).put(vs.getBytes()).array();

        SendData.sendData(s,bytes,packet.getAddress(), packet.getPort());
    }




    public static InfoNodo receiveLostNodeMSG(DatagramPacket packet) throws UnknownHostException {
        // To calculate sizes, this could be put in constants, but I don't know the sizes.
        // Change later.
        int sizeInetAdressByteArray = 4;
        int sizeInt = 4;
        /*
        try {
            sizeInetAdressByteArray = InetAddress.getByName("127.0.0.1").getAddress().length;
        } catch (UnknownHostException e) {
            System.out.println("Ignore");
        }
        System.out.println("Size IP: " + sizeInetAdressByteArray);
        */
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int portLostSon = msg.getInt();

        byte[] lostNodeIpPart = new byte[sizeInetAdressByteArray];
        // Cuidado com este 8, é o tamanho de 2 ints
        System.arraycopy(msg.array(), sizeInt*2, lostNodeIpPart, 0, sizeInetAdressByteArray);

        InetAddress ipLostNode = InetAddress.getByAddress(lostNodeIpPart);
        return new InfoNodo(ipLostNode, portLostSon);
    }


    public static byte[] receiveStreamContentMSG(DatagramPacket packet) throws UnknownHostException {
        // To calculate sizes, this could be put in constants, but I don't know the sizes.
        // Change later.
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int sizeContent = msg.getInt();

        byte[] content = new byte[sizeContent];
        // Cuidado com este 8, é o tamanho de 2 ints
        System.arraycopy(msg.array(), 8, content, 0, sizeContent);


        return content;
    }

    public static Connection receiveConnection(DatagramPacket packet) throws UnknownHostException {

        ByteBuffer msg = ByteBuffer.wrap(packet.getData());
        int type = msg.getInt();
        int saltos = msg.getInt();
        int tamanho1 = msg.getInt();
        int tamanho2 = msg.getInt();
        byte[] info1 = new byte[tamanho1];
        byte[] info2 = new byte[tamanho2];
        msg.get(info1);
        msg.get(info2);
        double delay = msg.getDouble();

        return new Connection(InfoNodo.BytestoNode(info1),InfoNodo.BytestoNode(info2),delay,saltos);
    }



    public static MessageAndType receiveData(DatagramSocket socket) throws IOException {
            byte[] buf = new byte[Constants.arraySize];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            int type = ByteBuffer.wrap(buf).getInt();
            MessageAndType received = new MessageAndType(type, packet);
            return received;

        }
}
