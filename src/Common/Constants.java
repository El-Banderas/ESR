package Common;

/**
 * Message id's are stored in message header, to identify the content of the message.
 */

public class Constants {

    // Still alive messages
    public static int sitllAliveID = 5;
    public static int timeoutSockets = 5;


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
