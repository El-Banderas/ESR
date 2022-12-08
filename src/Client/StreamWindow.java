package Client;

import Common.Stream.VideoStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Queue;
import java.util.TimerTask;

/**
 * Evocada quando queue tem X pacotes
 * O delay é 0, começa logo quando é chamada
 * Tempo entre pacotes é o mesmo do teste, dobro do FRAME_RATE
 */
public class StreamWindow extends Thread {

    //GUI
    //----
    JFrame f = new JFrame("Teste");
    JButton setupButton = new JButton("Setup");
    JButton playButton = new JButton("Play");
    JButton pauseButton = new JButton("Pause");
    JButton tearButton = new JButton("Teardown");
    JPanel mainPanel = new JPanel();
    JPanel buttonPanel = new JPanel();
   public JLabel iconLabel = new JLabel();
    ImageIcon icon;

    Queue<Image> receivedContent;
    Timer cTimer; //timer used to receive data from the UDP socket

    VideoStream video; //Common.Stream.VideoStream object used to access video frames
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    public static int FRAME_PERIOD = 10; //Frame period of the video to stream, in ms //Para controlar a velocidade
    static int VIDEO_LENGTH = 500; //length of the video in frames
    public StreamWindow(Queue<Image> receivedContent) {
        this.receivedContent = receivedContent;
        cTimer = new Timer(20, new clientTimerListener());
        cTimer.setInitialDelay(0);
        cTimer.setCoalesce(true);
    }

    @Override
    public void run() {
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
        playButton.addActionListener(new playButtonListener());

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

    }
    class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){

            System.out.println("Play Button pressed !");
            //start the timers ...
            cTimer.start();
        }
    }

    class clientTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (receivedContent.peek() != null) {
                Image now = receivedContent.remove();
                System.out.println("Change image");
                ImageIcon icon = new ImageIcon(now);
                iconLabel.setIcon(icon);
            }
                /*
                //receive the DP from the socket:
                RTPsocket.receive(rcvdp);

                //create an Common.Stream.RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

                //print important header fields of the RTP packet received:
                System.out.println("Got RTP packet with SeqNum # "+rtp_packet.getsequencenumber()+" TimeStamp "+rtp_packet.gettimestamp()+" ms, of type "+rtp_packet.getpayloadtype());

                //print header bitstream:
                rtp_packet.printheader();

                //get the payload bitstream from the Common.Stream.RTPpacket object
                int payload_length = rtp_packet.getpayload_length();
                byte [] payload = new byte[payload_length];
                rtp_packet.getpayload(payload);

                //get an Image object from the payload bitstream
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.createImage(payload, 0, payload_length);
*/


        }
    }

    /*public void changeImage(Image now){
        //display the image as an ImageIcon object
        System.out.println("Change image");
        ImageIcon icon = new ImageIcon(now);
        iconLabel.setIcon(icon);
    }*/


}
