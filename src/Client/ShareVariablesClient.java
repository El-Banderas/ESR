package Client;

import Common.Constants;
import Common.Stream.ConstantesStream;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class ShareVariablesClient {

    // We store in a queue, because it is FIFO
    private final Queue<Image> receivedContent;
    private int sizeQueue;

    // If the stream is showing or is in pause.
    private boolean play;
    private double sizePackets;
    private double lastMeasure;

    public ShareVariablesClient() {
        this.receivedContent = new LinkedList<>();
        this.play=true;
        this.sizeQueue = 0;
        this.sizePackets = 0;
        lastMeasure = Constants.getCurrentTime();
    }

    /**
     * This function is used when we pause the video, and we drop packets.
     * So, we drop the oldest image, and insert the new one.
     * This way, we keep the buffer updated.
     *
     * And, if the buffer isn't full, only adds the image.
     * @param img
     */
    public void replaceImage(Image img){
        if (sizeQueue < ConstantesStream.maxSizeBuffer){
            receivedContent.add(img);
        }
        else {
            receivedContent.add(img);
            receivedContent.remove();
        }
    }
    public void insertImage(Image img, int sizePacket){
        receivedContent.add(img);
        sizeQueue++;
        sizePackets += sizePacket;
    }

    public boolean haveImages(){
        return receivedContent.peek() != null;
    }

    public Image removeImage(){
        sizeQueue--;
        return receivedContent.remove();
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean isPlay() {
        return play;
    }

    public double getDebit(){
        double currentTime = Constants.getCurrentTime();
        double lastTime = this.lastMeasure;
        double oldsizePackets = sizePackets;
        lastMeasure = Constants.getCurrentTime();
        sizePackets = 0;
        return oldsizePackets/(currentTime-lastTime);

    }
}
