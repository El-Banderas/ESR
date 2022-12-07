package Client;


import Common.Constants;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.Bootstrapper.InfoConnection;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class ClientInformParent implements Runnable {
    public int parentPort = 0;
    public int thisPort = 0;
    public InetAddress parentIP ;
    public DatagramSocket socket;

    public ClientInformParent(int parentPort, int thisPort) throws UnknownHostException {
        this.parentPort = parentPort;
        this.thisPort = thisPort;
        this.parentIP = InetAddress.getByName("localhost");
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
    }

    @Override
    public void run() {



        System.out.println("otherServer.otherServer.Client ativo");
        byte[] buf = new byte[100];
        DatagramPacket receive = new DatagramPacket(buf, buf.length);
        boolean somethingReceived = false;
        // While the Node doesn't receive anything, send still alives
        while (!somethingReceived) {
            try {
                // De X em X tempo, envia para o parentport um hello com timestamp
                // Falta controlar se recebeu mensagem para atualizar pai.
                SendData.sendStillAliveMSG(socket, this.parentIP, this.parentPort, Constants.sitllAliveWithInterest);
                System.out.println("[ClientInformParent]Send still alive");
                MessageAndType received = ReceiveData.receiveData(socket);
                receiveStreamContentMSG(received.packet);
                somethingReceived = true;
                //handleReceivedMessage(received);

            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("[Client] Timeout socket");
            }
        }
        }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType){
            case Constants.streamContent:

                System.out.println("Recebi coisas");
break;
            default:
                System.out.println("\n[Client] What I received? " +Constants.convertMessageType(received.msgType) + "\n");
        }
    }

    private void receiveStreamContentMSG(DatagramPacket packet) throws IOException {
        //byte[] content = ReceiveData.receiveStreamContentMSG(packet);
        ClienteStream receiveStream = new ClienteStream(socket, this.parentIP, this.parentPort, packet);
        try {
            receiveStream.start();
        } catch (Exception e) {
            System.out.println("[Client] Problem receiving stream");
            throw new RuntimeException(e);
        }
    }


}
