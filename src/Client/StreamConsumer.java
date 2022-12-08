package Client;

import javax.swing.*;
import java.awt.*;
import java.util.Queue;
import java.util.TimerTask;

public class StreamConsumer extends TimerTask {
    Queue<Image> receivedContent;
    JLabel iconLabel;

    public StreamConsumer(Queue<Image> receivedContent, JLabel iconLabel) {
        this.receivedContent = receivedContent;
        this.iconLabel = iconLabel;
    }

    @Override
    public void run() {
        if (receivedContent.peek() != null) {
            Image now = receivedContent.remove();
            //display the image as an ImageIcon object
            ImageIcon icon = new ImageIcon(now);
            iconLabel.setIcon(icon);
        }
    }
}
