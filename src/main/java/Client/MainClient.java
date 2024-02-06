package Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale pour démarrer un client dans une application réseau.
 * Lit l'adresse et le port du serveur à partir des arguments de la ligne de commande.
 * Cette classe est responsable de l'initialisation et du lancement du client.
 *
 * @author Remi Watrigant
 */
public class MainClient {
    /**
     * Méthode principale pour démarrer le client.
     * Crée une instance de Client avec l'adresse et le port spécifiés.
     *
     * @param args Les arguments de la ligne de commande où args[0] est l'adresse du serveur et args[1] est le port.
     */
    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                printUsage();
            } else {
                String address = args[0];
                Integer port =  Integer.valueOf(args[1]);
                Client c = new Client(address, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche l'utilisation correcte de la commande pour démarrer le client.
     * Utile en cas d'erreur dans les arguments de la ligne de commande.
     */
    private static void printUsage() {
        System.out.println("java client.Client <address> <port>");
        System.out.println("\t<address>: server's ip address");
        System.out.println("\t<port>: server's port");
    }
}
