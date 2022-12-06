package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;

import java.time.LocalDateTime;

public class InfoConnection {
    public InfoNodo otherNode; // proximo node
    public int delay;

    // Conection info
    public int timeLastMessage;
    public boolean isAlive;
    public boolean interested ;

    public InfoConnection(InfoNodo otherNode, int delay, int timeLastMessage, boolean interested) {
        this.otherNode = otherNode;
        this.delay = delay;
        this.timeLastMessage = timeLastMessage;
        this.interested = interested;
        this.isAlive = true;
    }
/*
    public void updateTimeLastConnection(int time){
        timeLastMessage = time;
    }

    public void setInteress(boolean update){
        interested = update;
    }
*/




    /**
     * Se calhar dá para usar apenas o LocalDateTime, experimentar
     * Checks if the node is alive
     * @return
     */
    public boolean isAliveTimeout(){

        int now = Constants.getCurrentTime();
        if (now-timeLastMessage > Constants.timeToConsiderNodeLost){
//            System.out.println("A diferença de tempos é: "+ );
            isAlive = false;
        }
        int differenceTime = now - timeLastMessage;
        if (!isAlive)
        System.out.println("A mensagem é descartada, diferença de tempos: " + differenceTime + " e o máximo é: " + Constants.timeToConsiderNodeLost);
        else
            System.out.println("A mensagem é válida, diferença de tempos: " + differenceTime + " e o máximo é: " + Constants.timeToConsiderNodeLost);

        return isAlive;
    }

    @Override
    public String toString() {
        return "InfoConnection{" +
                "otherNode=" + otherNode.ip + " and port " + otherNode.port +
                ", delay=" + delay +
                ", timeLastMessage=" + timeLastMessage +
                ", isAlive=" + isAlive +
                ", interested=" + interested +
                '}';
    }
}
