package Common;

import java.time.LocalDateTime;

/**
 * Message id's are stored in message header, to identify the content of the message.
 */

public class Constants {

    // Still alive messages
    final public static  int sitllAliveNoInterest = 5;
    final public static  int sitllAliveWithInterest = 6;
    // Miliseconds
    final public static int timeoutSockets = 1000;
    // Seconds
    final public static int timeToConsiderNodeLost = 6;

    public static int getCurrentTime(){
        LocalDateTime date = LocalDateTime.now();
        return date.toLocalTime().toSecondOfDay();
    }

    public static String convertMessageType(int id){
        switch (id){
            case sitllAliveNoInterest:
                return "Still alive but not interested.";

            case sitllAliveWithInterest:
                return "Still alive and interested.";
            default:
                return "Type not defined";
        }
    }


    // Info about the ports, that won't change when testing in core.
    public static int portBootSendNeighbours = 1234;

    // Info about send data
    public static int arraySize = 2048;


    // Info about comunications address
    public static String serverIP = "localhost";
    public static int portServer = 4000;
    /**
     * Info of the network:
     * Distribution:
     
     */
    public static String Node1IP = "localhost";
    public static int portNode1 = 5001;

    public static String Node2IP = "localhost";
    public static int portNode2 = 5002;

    public static String Node3IP = "localhost";
    public static int portNode3 = 5003;

    public static String Node4IP = "localhost";
    public static int portNode4 = 5004;
}
