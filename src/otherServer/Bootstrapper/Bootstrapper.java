package otherServer.Bootstrapper;

import java.io.IOException;

/**
 * Main class of bootstrapper functions.
 * It start by reading the config file. Then, starts receiveng request of nodes.
 *
 * The best_paths_tree stores the best ways to get to each node. TODO: Falta fazer
 *
 *  Neste momento, a TypologyGraph e o Layout são coisas separadas, mas deviam ser a mesma.
 *  Porque ambas guardam tudo o que a topologia tem.
 *
 *  Acho que também é necessário guardar o filho atual do boot/servidor.
 *  Isso tem de ser comunicado entre boot e servidor
 *      (talvez?: https://stackoverflow.com/questions/13582395/sharing-a-variable-between-multiple-different-threads)
 */

public class Bootstrapper implements Runnable{
    // All the topology
    private TypologyGraph topology;
    private Layout topologyLayout;

    // Tree of connections
    private Tree best_paths_tree;

    public Bootstrapper() {
        this.topology = topology;
    }

    /**
     * Banderas: In this method, we sent the tree to the network, in one message.
     * It takes the tree, converts to an array of bytes, and sent to the first Node.
     */
    public void sendTree(){

    }

    @Override
    public void run() {

        // Topology
        Layout l = new Layout();
        try {
            //l.parse("otherServer/config.txt");
            // É preciso corrigir a parte de baixo :)
            l.parse("src/otherServer/config.txt");
        } catch (IOException e) {
            System.out.println("[SERVERDATA] Error in parte of config file.");
            e.printStackTrace();
        }

        // Fica à espera de enviar informações sobre vizinhos
        SendNeighbours th_SendNeighbours = new SendNeighbours(l);
        new Thread(th_SendNeighbours).start();
    }
}
