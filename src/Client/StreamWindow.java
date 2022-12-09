package Client;

import Common.Stream.ConstantesStream;
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
    JFrame f = new JFrame("Client");
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


    //Video constants:
    //------------------
    int imagenb = 0; //image nb of the image currently transmitted
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video
    static int FRAME_PERIOD = 10; //Frame period of the video to stream, in ms //Para controlar a velocidade
    static int VIDEO_LENGTH = 500; //length of the video in frames
    //private Queue<Image> receivedContent;
    private ShareVariablesClient shared;

    //--------------------------
    //Constructor
    //--------------------------
    public StreamWindow(ShareVariablesClient shared) {
        this.shared = shared;
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
        playButton.addActionListener(new StreamWindow.playButtonListener());
        pauseButton.addActionListener(new StreamWindow.pauseButtonListener());

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
        cTimer = new Timer(20, new clientTimerListener());
        cTimer.setInitialDelay(0);
        cTimer.setCoalesce(true);

    }

    //------------------------------------
    //main
    //------------------------------------
    public void run()
    {
        System.out.println("Janela começa");
    }


    //------------------------------------
    //Handler for buttons
    //------------------------------------
    public class pauseButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){

            System.out.println("Play Pause pressed !");
            shared.setPlay(false);
            //start the timers ...

        }
    }
    //Handler for Play button
    //-----------------------
    public class playButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e){

            System.out.println("Play Button pressed !");
            //start the timers ...
            cTimer.start();
            shared.setPlay(true);

        }
    }

    //------------------------------------
    //Handler for timer (para cliente)
    //------------------------------------

    /**
     * The sleep is necessary to keep the frame rate.
     */
    class clientTimerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (shared.isPlay() && shared.haveImages()){
                //System.out.println("Retira pacote");
                Image now = shared.removeImage();
                icon = new ImageIcon(now);
                iconLabel.setIcon(icon);
                try {
                    Thread.sleep(ConstantesStream.FRAME_PERIOD);
                } catch (InterruptedException error) {
                    throw new RuntimeException(error);
                }
            }

        }
    }

    //------------------------
    //Handler for timer
    //------------------------



}
