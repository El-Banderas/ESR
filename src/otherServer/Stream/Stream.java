package otherServer.Stream;

import TransmitData.SendData;
import otherServer.CommuncationBetweenThreads;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

public class Stream implements Runnable {
    private CommuncationBetweenThreads shared;
    private DatagramSocket socket;

    public Stream(CommuncationBetweenThreads shared) {
        this.shared = shared;
        // The port of the server could be random, because no one wants to send data to the server.
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("[STREAM] Error creating server");
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        System.out.println( "[Server - stream] Start");
        byte[] contentToSend = "Olá do servidor".getBytes(StandardCharsets.UTF_8);
        while(true){
            try {
                sleep(5000);
                System.out.println("[Server - Stream] Stream send? " + shared.getSendStream());
                if (shared.getSendStream()){
                    // TODO: Send something to children
                    SendData.sendStreamContentMSG(this.socket, shared.son, contentToSend);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
