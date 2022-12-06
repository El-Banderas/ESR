package otherServer.SendStream;

import Common.Stream.RTPpacket;
import Common.Stream.VideoStream;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerTimerListener implements ActionListener {
    DatagramPacket senddp; //UDP packet containing the video frames (to send)A
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet

    // Um destes desaparece
    static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets
    int RTP_dest_port = 25000; //destination port for RTP packets

    InetAddress ClientIPAddr; //Client IP address


    static String VideoFileName; //video file to request to the server

    int imagenb = 0; //image nb of the image currently transmitted
    VideoStream video; //Common.Stream.VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 10; //Frame period of the video to stream, in ms //Para controlar a velocidade
    static int VIDEO_LENGTH = 500; //length of the video in frames
                                    // O de cima não devia ser constante?

    Timer sTimer; //timer used to send the images at the video frame rate
    byte[] sBuf; //buffer used to store the images to send to the client

    public void actionPerformed(ActionEvent e) {

            //if the current image nb is less than the length of the video
            if (imagenb < VIDEO_LENGTH)
            {
                //update current imagenb
                imagenb++;

                try {
                    //get next frame to send from the video, as well as its size
                    int image_length = video.getnextframe(sBuf);

                    //Builds an Common.Stream.RTPpacket object containing the frame
                    RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb*FRAME_PERIOD, sBuf, image_length);

                    //get to total length of the full rtp packet to send
                    int packet_length = rtp_packet.getlength();

                    //retrieve the packet bitstream and store it in an array of bytes
                    byte[] packet_bits = new byte[packet_length];
                    rtp_packet.getpacket(packet_bits);

                    //send the packet as a DatagramPacket over the UDP socket
                    senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTP_dest_port);
                    RTPsocket.send(senddp);

                    System.out.println("Send frame #"+imagenb);
                    //print the header bitstream
                    rtp_packet.printheader();

                    //update GUI
                    // label.setText("Send frame #" + imagenb);
                }
                catch(Exception ex)
                {
                    System.out.println("Exception caught: "+ex);
                    System.exit(0);
                }
            }
            else
            {
                //if we have reached the end of the video file, stop the timer
                sTimer.stop();
            }
        }

}
