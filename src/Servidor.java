/* ------------------
   Servidor
   usage: java Servidor [Video file]
   adaptado dos originais pela equipa docente de ESR (nenhumas garantias)
   colocar primeiro o cliente a correr, porque este dispara logo
   ---------------------- */

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;


public class Servidor extends JFrame implements ActionListener {

  //GUI:
  //----------------
  JLabel label;

  //RTP variables:
  //----------------
  DatagramPacket senddp; //UDP packet containing the video frames (to send)A
  DatagramSocket RTPsocket; //socket to be used to send and receive UDP packet
  int RTP_dest_port = 25000; //destination port for RTP packets 
  InetAddress ClientIPAddr; //Client IP address
  
  static String VideoFileName; //video file to request to the server

  //Video constants:
  //------------------
  int imagenb = 0; //image nb of the image currently transmitted
  VideoStream video; //VideoStream object used to access video frames
  static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
  static int FRAME_PERIOD = 100; //Frame period of the video to stream, in ms
  static int VIDEO_LENGTH = 500; //length of the video in frames

  Timer sTimer; //timer used to send the images at the video frame rate
  byte[] sBuf; //buffer used to store the images to send to the client 

  //--------------------------
  //Constructor
  //--------------------------
  public Servidor() {
    //init Frame
    super("Servidor");

    // init para a parte do servidor
    sTimer = new Timer(FRAME_PERIOD, this); //init Timer para servidor
    sTimer.setInitialDelay(0);
    sTimer.setCoalesce(true);
    sBuf = new byte[15000]; //allocate memory for the sending buffer

    try {
	RTPsocket = new DatagramSocket(); //init RTP socket
    ClientIPAddr = InetAddress.getByName("127.0.0.1");
    System.out.println("Servidor: socket " + ClientIPAddr);
	video = new VideoStream(VideoFileName); //init the VideoStream object:
    System.out.println("Servidor: vai enviar video da file " + VideoFileName);

    } catch (SocketException e) {
        System.out.println("Servidor: erro no socket: " + e.getMessage());
    } catch (Exception e) {
        System.out.println("Servidor: erro no video: " + e.getMessage());
    }

    //Handler to close the main window
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	//stop the timer and exit
	sTimer.stop();
	System.exit(0);
      }});

    //GUI:
    label = new JLabel("Send frame #        ", JLabel.CENTER);
    getContentPane().add(label, BorderLayout.CENTER);
          
    sTimer.start();
  }

  //------------------------------------
  //main
  //------------------------------------
  public static void main(String argv[]) throws Exception
  {
    //get video filename to request:
    if (argv.length >= 1 ) {
        VideoFileName = argv[0];
        System.out.println("Servidor: VideoFileName indicado como parametro: " + VideoFileName);
    } else  {
        VideoFileName = "movie.Mjpeg";
        System.out.println("Servidor: parametro não foi indicado. VideoFileName = " + VideoFileName);
    }

    File f = new File(VideoFileName);
    if (f.exists()) {
        //Create a Main object 
        Servidor s = new Servidor();
        //show GUI: (opcional!)
        //s.pack();
        //s.setVisible(true);
    } else
        System.out.println("Ficheiro de video não existe: " + VideoFileName);
  }

  //------------------------
  //Handler for timer
  //------------------------
  public void actionPerformed(ActionEvent e) {

    //if the current image nb is less than the length of the video
    if (imagenb < VIDEO_LENGTH)
      {
	//update current imagenb
	imagenb++;
       
	try {
	  //get next frame to send from the video, as well as its size
	  int image_length = video.getnextframe(sBuf);

	  //Builds an RTPpacket object containing the frame
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
	  //label.setText("Send frame #" + imagenb);
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

}//end of Class Servidor
