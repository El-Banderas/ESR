package Client;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class InitClient {
    private DatagramSocket socketNet;
    private DatagramSocket socketStream;
    private InfoNodo infoBoot;
    private InfoNodo infoClientNet;

    public InitClient(InfoNodo infoBoot, InfoNodo infoClientNet) {
        this.infoBoot = infoBoot;
        this.infoClientNet = infoClientNet;
        try {
            socketNet = new DatagramSocket(infoClientNet.portNet);
            socketStream = new DatagramSocket(infoClientNet.portNet+1);

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void runClient() {

        try {
            SendData.sendHelloMsgClient(this.socketNet, infoBoot);
            MessageAndType received = ReceiveData.receiveData(socketNet);

            handleReceivedMessage(received);



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleReceivedMessage(MessageAndType received) {
        switch (received.msgType) {
            case Constants.impossibleConnection:
                System.out.println("Conexão impossível, tente mais tarde.");
                break;
            case Constants.wakeUpClient:
                System.out.println("Recebeu o pai.");
                startClient(received.packet);
                break;
            default:
                System.out.println("Ignora");
                System.out.println("Recebeu mensagem do tipo: " + received.msgType);
        }
    }

    private void startClient(DatagramPacket packet) {
        InfoNodo parent = new InfoNodo(packet.getAddress(), packet.getPort());
        System.out.println("Create parent " + parent);
        try {

            ClientInformParent cli = new ClientInformParent(parent, infoBoot, infoClientNet, socketNet, socketStream);
            cli.start();

        } catch (UnknownHostException e) {
            System.out.println("Error creating final client.");
            throw new RuntimeException(e);
        }

    }
}
