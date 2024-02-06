package server;

import common.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Classe représentant un client connecté au serveur.
 * Cette classe gère la communication avec un client spécifique dans un contexte de serveur.
 * Elle implémente Runnable pour permettre son exécution dans un thread séparé.
 */
public class ConnectedClient implements Runnable{
    // Un compteur statique pour attribuer des identifiants uniques à chaque client
    private static int idCounter = 0;

    // L'identifiant unique de ce client
    private int id;

    // Le serveur auquel ce client est connecté
    private Server server;

    // Le socket pour la communication avec le client
    private Socket socket;

    // Les flux d'entrée et de sortie pour la communication objet
    private ObjectOutputStream out;
    private ObjectInputStream in;


    /**
     * Constructeur pour créer un nouveau client connecté.
     *
     * @param server Le serveur auquel le client est connecté.
     * @param socket Le socket pour la communication avec le client.
     */
    public ConnectedClient(Server server, Socket socket) {
        // Incrémentation du compteur d'ID pour chaque nouvelle instance
        idCounter++;
        this.id = idCounter;
        this.socket = socket;
        this.server = server;
        try{
            // Initialisation du flux de sortie pour envoyer des objets au client
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Nouvelle connexion, id = " + id);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try{
            // Initialisation du flux d'entrée pour recevoir des objets du client
            in = new ObjectInputStream(socket.getInputStream());

            // Boucle pour écouter les messages entrants du client
            boolean isActive = true;
            while (isActive) {
                Message mess = (Message) in.readObject();
                mess.setSender(String.valueOf(id));
                if (mess != null){
                    server.broadcastMessage(mess, id);
                }else {
                    server.disconnectedClient(this);
                    isActive = false;
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * Méthodes d'accès pour la classe ConnectedClient.
     * Ces méthodes fournissent des moyens pour obtenir et définir les valeurs des attributs de l'instance.
     */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Envoie un message au client connecté.
     * Cette méthode utilise le flux de sortie pour envoyer un objet Message sérialisé au client.
     *
     * @param mess Le message à envoyer.
     */

    public void sendMessage(Message mess){
        try{
            // Écriture de l'objet message dans le flux de sortie
            this.out.writeObject(mess);

            // Force l'envoi des données dans le flux
            this.out.flush();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * Ferme proprement les ressources réseau du client.
     * Cette méthode ferme le flux d'entrée, le flux de sortie et le socket.
     */
    public void closeClient(){
        try{
            // Fermeture général
            this.in.close();
            this.out.close();
            this.socket.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
