package Client;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class ShareVariablesClient {
    private Queue<Image> receivedContent;
    private boolean play;

    public ShareVariablesClient() {
        this.receivedContent = new LinkedList<>();
        this.play=true;
    }

    public void insertImage(Image img){
        receivedContent.add(img);
    }

    public boolean haveImages(){
        return receivedContent.peek() != null;
    }

    public Image removeImage(){
        return receivedContent.remove();
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public boolean isPlay() {
        return play;
    }
}
