package Common;

/**
 * Message id's are stored in message header, to identify the content of the message.
 */

public class Constants {
    /**
     * BOOTSRAPPER MESSAGES and other constants
     */
    // Still alive messages
    final public static  int sitllAlive = 5;
    final public static  int streamWanted = 6;
    // Miliseconds
    final public static int timeoutSockets = 3000;
    // Miliseconds
    final public static int timeToConsiderNodeLost = 6000;

    public static double getCurrentTime(){
//        LocalDateTime date = LocalDateTime.now();
        //date.toLocalTime().toSecondOfDay();
        return System.currentTimeMillis();
    }

    public static String convertMessageType(int id){
        switch (id){
            case sitllAlive:
                return "Still alive but not interested.";

            case streamWanted:
                return "Still alive and interested.";

            default:
                return "Type not defined";
        }
    }




    // Não consigo testar, porque no mesmo computador dá sempre 0, praticamente.
    // Percentage to trigger a "Too much delay message".
    final public static int minDelayToTrigger = 40;
    final public static  int tooMuchDelay = 7;

    final public static  int lostNode = 8;


    // Info about the ports, that won't change when testing in core.
    public static int portBootSendNeighbours = 1234;

    // Info about send data
    public static int arraySize = 2048;


    /**
     * STREAM MESSAGES
     */
    final public static  int streamContent = 9;


    /**
     * Run commands of each element:
     *
     * Cliente1 : 8010 8008 8020
     * Cliente2 : 8009 8008 8021
     * Node 1: 8009 8008 8010 8020
     * Node 2: 8008 8008 8009 8021
     * Server: 8009 8008
     *
     * Client arguments: parent port // boot // this port
     * Node arguments: parent port //boot // this port // Nodos filhos
     * Server arguments: son port // this port
     *
     *           8021
     *            C2
     *            |
     * Server  -> N2  ->  N1  ->  Cl1
     *  8008    8009    8010    8020
     *
     *
     * Depois testar num nodo sem clientes
     *
     */
}
