package server;

import common.Message;

import java.util.ArrayList;


/**
 * Classe représentant un serveur dans l'application.
 * Cette classe est responsable de l'initialisation du serveur et de la gestion des clients connectés.
 */
public class Server {
    // Le port sur lequel le serveur écoute
    private int port;

    // Liste pour garder une trace des clients connectés
    private ArrayList<ConnectedClient> clients;

    /**
     * Constructeur pour le serveur.
     * Initialise le serveur et commence à écouter les connexions entrantes.
     *
     * @param port Le port sur lequel le serveur doit écouter.
     */

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<ConnectedClient>();

        // Création et démarrage d'un thread pour gérer les connexions entrantes
        Thread threadConnection = new Thread(new Connection(this));
        threadConnection.start();
    }

    /**
     * Obtient le port sur lequel le serveur écoute.
     *
     * @return Le numéro de port.
     */
    public int getPort() {
        return port;
    }


    /**
     * Définit le port sur lequel le serveur doit écouter.
     *
     * @param port Le numéro de port à définir.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Obtient la liste des clients connectés au serveur.
     *
     * @return Une ArrayList de ConnectedClient.
     */

    public ArrayList<ConnectedClient> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ConnectedClient> clients) {
        this.clients = clients;
    }

    /**
     * Obtient le nombre de clients actuellement connectés au serveur.
     *
     * @return Le nombre de clients connectés.
     */
    public int getNumClients(){
        return this.clients.size();
    }

    /**
     * Ajoute un nouveau client à la liste des clients connectés et envoie un message de notification.
     *
     * @param newClient Le client à ajouter.
     */
    public void addClient(ConnectedClient newClient){
        this.clients.add(newClient);
        broadcastMessage(new Message("",newClient.getId() + " vient de se connecter "),newClient.getId());
    }

    /**
     * Envoie un message à tous les clients connectés, à l'exception de l'expéditeur.
     *
     * @param mess Le message à diffuser.
     * @param id L'identifiant du client expéditeur (qui ne recevra pas le message).
     */
    public void broadcastMessage(Message mess, int id){
        for (ConnectedClient client : clients) {
            // Vérifie pour ne pas renvoyer le message à l'expéditeur
            if (client.getId() != id) {
                client.sendMessage(mess);
            }
        }
    }


    /**
     * Gère la déconnexion d'un client.
     * Ferme les ressources associées au client et le retire de la liste des clients connectés.
     * Envoie également une notification aux autres clients concernant la déconnexion.
     *
     * @param disClient Le client qui se déconnecte.
     */
    public void disconnectedClient(ConnectedClient disClient){
        // Ferme les ressources réseau du client
        disClient.closeClient();
        // Retire le client de la liste des clients connectés
        this.clients.remove(disClient.getId());
        // Informe les autres clients de la déconnexion
        broadcastMessage(new Message("",disClient.getId() + " vient de se deconnecter "), disClient.getId());
    }
}
