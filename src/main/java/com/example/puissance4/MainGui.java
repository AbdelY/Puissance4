package com.example.puissance4;

import Client.Client;
import Client.ClientPanel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale pour l'interface graphique de l'application client.
 * Elle étend la classe Application de JavaFX.
 */


public class MainGui extends Application {

    /**
     * Démarre l'interface graphique utilisateur.
     * Cette méthode est appelée automatiquement lors du lancement de l'application JavaFX.
     *
     * @param stage Le stage principal pour cette application.
     * @throws IOException Si une erreur d'entrée/sortie se produit.
     */

    @Override
    public void start(Stage stage) throws IOException {
        ClientPanel clientPanel = new ClientPanel();
        Group root = new Group();
        root.getChildren().add(clientPanel);
        Scene scene = new Scene(root, 600, 800);
        stage.setTitle("Mon application");
        stage.setScene(scene);
        stage.show();

        Client client = new Client("127.0.0.1", 5000);
        client.setView(clientPanel);
        clientPanel.setClient(client);
    }

    /**
     * Méthode principale pour lancer l'application.
     *
     * @param args Les arguments de la ligne de commande.
     */

    public static void main(String[] args) {
        Application.launch(MainGui.class, args);
    }
}