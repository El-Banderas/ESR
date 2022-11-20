package otherServer;

/**
 * Class with shared variables between server threads (bootstrap, and stream).
 * For now, it doesn't have concurrency control, mas talvez não seja necessário, o problema de enviar quando não deve é baixo.
 * O contrário também não é muito grave.
 */
public class CommuncationBetweenThreads {
    public boolean sendStream;

    public CommuncationBetweenThreads() {
        this.sendStream = false;
    }

    public boolean getSendStream() {
        return sendStream;
    }

    public void setSendStream(boolean sendStream) {
        this.sendStream = sendStream;
    }
}
