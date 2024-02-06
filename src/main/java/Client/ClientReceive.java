package Client;

import common.Message;

import java.io.EOFException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientReceive implements Runnable {

    private Client client;
    private Socket socket;
    private ObjectInputStream in;

    /**
     * Constructeur de ClientReceive.
     *
     * @param client Le client qui reçoit les messages.
     * @param socket Le socket utilisé pour la communication réseau.
     */

    public ClientReceive(Client client, Socket socket){
        this.client = client;
        this.socket = socket;
    }

    /**
     * Méthode exécutée lors du démarrage du thread.
     * Elle écoute en continu les messages entrants du serveur et les traite.
     */
    @Override
    public void run() {
        try{
            // Initialisation du flux d'entrée
            in = new ObjectInputStream(socket.getInputStream());

            // Boucle pour écouter les messages entrants
            boolean isActive = true ;
            while(isActive) {

                // Lecture du message
                Message mess = (Message) in.readObject();
                // Traitement du message reçu
                if (mess != null) {
                    this.client.messageReceived(mess);
                } else {
                    isActive = false;
                }
            }

            // Gestion de la déconnexion du serveur
            client.disconnectedServer();
        }catch(EOFException fe){
            System.out.println(fe);
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
