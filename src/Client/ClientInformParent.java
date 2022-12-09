package Client;


import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import Common.Stream.ConstantesStream;
import Common.Stream.RTPpacket;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Producer
 * <p>
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 * <p>
 * TODO:
 * Não ignorar os números de sequência, dar-lhes importância;
 */
public class ClientInformParent implements Runnable {
    public InfoNodo parent;
    public InfoNodo boot;
    public int thisPort;
    public InetAddress parentIP;
    public DatagramSocket socket;

    private final Toolkit toolkit;

    private final ShareVariablesClient shared;

    // If the client already started
    private boolean startConsumer;
    private StreamWindow window;
    private int numPacketsReceived;




    public ClientInformParent(InfoNodo parent, InfoNodo boot, int thisPort) throws UnknownHostException {
        this.parent = parent;
        this.boot = boot;
        this.thisPort = thisPort;
        this.parentIP = InetAddress.getByName("localhost");
        this.toolkit = Toolkit.getDefaultToolkit();
        this.startConsumer = false;
        this.numPacketsReceived = 0;
        this.shared = new ShareVariablesClient();
        //this.sendStillAlives = new Timer(20, new sendStillAlive());

        //new Timer().scheduleAtFixedRate(new sendStillAlive(), 0, Constants.timeToConsiderNodeLost/2);
        //this.sendStillAlives.setInitialDelay(0);
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
        //sendStillAlives.start();
        Timer timer = new Timer();
        TimerTask sendStillAlives = new sendStillAlive();
        timer.scheduleAtFixedRate(sendStillAlives, 0, Constants.timeToConsiderNodeLost);
        while (true) {
            try {
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);

            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("[Client] Timeout socket");
            }
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType) {
            case Constants.sitllAlive:
                System.out.println();
                break;
            default:
                RTPpacket rtp_packet = new RTPpacket(received.packet.getData(), received.packet.getLength());
                store_packet(rtp_packet);
                numPacketsReceived++;
                // When the buffer reaches the minimum size, we start the stream
                if (numPacketsReceived > ConstantesStream.maxSizeBuffer && !startConsumer) {
                    window = new StreamWindow(shared);
                    startConsumer = true;
                    System.out.println("Start consumer");
                    window.start();
                }
        }

    }

    private void store_packet(RTPpacket rtpPacket) {

        int payload_length = rtpPacket.getpayload_length();

        byte[] payload = new byte[payload_length];
        rtpPacket.getpayload(payload);

        Image image = toolkit.createImage(payload, 0, payload_length);

        // Here we choose if we want to store all packets or replace the packets.
        if (shared.isPlay() || (!shared.isPlay() && !ConstantesStream.dropPacketsWhenPause))
            shared.insertImage(image);

        else shared.replaceImage(image);
    }

    class sendStillAlive extends TimerTask {

        @Override
        public void run() {
            try {
                SendData.wantsStream(socket, parent);
                System.out.println("Send still alive");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
