package otherNode;

import Common.Constants;
import TransmitData.ReceiveData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class NotGood_MainNode {

    public static void main(String[] args) throws IOException {
        System.out.println("Common.InfoNodo ativo");
        int portNumber = Integer.parseInt(args[0]);
        DatagramSocket socket = new DatagramSocket(portNumber);
        while (true) {

            DatagramPacket received = ReceiveData.receiveData(socket);
            String message
                    = new String(received.getData(), 0, received.getLength());
            System.out.println(message);
        }

    }
    }

