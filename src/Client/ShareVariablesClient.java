package Client;

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

    public ShareVariablesClient() {
        this.receivedContent = new LinkedList<>();
        this.play=true;
        this.sizeQueue = 0;
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
    public void insertImage(Image img){
        System.out.println("Insere imagem");
        receivedContent.add(img);
        sizeQueue++;
    }

    public boolean haveImages(){
        return receivedContent.peek() != null;
    }

    public Image removeImage(){
        sizeQueue--;
        System.out.println("Remove imagem");
        return receivedContent.remove();
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean isPlay() {
        return play;
    }
}
