package Client;

import common.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


/**
 * Classe Client pour gérer la connexion réseau dans une application de jeu.
 * Cette classe est responsable de la communication avec le serveur et de la réception des mises à jour de jeu.
 */
public class Client {

    // Listener pour les mises à jour de jeu
    private GameUpdateListener gameUpdateListener;

    /**
     * Définit la vue (interface utilisateur) associée à ce client.
     *
     * @param view L'interface utilisateur à associer.
     */
    public void setView(ClientPanel view) {
        this.view = view;
    }

    /**
     * Définit le listener pour les mises à jour de jeu.
     * Ce listener sera notifié lors de la réception de mises à jour de jeu du serveur.
     *
     * @param listener Le listener à définir pour les mises à jour de jeu.
     */
    public void setGameUpdateListener(GameUpdateListener listener) {
        this.gameUpdateListener = listener;
        System.out.println("GameUpdateListener est défini: " + (listener != null)); // Cette ligne affichera si le listener est non null
    }
    // Vue associée à ce client
    public ClientPanel view;

    // Adresse et port pour la connexion au serveur
    private String address;
    private int port;
    // Socket et flux pour la communication réseau
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Constructeur pour la classe Client.
     * Ce constructeur initialise une connexion réseau avec le serveur à l'adresse et au port spécifiés.
     * Il crée également un socket pour la communication et initialise les flux d'objets pour l'envoi et la réception de données.
     * Un thread pour la réception des messages du serveur est également lancé.
     *
     * @param address L'adresse du serveur auquel se connecter.
     * @param port Le port du serveur auquel se connecter.
     */
    public Client(String address, int port){

        // Initialisation des informations de connexion
        this.address = address;
        this.port = port;
        try{
            // Création du socket pour la connexion au serveur
            this.socket = new Socket(address, port);
            // Initialisation du flux de sortie pour envoyer des messages au serveur
            out = new ObjectOutputStream(socket.getOutputStream());
        }catch(Exception e){
            System.out.println(e);
        }

        // Création et lancement d'un thread pour la réception des messages du serveur
        Thread threadClientReceive = new Thread(new ClientReceive(this, this.socket));
        threadClientReceive.start();

    }

    /**
     * Gère la déconnexion du client du serveur.
     * Cette méthode ferme les flux de communication et le socket du client.
     * Elle termine également le programme en appelant System.exit(0).
     */
    public void disconnectedServer(){
        try{
            // Ferme le flux de sortie
            this.out.close();

            // Ferme le socket de connexion
            this.socket.close();

            // Ferme le flux d'entrée s'il est non null
            if (this.in != null){
                this.in.close();
            }

            // Termine le programme
            System.exit(0);
        }catch (Exception e){
            System.out.println(e);
        }
    }


    /**
     * Traite les messages reçus par le client.
     * Cette méthode analyse le contenu des messages et effectue des actions en fonction de leur type.
     * Dans le cas des messages indiquant un coup joué ("Colonne:x"), elle notifie le GameUpdateListener.
     *
     * @param message Le message reçu à traiter.
     * @return Le message reçu après traitement.
     */
    public Message messageReceived(Message message) {
        System.out.println(message.getContent());
        String content = message.getContent();

        // Traitement pour les messages de type "Colonne:x"
        if (content.startsWith("Colonne:")) {
            try {
                // Extraction du numéro de colonne à partir du message
                int colonne = Integer.parseInt(content.split(":")[1]);

                // Notification du GameUpdateListener si non null
                if (gameUpdateListener != null) {
                    gameUpdateListener.onGameUpdate(colonne);
                } else {
                    System.out.println("GameUpdateListener est null");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return message;
    }



    /**
     * Envoie un message au serveur via le flux de sortie du socket.
     * Cette méthode est utilisée pour communiquer des informations telles que les actions de jeu au serveur.
     *
     * @param message Le message à envoyer au serveur.
     */

    public void sendMessage(Message message){
        try{
            // Écriture de l'objet message dans le flux de sortie
            this.out.writeObject(message);

            // Force l'envoi des données dans le flux
            this.out.flush();
        }
        catch (Exception e){
            // Affichage de l'exception en cas d'erreur lors de l'envoi du message
            System.out.println(e);
        }
    }
}
