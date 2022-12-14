package TransmitData;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import org.xml.sax.SAXException;
import otherServer.Bootstrapper.Connection;
import otherServer.Bootstrapper.InfoConnection;
import otherServer.Bootstrapper.Typology;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ReceiveData {

    public static InfoConnection receiveStillAliveMSG(DatagramPacket packet) {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        InfoNodo other = new InfoNodo(packet.getAddress(), packet.getPort());

 //       boolean interested;
        int type = msg.getInt();
//        interested = type != Constants.sitllAliveNoInterest;

        double msgTime = msg.getDouble();
        double now = Constants.getCurrentTime();
        double delay = now-msgTime;
// Estou a meter o interested como falso, o que não é necessariamente verdade.
        // O still alive agora é sobre o pai, e ele não tem "interesse"
        return new InfoConnection(other, delay, now, false);
    }





    public static void receivedHelloMsg(DatagramPacket packet, DatagramSocket s, Typology t, InfoNodo bootAlter) throws IOException {
        int sizeInetAdressByteArray = 4;

        InfoNodo i = new InfoNodo(packet.getAddress(),packet.getPort());

        List<InfoNodo> vizinhos = t.getNeighbours(i);

        System.out.println("CHEGOU AQUI \n");
        System.out.println(vizinhos.get(0).toStringCon());

        StringBuilder v = new StringBuilder() ;
        for(InfoNodo nodo :vizinhos) {
            v.append(nodo.toString());
        }
        String vs = v.toString();
        boolean isBootAlter = bootAlter != null;
        int isBoolAlterInt = isBootAlter ? 1 : 0;
        byte[] bytesIP = new byte[sizeInetAdressByteArray];
        if (isBootAlter) {
            // Se for windows, metos o ip, senão é a porta
            if (Constants.Windows) {
                ByteBuffer bb = ByteBuffer.allocate(4);
                bb.putInt(bootAlter.portNet);
                bytesIP = bb.array();
            } else {
                bytesIP = bootAlter.ip.getAddress();


            }
        }
        else{
            // No alter boot
            bytesIP = InetAddress.getByName("1.2.3.4").getAddress();

        }
        byte[] bytes = ByteBuffer.allocate(4+4+bytesIP.length+18+(2*vs.length()))
                .putInt(Constants.sendNeibourghs).
                putInt(isBoolAlterInt).
                put(bytesIP).
                put(vs.getBytes()).array();

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

        System.out.println("\n\n\n\nNão devia aparecer \n\n\n");

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
        double delay = msg.getDouble();

        int saltos = msg.getInt();
        int tamanho1 = msg.getInt();
        int tamanho2 = msg.getInt();
        byte[] info1 = new byte[tamanho1];
        byte[] info2 = new byte[tamanho2];
        byte[] twoClasses = new byte[tamanho1+tamanho2];
        System.arraycopy(msg.array(), 3*4, twoClasses,0,tamanho1+tamanho2);

        System.arraycopy(twoClasses, 0, info1, 0, tamanho1);
        System.arraycopy(twoClasses, tamanho1, info2, 0, tamanho2);
        System.out.println("Ver que número é");
        return new Connection(InfoNodo.BytestoNode(info1),InfoNodo.BytestoNode(info2),delay,saltos);

    }



    /**
     * When receives stream, sends to interested sons.
     * @param socket
     * @param interestedSons
     * @throws IOException
     */
    public static void nodeReceiveStream(DatagramSocket socket, ArrayList<InfoNodo> interestedSons) throws IOException {
        byte[] buf = new byte[15000];
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        System.out.println("Recebi stream, vou enviar para os filhos");
        System.out.println(interestedSons);

        for (InfoNodo son : interestedSons){
            try {
               // System.out.println("Envia para o filho");
               // System.out.println(son);
                SendData.sendStreamContentMSG(socket, son, packet.getData());
            } catch (IOException e) {
                System.out.println("What son not receive: ");
                System.out.println(son);
                throw new RuntimeException(e);
            }
        }

    }

    public static Connection BootreceivedTimeStamp(DatagramPacket packet, InetAddress ipBoot, int portaBoot) throws IOException, ParserConfigurationException, SAXException {

        ByteBuffer msg = ByteBuffer.wrap(packet.getData());
        int type = msg.getInt();
        double time = msg.getDouble();
        int numHops= msg.getInt();

        InfoNodo from = new InfoNodo(packet.getAddress(),packet.getPort());
        InfoNodo to = new InfoNodo(ipBoot,portaBoot);
        double delay = Constants.getCurrentTime()-time;
        Connection co = new Connection(from, to, delay, numHops);
        //XMLParser parser = new XMLParser();
        // parser.parseXML(xml);


        return co;
    }



    public static Connection receivedTimeStamp(DatagramPacket packet, InetAddress ip, int porta, DatagramSocket s, InfoConnection n) throws IOException, ParserConfigurationException, SAXException {

        ByteBuffer msg = ByteBuffer.wrap(packet.getData());
        int type = msg.getInt();
        double time = msg.getDouble();
        int numHops= msg.getInt();

        InfoNodo from = new InfoNodo(packet.getAddress(),packet.getPort());
        InfoNodo to = new InfoNodo(ip,porta);
        double delay = Constants.getCurrentTime()-time;
        Connection co = new Connection(from, to, delay, numHops);
        //XMLParser parser = new XMLParser();
        // parser.parseXML(xml);

        System.out.println("Vai enviar send con para " + n.otherNode.ip);
        SendData.sendConnection(s,co,n.otherNode.ip,n.otherNode.portNet);

        return co;
    }

    public static InfoNodo receivedWantStream(DatagramPacket packet) {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int portWantStream = msg.getInt();

        return new InfoNodo(packet.getAddress(), packet.getPort(),packet.getPort()+1 );

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


    public static String receivedXML(DatagramPacket packet) {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());
        int type = msg.getInt();
        int sizeXML = msg.getInt();

        byte[] xmlBytes = new byte[sizeXML];
        System.arraycopy(msg.array(), 8, xmlBytes,0,sizeXML);

        System.out.println("Ver o XML que recebi");
        String res = new String(xmlBytes);
        System.out.println(res);
        return res;

    }

    public static int receiveStillAliveFromPrimeBoot(DatagramPacket packet) {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int timeStamp = msg.getInt();
        return timeStamp;
    }

    public static Map<InfoNodo, List<Connection>> getActiveNodes(DatagramPacket packet){
        try {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int sizArray = msg.getInt();
        byte[] map = msg.array();
        // 8 = 2 * 4 (size int)
        byte[] mapCutted = Arrays.copyOfRange(packet.getData(), 8, sizArray+8);
            ByteArrayInputStream bais = new ByteArrayInputStream(mapCutted);
            ObjectInputStream inputStream = new ObjectInputStream(bais);
            Map<InfoNodo, List<Connection>> o = (Map<InfoNodo, List<Connection>>) inputStream.readObject();
            System.out.println("O que recebeu");
            System.out.println(o);
            return o;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
