package otherNode;

import Common.Constants;
import Common.MessageAndType;
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

            MessageAndType received = ReceiveData.receiveData(socket);
            String message
                    = new String(received.packet.getData(), 0, received.packet.getLength());
            System.out.println(message);
        }

    }
    }

