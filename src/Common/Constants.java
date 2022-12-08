package Common;

import java.time.LocalDateTime;

/**
 * Message id's are stored in message header, to identify the content of the message.
 */

public class Constants {

    // msg HELLO

    final public static  int hellomesage = 10;

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

            case hellomesage:
                return "Node Connected";

            default:
                return "Type not defined";
        }
    }

    // Não consigo testar, porque no mesmo computador dá sempre 0, praticamente.
    final public static int minDelayToTrigger = 3;
    final public static  int tooMuchDelay = 7;


    // Info about the ports, that won't change when testing in core.
    public static int portBootSendNeighbours = 1234;

    // Info about send data
    public static int arraySize = 2048;


    /**
     * Run commands of each element:
     *
     * Cliente1 : 8010 8020
     * Cliente2 : 8009 8021
     * Node 1: 8009 8010 -1
     * Node 2: 8008 8009 -1
     * Server: 8009 8008
     *
     * Client arguments: parent port // this port
     * Node arguments: parent port // this port // Nodos filhos
     * Server arguments: son port // this port
     *
     *
     *           8021
     *            C2
     *            |
     * Server  -> N2  ->  N1  ->  Cl1
     *  8008    8009    8010    8020
     */
}
