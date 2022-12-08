package otherServer;

import Common.InfoNodo;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with shared variables between server threads (bootstrap, and stream).
 * For now, it doesn't have concurrency control, mas talvez não seja necessário, o problema de enviar quando não deve é baixo.
 * O contrário também não é muito grave.
 */
public class CommuncationBetweenThreads {
    // I am assuming the server has only one node conected.
    // In case the son is lost, it should be changed by te server (not implemented).
    public final InfoNodo son;
    public boolean sendStream;

    public CommuncationBetweenThreads(InfoNodo son) {
        this.sendStream = false;
        this.son = son;
    }

    public boolean getSendStream() {
        return sendStream;
    }

    public void setSendStream(boolean sendStream) {
        System.out.println("Change interess: " + sendStream);
        this.sendStream = sendStream;
    }


}
