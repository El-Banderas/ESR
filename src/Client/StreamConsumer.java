package Client;

import Common.Stream.RTPpacket;
import Common.Stream.VideoStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.TimerTask;

public class StreamConsumer extends Thread {

    //GUI
    //----
    JFrame f = new JFrame("Teste");
    JButton setupButton = new JButton("Setup");
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JButton tearButton = new JButton("Teardown");
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
    JLabel iconLabel = new JLabel();
    ImageIcon icon;


    //RTP variables:
    //----------------

    // Client timer
    Timer cTimer; //timer used to receive data from the UDP socket
    byte[] cBuf; //buffer used to store data received from the server


    //Video constants:
    //------------------
    int imagenb = 0; //image nb of the image currently transmitted
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 10; //Frame period of the video to stream, in ms //Para controlar a velocidade
    static int VIDEO_LENGTH = 500; //length of the video in frames
    private Queue<Image> receivedContent;

    //--------------------------
    //Constructor
    //--------------------------
    public StreamConsumer(Queue<Image> receivedContent) {
        this.receivedContent = receivedContent;
        //build GUI
        //--------------------------

        //Frame
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Buttons
        buttonPanel.setLayout(new GridLayout(1,0));
        buttonPanel.add(setupButton);
        buttonPanel.add(playButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(tearButton);
        playButton.addActionListener(new Teste.playButtonListener());

        //Image display label
        iconLabel.setIcon(null);

        //frame layout
        mainPanel.setLayout(null);
        mainPanel.add(iconLabel);
        mainPanel.add(buttonPanel);
        iconLabel.setBounds(0,0,380,280);
        buttonPanel.setBounds(0,280,380,50);

        f.getContentPane().add(mainPanel, BorderLayout.CENTER);
        f.setSize(new Dimension(390,370));
        f.setVisible(true);

        //init para a parte do cliente
        //--------------------------
        cTimer = new Timer(20, new Teste.clientTimerListener());
        cTimer.setInitialDelay(0);
        cTimer.setCoalesce(true);
        cBuf = new byte[15000]; //allocate enough memory for the buffer used to receive data from the server

        // init para a parte do servidor
        sTimer = new Timer(FRAME_PERIOD, new Teste.serverTimerListener()); //init Timer para servidor
        sTimer.setInitialDelay(0);
        sTimer.setCoalesce(true);
        sBuf = new byte[15000]; //allocate memory for the sending buffer

        try {
            // socket e video
            RTPsocket = new DatagramSocket(RTP_RCV_PORT); //init RTP socket (o mesmo para o cliente e servidor)
            RTPsocket.setSoTimeout(5000); // setimeout to 5s
            //Get Client IP address
            ClientIPAddr = InetAddress.getByName("127.0.0.1");
            //System.out.println("Teste: vai enviar e receber video no mesmo socket " + ClientIPAddr);
            video = new VideoStream(VideoFileName); //init the Common.Stream.VideoStream object:
            //System.out.println("Teste: vai enviar e receber video da file " + VideoFileName);

        } catch (SocketException e) {
            System.out.println("Teste: erro no socket: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Teste: erro no video: " + e.getMessage());
        }

    }

    //------------------------------------
    //main
    //------------------------------------
    public static void main(String argv[]) throws Exception
    {
        //get video filename to request:
        if (argv.length >= 1 ) {
            VideoFileName = argv[0];
            System.out.println("Teste: VideoFileName indicado como parametro: " + VideoFileName);
        } else  {
            VideoFileName = "C:\\Users\\Diogo\\Desktop\\Diogo\\Trabalhos da escola\\4ano1sem\\redes\\ESR\\src\\out\\production\\ProgEx\\movie.Mjpeg";
            System.out.println("Teste: parametro não foi indicado. VideoFileName = " + VideoFileName);
        }

        File f = new File(VideoFileName);
        if (f.exists()) {
            //Create a Main object
            Teste t = new Teste();
        } else
            System.out.println("Ficheiro de video não existe: " + VideoFileName);
    }


    //------------------------------------
    //Handler for buttons
    //------------------------------------

    //Handler for Play button
    //-----------------------
    public class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){

            System.out.println("Play Button pressed !");
            //start the timers ...
            cTimer.start();
            sTimer.start();
        }
    }

    //------------------------------------
    //Handler for timer (para cliente)
    //------------------------------------

    class clientTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            //Construct a DatagramPacket to receive data from the UDP socket
            rcvdp = new DatagramPacket(cBuf, cBuf.length);

            try{
                //receive the DP from the socket:
                RTPsocket.receive(rcvdp);

                //create an Common.Stream.RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

                //print important header fields of the RTP packet received:
                //System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());

                //print header bitstream:
                //rtp_packet.printheader();

                //get the payload bitstream from the Common.Stream.RTPpacket object
                int payload_length = rtp_packet.getpayload_length();
                byte [] payload = new byte[payload_length];
                rtp_packet.getpayload(payload);

                //get an Image object from the payload bitstream
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.createImage(payload, 0, payload_length);

                //display the image as an ImageIcon object
                icon = new ImageIcon(image);
                iconLabel.setIcon(icon);
            }
            catch (InterruptedIOException iioe){
                System.out.println("Nothing to read");
            }
            catch (IOException ioe) {
                System.out.println("Exception caught: "+ioe);
            }
        }
    }

    //------------------------
    //Handler for timer
    //------------------------
    class serverTimerListener implements ActionListener {
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

                    //System.out.println("Send frame #"+imagenb);
                    //print the header bitstream
                    //rtp_packet.printheader();

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

}