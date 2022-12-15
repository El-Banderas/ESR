package otherServer.SendStream;

import Common.Constants;
import Common.Stream.ConstantesStream;
import Common.Stream.RTPpacket;
import Common.Stream.VideoStream;
import otherServer.CommuncationBetweenThreads;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import static Common.Stream.ConstantesStream.*;
import static java.lang.Thread.sleep;

public class SendStream implements Runnable {
    private final CommuncationBetweenThreads shared;

    DatagramPacket senddp; //UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet

    // Um destes desaparece



    //video file to request to the server

    int imagenb = 0; //image nb of the image currently transmitted
    VideoStream video; //Common.Stream.VideoStream object used to access video frames
   // static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
//    static int FRAME_PERIOD = 10; //Frame period of the video to stream, in ms //Para controlar a velocidade

   // static int VIDEO_LENGTH = 500; //length of the video in frames
    // O de cima não devia ser constante?

    byte[] sBuf; //buffer used to store the images to send to the client


    public SendStream(CommuncationBetweenThreads shared) {
        this.shared = shared;
        // The port of the server could be random, because no one wants to send data to the server.

        try{
            // init para a parte do servidor
            sBuf = new byte[15000]; //allocate memory for the sending buffer

            RTPsocket = new DatagramSocket(); //init RTP socket (o mesmo para o cliente e servidor)
            RTPsocket.setSoTimeout(Constants.timeoutSockets); // setimeout to 5s
            if(Constants.Windows){
                video = new VideoStream(VideoFileName); //init the Common.Stream.VideoStream object:
            }else {
                video = new VideoStream(VideoFileNameCORE);
            }

        } catch(SocketException e)
        {
            System.out.println("Teste: erro no socket: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {
        // Constantly checks if the server should send stream
        while (true) {
            if (shared.sendStream) sendStream();
            else {
                try {
                    // If no one is interested, don't send, and sleep.
                    Thread.sleep(Constants.timeoutSockets);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    private void sendStream() {
        // While someone is interested.
        while (shared.sendStream) {
            if (imagenb < VIDEO_LENGTH) {
                //update current imagenb
                imagenb++;

                try {
                    //get next frame to send from the video, as well as its size
                    int image_length = video.getnextframe(sBuf);

                    //Builds an Common.Stream.RTPpacket object containing the frame
                    RTPpacket rtp_packet = new RTPpacket(ConstantesStream.MJPEG_TYPE, imagenb, imagenb * ConstantesStream.FRAME_PERIOD, sBuf, image_length);

                    //get to total length of the full rtp packet to send
                    int packet_length = rtp_packet.getlength();

                    //retrieve the packet bitstream and store it in an array of bytes
                    byte[] packet_bits = new byte[packet_length];
                    rtp_packet.getpacket(packet_bits);

                    //send the packet as a DatagramPacket over the UDP socket
int portDest = shared.son.portNet;
portDest++;
                     senddp = new DatagramPacket(packet_bits, packet_length, shared.son.ip, portDest);
                    System.out.println("Send stream to: "  + portDest);
                    RTPsocket.send(senddp);
                    //System.out.println("[SendStream] Envia stream para: " + shared.son.ip + " - " + shared.son.portNet+1);

                } catch (Exception ex) {
                    System.out.println("Exception caught: " + ex);
                    ex.printStackTrace();
                    System.exit(0);
                }
            } else {
                //if we have reached the end of the video file, stop the timer
                imagenb = 0;
                video.resetFileReader();
            }
            try {
                Thread.sleep(ConstantesStream.FRAME_PERIOD);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
