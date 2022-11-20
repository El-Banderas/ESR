package otherServer.Stream;

import otherServer.CommuncationBetweenThreads;

import static java.lang.Thread.sleep;

public class Stream implements Runnable {
    private CommuncationBetweenThreads shared;

    public Stream(CommuncationBetweenThreads shared) {
        this.shared = shared;
    }


    @Override
    public void run() {
        System.out.println( "[Server - stream] Start");
        while(true){
            try {
                sleep(5000);
                System.out.println("[Server - Stream] Stream send? " + shared.getSendStream());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
