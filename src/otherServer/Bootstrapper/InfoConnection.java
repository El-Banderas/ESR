package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;

public class InfoConnection implements Comparable<InfoConnection> {
    public InfoNodo otherNode;
    public double delay;

    // Conection info
    public double timeLastMessage;
    public boolean isAlive;
    public boolean interested ;

    public InfoConnection(InfoNodo otherNode, double delay, double timeLastMessage, boolean interested) {
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

        double now = Constants.getCurrentTime();
        if (now-timeLastMessage > Constants.timeToConsiderNodeLost){
//            System.out.println("A diferença de tempos é: "+ );
            isAlive = false;
        }
        double differenceTime = now - timeLastMessage;
        if (!isAlive)
        System.out.println("O nodo é descartado, diferença de tempos: " + differenceTime + " e o máximo é: " + Constants.timeToConsiderNodeLost);
        else
            System.out.println("o nodo é válido, diferença de tempos: " + differenceTime + " e o máximo é: " + Constants.timeToConsiderNodeLost);

        return isAlive;
    }

    @Override
    public String toString() {
        return "InfoConnection{" +
                "otherNode=" + otherNode.ip + " and port " + otherNode.portNet +
                ", delay=" + delay +
                ", timeLastMessage=" + timeLastMessage +
                ", isAlive=" + isAlive +
                ", interested=" + interested +
                '}';
    }


    public int compareTo(InfoConnection that){
        return (int) (this.delay - that.delay);
    }



}

