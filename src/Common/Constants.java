package Common;

/**
 * Message id's are stored in message header, to identify the content of the message.
 */

public class Constants {

    // msg HELLO

    final public static  int hellomesage = 10;
    final public  static int timeStamp = 22;
    final public static int ConnectionMsg = 100;
    final public static int XMLmsg = 101;


    /**
     * BOOTSRAPPER MESSAGES and other constants
     */
    // Still alive messages
    final public static  int sitllAlive = 5;
    final public static  int sendNeibourghs = 33;
    /**
     * STREAM MESSAGES
     */
    final public static  int streamWanted = 6;

    final public static  int streamContent = 9;
    final public static  int tooMuchDelay = 7;


    final public static  int lostNode = 8;
final public static int StillAliveBootAlt = 23;
final public static int helloAltBoot = 24;
final public static int helloClient = 26;
final public static int impossibleConnection = 27;
final public static int wakeUpClient = 28;
final public static int changeTree = 25;

    // Miliseconds
    final public static int timeoutSockets = 2000;
    // Miliseconds
    final public static int timeToConsiderNodeLost = 10000; // 1 minute

final public static    int sizeInetAdressByteArray = 4;

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

            case hellomesage:
                return "Node Connected";

            default:
                return "Type not defined";
        }
    }




    // Não consigo testar, porque no mesmo computador dá sempre 0, praticamente.
    // Percentage to trigger a "Too much delay message".
    final public static int minDelayToTrigger = 40;


    // Info about the ports, that won't change when testing in core.
    public static int portBootSendNeighbours = 1234;

    // Info about send data0
    public static int arraySize = 150000;


    public static boolean Windows = true ;


    public static  int portNet = 9000;
    public static  int portStream = 9001;





    /**
     *
     *
     * nodo 1 : 8009 8008 8010
     * nodo 2 : 8008 8008 8009 8010
     * server : 8009 8008
     * Run commands of each element:
     *
     * Cliente1 : 8011 8008 8020
     * Cliente2 : 8009 8008 8021
     * Node 1: 8009 8008 8011 8012 8020 8020
     * Node 2: 8008 8008 8009 8010 8021 8021 8011 8012
     * Server: 8009 8010 8008
     *
     * Client arguments: parent port // boot // this port
     * Node arguments: parent port (net) //boot // this port (net) // this port (stream) // Nodos filhos (par porta net/stream)
     * Server arguments: son port (net) // son port (stream) // this port
     *
     *           8021
     *            C2
     *            |
     * Server   ->  N2    ->   N1  ->  Cl1
     *  8008     8009/8010   8011/8012    8020
     *
     *  net/stream
     * Depois testar num nodo sem clientes
     *
     */
}
