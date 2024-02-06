package server;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Classe qui gère les connexions entrantes sur un serveur.
 * Elle implémente Runnable pour permettre son exécution dans un thread séparé.
 */
public class Connection implements Runnable{

    // Le serveur associé à cette connexion
    private Server server;
    // Le ServerSocket pour accepter les connexions entrantes
    private ServerSocket serverSocket;

    /**
     * Constructeur pour la classe Connection.
     * Initialise un ServerSocket pour écouter sur le port spécifié par le serveur.
     *
     * @param server Le serveur qui utilise cette connexion.
     */

    public Connection(Server server) {
        this.server = server;
        try {
            // Création du ServerSocket pour écouter les connexions entrantes
            this.serverSocket = new ServerSocket(server.getPort());
        }catch (Exception e){
            System.out.println(e);
        }
    }


    @Override
    public void run() {
        while (true){
            try{
                Socket sockNewClient = serverSocket.accept();
                ConnectedClient newClient = new ConnectedClient(server, sockNewClient);
                newClient.setId(server.getNumClients());
                server.addClient(newClient);

                Thread threadNewClient = new Thread(newClient);
                threadNewClient.start();
            }catch (Exception e){
                System.out.println(e);
            }


        }
    }
}
