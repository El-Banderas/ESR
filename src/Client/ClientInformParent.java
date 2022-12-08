package Client;


import Common.Constants;
import Common.Stream.RTPpacket;
import TransmitData.SendData;

import java.awt.*;
import java.util.Queue;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

/**
 *  Producer
 *
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class ClientInformParent implements Runnable {
    public int parentPort = 0;
    public int thisPort = 0;
    public InetAddress parentIP ;
    public DatagramSocket socket;

    private Toolkit toolkit;

    // De momento, armazenamos numa queue, porque é FIFO
    Queue<Image> receivedContent = new LinkedList<>();

    public ClientInformParent(int parentPort, int thisPort) throws UnknownHostException {
        this.parentPort = parentPort;
        this.thisPort = thisPort;
        this.parentIP = InetAddress.getByName("localhost");
        this.toolkit = Toolkit.getDefaultToolkit();
        try {
            if (this.thisPort > 0) {
                socket = new DatagramSocket(this.thisPort);
            }
            else
                socket = new DatagramSocket();
                socket.setSoTimeout(Constants.timeoutSockets);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Client] Error creating socket");
        }
    }

    @Override
    public void run() {



        System.out.println("otherServer.otherServer.Client ativo");
        boolean somethingReceived = false;
        // While the Node doesn't receive anything, send still alives
        byte[] cBuf= new byte[15000]; //buffer used to store data received from the server
        DatagramPacket rcvdp; //UDP packet received from the server (to receive)

        while (true) {
        //while (!somethingReceived) {
            try {
                // De X em X tempo, envia para o parentport um hello com timestamp
                // Falta controlar se recebeu mensagem para atualizar pai.
                SendData.sendStillAliveMSG(socket, this.parentIP, this.parentPort, Constants.sitllAliveWithInterest);
                System.out.println("Envia hello msg");
                rcvdp = new DatagramPacket(cBuf, cBuf.length);

                socket.receive(rcvdp);
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());
                rtp_packet.printheader();
                store_packet(rtp_packet);

                //MessageAndType received = ReceiveData.receiveData(socket);
                //receiveStreamContentMSG();
                //handleReceivedMessage(received);

            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("[Client] Timeout socket");
            }
        }
        }

    private void store_packet(RTPpacket rtpPacket) {
        int payload_length = rtpPacket.getpayload_length();
        byte [] payload = new byte[payload_length];
        Image image = toolkit.createImage(payload, 0, payload_length);

        receivedContent.add(image);
    }


}
