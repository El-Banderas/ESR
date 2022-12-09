package Client;


import Common.Constants;
import Common.MessageAndType;
import Common.Stream.ConstantesStream;
import Common.Stream.RTPpacket;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.awt.*;
import java.util.Queue;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Timer;

/**
 * Producer
 * <p>
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 * <p>
 * TODO:
 * Não ignorar os números de sequência, dar-lhes importância;
 * Para além da queue, arranjar maneira de, quando o cliente meter pausa, ele começar a "ignorar" pacotes.
 *      Ter mais coisas partilhadas
 */
public class ClientInformParent implements Runnable {
    public int parentPort = 0;
    public int thisPort = 0;
    public InetAddress parentIP;
    public DatagramSocket socket;

    private Toolkit toolkit;

    // De momento, armazenamos numa queue, porque é FIFO
    // Queue<Image> receivedContent = new LinkedList<>();
    private ShareVariablesClient shared;
    private boolean startConsumer;
    private int sizeBufferBeforeConsumer = 20;
    private StreamWindow window;
    private int numPacketsReceived;

    public ClientInformParent(int parentPort, int thisPort) throws UnknownHostException {
        this.parentPort = parentPort;
        this.thisPort = thisPort;
        this.parentIP = InetAddress.getByName("localhost");
        this.toolkit = Toolkit.getDefaultToolkit();
        this.startConsumer = false;
        this.numPacketsReceived = 0;
        this.shared = new ShareVariablesClient();
        try {
            if (this.thisPort > 0) {
                socket = new DatagramSocket(this.thisPort);
            } else
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
        byte[] cBuf = new byte[15000]; //buffer used to store data received from the server
        DatagramPacket rcvdp; //UDP packet received from the server (to receive)

        while (true) {
            //while (!somethingReceived) {
            try {
                // De X em X tempo, envia para o parentport um hello com timestamp
                // Falta controlar se recebeu mensagem para atualizar pai.
                SendData.sendStillAliveMSG(socket, this.parentIP, this.parentPort, Constants.sitllAliveWithInterest);
                // System.out.println("Envia hello msg");


                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);

                //MessageAndType received = ReceiveData.receiveData(socket);
                //receiveStreamContentMSG();
                //handleReceivedMessage(received);

            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("[Client] Timeout socket");
            }
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType) {
            case Constants.sitllAliveNoInterest:
            case Constants.sitllAliveWithInterest:
                System.out.println("");
                break;
            default:
                RTPpacket rtp_packet = new RTPpacket(received.packet.getData(), received.packet.getLength());

                //rtp_packet.printheader();
                //System.out.println("Armazena pacote " + rtp_packet.getsequencenumber());
                store_packet(rtp_packet);
                numPacketsReceived++;
                // Quando o número de pacotes ultrapassa, e o consumidor é falso, começa cliente
                // Isto só acontece uma vez
                if (numPacketsReceived > ConstantesStream.maxSizeBuffer && !startConsumer) {
                    window = new StreamWindow(shared);
                    startConsumer = true;
                    System.out.println("Start consumer");
                    window.start();
                    //new Timer().schedule(new StreamConsumer(receivedContent, window), 0, StreamWindow.FRAME_PERIOD);
                }
        }

    }

    private void store_packet(RTPpacket rtpPacket) {
        int payload_length = rtpPacket.getpayload_length();
        //System.out.println("Insere pacote");
        byte[] payload = new byte[payload_length];
        rtpPacket.getpayload(payload);

        Image image = toolkit.createImage(payload, 0, payload_length);
        if (shared.isPlay() || (!shared.isPlay() && !ConstantesStream.dropPacketsWhenPause))
            shared.insertImage(image);
        else shared.replaceImage(image);
    }


}
