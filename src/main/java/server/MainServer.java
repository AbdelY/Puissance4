package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainServer {
    /**
     * Point d'entrée principal pour démarrer le serveur.
     * Le port d'écoute du serveur est spécifié en tant qu'argument de ligne de commande.
     *
     * @param args Les arguments de la ligne de commande, args[0] doit être le port du serveur.
     */

    public static void main(String[] args) {
        try {
            // Vérifie si le nombre correct d'arguments est fourni
            if (args.length != 1) {
                printUsage();
            } else {
                // Parse l'argument pour obtenir le port du serveur
                Integer port =  Integer.valueOf(args[0]);
                // Crée une nouvelle instance du serveur avec le port spécifié
                Server server = new Server(port);

            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Affiche l'utilisation correcte de la commande pour démarrer le serveur.
     * Utile en cas d'erreur dans les arguments de la ligne de commande.
     */
    private static void printUsage() {
        System.out.println("java server.Server <port>");
        System.out.println("\t<port>: server's port");
    }
}
