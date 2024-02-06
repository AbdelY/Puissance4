package com.example.puissance4;



import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import Client.GameUpdateListener;
import Client.Client;
import common.Message;
import Client.ClientPanel;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Cette classe représente le point d'entrée principal d'une application JavaFX.
 * Elle étend la classe Application de JavaFX et implémente l'interface GameUpdateListener.
 * Cette application gère un jeu de Puissance 4 avec une interface graphique.
 */

public class Main extends Application implements GameUpdateListener{

    // Constantes pour la configuration du jeu
    private static final int COLONNES = 7;
    private static final int LIGNES = 6;
    private static final int TAILLE_CASE = 100;

    // Grille de jeu représentée par un tableau de cercles
    private  Circle[][] grille = new Circle[COLONNES][LIGNES];

    // Variable pour suivre le joueur actuel
    private int joueurActuel = 1;

    // Client pour la gestion de la connexion réseau
    private Client client;

    // Panneau d'interface client
    private  ClientPanel clientPanel;

    /**
     * Méthode principale pour démarrer l'application JavaFX.
     * Cette méthode initialise l'interface utilisateur pour le démarrage du jeu.
     *
     * @param primaryStage Le stage principal pour cette application.
     */

    @Override
    public void start(Stage primaryStage) {
        // Création de l'interface utilisateur
        VBox root = new VBox(10);
        TextField pseudoField = new TextField();
        pseudoField.setPromptText("Entrez votre pseudo");
        Button startButton = new Button("Lancer");
        startButton.setOnAction(e -> commencerJeu(primaryStage, pseudoField.getText()));

        root.getChildren().addAll(pseudoField, startButton);
        Scene scene = new Scene(root, 300, 200);

        // Tentative de connexion à la base de données
        try {
            Connection conn = BD.getConnection();
            System.out.println("Connexion réussie à la base de données !");
            // Vous pouvez fermer la connexion ici ou la garder ouverte selon vos besoins
            // conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            return; // Arrête l'exécution si la connexion échoue
        }

        // Configuration et affichage de la fenêtre principale
        primaryStage.setTitle("Puissance 4 - Saisie du Pseudo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Méthode pour initialiser et démarrer le jeu de Puissance 4.
     * Cette méthode configure l'interface graphique du jeu et établit la connexion réseau.
     * Elle crée également la grille de jeu et gère les interactions avec la base de données.
     *
     * @param primaryStage Le stage principal sur lequel la scène du jeu sera définie.
     * @param pseudo Le pseudo du joueur qui sera utilisé dans le jeu.
     */
    private void commencerJeu(Stage primaryStage, String pseudo) {
        // Configuration de la scène de jeu
        Pane pane = new Pane();
        Scene scene = new Scene(pane, COLONNES * TAILLE_CASE, LIGNES * TAILLE_CASE);

        // Configuration du client et du panel client pour la gestion de la connexion réseau
        clientPanel = new ClientPanel();
        client = new Client("127.0.0.1", 5000);
        client.setGameUpdateListener(this);
        client.setView(clientPanel);
        clientPanel.setClient(client);
        HBox root = new HBox(pane, clientPanel);
        Scene scene2 = new Scene(root, 1200, 600);

        // Création de la grille
        for (int i = 0; i < LIGNES; i++) {
            for (int j = 0; j < COLONNES; j++) {
                Rectangle rect = new Rectangle(j * TAILLE_CASE, i * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
                rect.setFill(Color.WHITE);
                rect.setStroke(Color.BLACK);

                Circle cercle = new Circle(j * TAILLE_CASE + TAILLE_CASE / 2, i * TAILLE_CASE + TAILLE_CASE / 2, TAILLE_CASE / 2 - 10);
                cercle.setFill(Color.TRANSPARENT);

                final int colonne = j;
                cercle.setOnMouseClicked(e -> jouer(colonne));

                grille[j][i] = cercle;
                pane.getChildren().addAll(rect, cercle);
            }
        }

        // Interaction avec la base de données pour gérer le pseudo
        try {
            Connection conn = BD.getConnection();

            if (BD.pseudoExists(conn, pseudo)) {
                System.out.println("Re-bonjour " + pseudo);
            } else {
                BD.addPseudo(conn, pseudo);
                System.out.println("Bienvenue " + pseudo);
            }
            // Fermez la connexion si vous ne l'utilisez plus
            conn.close();
        } catch (SQLException e) {
            System.out.println("Erreur de base de données : " + e.getMessage());
            return; // En cas d'erreur, arrêtez l'exécution
        }

        // Mise en place et affichage de la scène de jeu
        primaryStage.setTitle("Puissance 4");
        primaryStage.setScene(scene2);
        primaryStage.show();
    }


    /**
     * Méthode pour gérer un coup reçu dans le jeu de Puissance 4.
     * Cette méthode est appelée lorsqu'un coup est joué par l'autre joueur (via le réseau, par exemple).
     * Elle met à jour la grille de jeu avec le coup reçu et vérifie si ce coup conduit à une victoire.
     *
     * @param colonne L'indice de la colonne où le coup a été joué.
     */
    private void jouerCoupRecu(int colonne) {
        System.out.println("jouerCoupRecu appelé pour la colonne: " + colonne);

        // Parcours des lignes de la colonne, de bas en haut
        // Met à jour la couleur du cercle pour indiquer le coup du joueur
        for (int i = LIGNES - 1; i >= 0; i--) {
            if (grille[colonne][i].getFill() == Color.TRANSPARENT) {
                grille[colonne][i].setFill(joueurActuel == 1 ? Color.RED : Color.YELLOW);
                System.out.println("Coup joué à la colonne " + colonne + ", ligne " + i);

                // Vérifier si ce coup entraîne une victoire
                if (verifierGagnant(colonne, i, joueurActuel)) {
                    System.out.println("Le joueur " + (joueurActuel == 1 ? "rouge" : "jaune") + " a gagné ! Partie finie.");
                    // Gestion de la fin du jeu
                }

                // Change de 'joueurActuel' ici car cette méthode est pour les coups reçus
                joueurActuel = joueurActuel == 1 ? 2 : 1;
                break;
            }
        }
    }

    /**
     * Méthode pour gérer un coup joué par le joueur local dans le jeu de Puissance 4.
     * Elle est appelée lorsqu'un joueur clique sur une colonne pour y placer son jeton.
     * La méthode met à jour la grille de jeu avec le coup joué et vérifie si ce coup conduit à une victoire.
     * Elle envoie également le coup joué au serveur pour informer l'autre joueur.
     *
     * @param colonne L'indice de la colonne où le coup est joué.
     */
    private void jouer(int colonne) {

        // Parcours des lignes de la colonne, de bas en haut
        System.out.println("jouer appelé pour la colonne: " + colonne);
        for (int i = LIGNES - 1; i >= 0; i--) {
            // Vérifie si la cellule est vide (transparente)
            if (grille[colonne][i].getFill() == Color.TRANSPARENT) {
                // Met à jour la couleur du cercle pour indiquer le coup du joueur
                grille[colonne][i].setFill(joueurActuel == 1 ? Color.RED : Color.YELLOW);

                // Vérifie si ce coup entraîne une victoire
                if (verifierGagnant(colonne, i, joueurActuel)) {
                    System.out.println("Le joueur " + (joueurActuel == 1 ? "rouge" : "jaune") + " a gagné ! Partie finie.");
                }

                // Change le joueur actuel pour le prochain tour
                joueurActuel = joueurActuel == 1 ? 2 : 1;
                break;
            }
        }

        // Envoie le coup joué au serveur pour qu'il soit communiqué à l'autre joueur
        envoyerActionAuServeur(colonne);
    }


    /**
     * Méthode pour envoyer une action de jeu au serveur.
     * Elle est utilisée pour communiquer les coups joués par le joueur local au serveur,
     * qui les transmet ensuite à l'autre joueur dans un contexte de jeu en réseau.
     *
     * @param val L'indice de la colonne où le coup a été joué.
     */
    private void envoyerActionAuServeur(int val) {
        // Identifiant ou type de message pour différencier les différents types de communications
        String sender = "TypeDeMessageOuIdentifiantJoueur";
        // Contenu du message, ici l'indice de la colonne jouée
        String content = "Colonne:" + val;

        // Création d'un objet Message avec les informations du coup joué
        Message messageJeu = new Message(sender, content);
        // Envoi du message au serveur via le client
        client.sendMessage(messageJeu);
    }

    /**
     * Méthode appelée lorsqu'une mise à jour du jeu est reçue, typiquement lorsqu'un coup est joué par l'autre joueur.
     * Cette méthode fait partie de l'implémentation de l'interface GameUpdateListener.
     * Elle assure que les mises à jour du jeu sont traitées dans le thread approprié de l'interface graphique JavaFX.
     *
     * @param colonne L'indice de la colonne où le coup a été joué par l'autre joueur.
     */
    @Override
    public void onGameUpdate(int colonne) {
        System.out.println("onGameUpdate appelé pour la colonne: " + colonne);
        // Utilisation de Platform.runLater pour assurer que l'interaction avec l'interface graphique se fait sur le thread JavaFX
        Platform.runLater(() -> jouerCoupRecu(colonne));
    }


    /**
     * Vérifie si le coup joué conduit à une victoire.
     * Cette méthode parcourt la grille de jeu pour détecter une séquence de quatre jetons consécutifs de la même couleur.
     * Elle vérifie horizontalement, verticalement et diagonalement dans les deux sens.
     *
     * @param colonneJouee L'indice de la colonne où le coup a été joué.
     * @param ligneJouee L'indice de la ligne où le coup a été joué.
     * @param couleurJoueur Le numéro du joueur (1 ou 2) pour déterminer la couleur du jeton.
     * @return true si le coup conduit à une victoire, sinon false.
     */

    private boolean verifierGagnant(int colonneJouee, int ligneJouee, int couleurJoueur) {
        Color couleur = couleurJoueur == 1 ? Color.RED : Color.YELLOW;

        // Vérification horizontale
        for (int col = 0; col < COLONNES - 3; col++) {
            if (grille[col][ligneJouee].getFill().equals(couleur) &&
                    grille[col + 1][ligneJouee].getFill().equals(couleur) &&
                    grille[col + 2][ligneJouee].getFill().equals(couleur) &&
                    grille[col + 3][ligneJouee].getFill().equals(couleur)) {
                return true;
            }
        }

        // Vérification verticale
        for (int lig = 0; lig < LIGNES - 3; lig++) {
            if (grille[colonneJouee][lig].getFill().equals(couleur) &&
                    grille[colonneJouee][lig + 1].getFill().equals(couleur) &&
                    grille[colonneJouee][lig + 2].getFill().equals(couleur) &&
                    grille[colonneJouee][lig + 3].getFill().equals(couleur)) {
                return true;
            }
        }

        // Vérification diagonale (de haut en bas, de gauche à droite)
        for (int col = 0; col < COLONNES - 3; col++) {
            for (int lig = 0; lig < LIGNES - 3; lig++) {
                if (grille[col][lig].getFill().equals(couleur) &&
                        grille[col + 1][lig + 1].getFill().equals(couleur) &&
                        grille[col + 2][lig + 2].getFill().equals(couleur) &&
                        grille[col + 3][lig + 3].getFill().equals(couleur)) {
                    return true;
                }
            }
        }

        // Vérification diagonale (de bas en haut, de gauche à droite)
        for (int col = 0; col < COLONNES - 3; col++) {
            for (int lig = 3; lig < LIGNES; lig++) {
                if (grille[col][lig].getFill().equals(couleur) &&
                        grille[col + 1][lig - 1].getFill().equals(couleur) &&
                        grille[col + 2][lig - 2].getFill().equals(couleur) &&
                        grille[col + 3][lig - 3].getFill().equals(couleur)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Point d'entrée principal pour l'application JavaFX.
     * Cette méthode lance l'application JavaFX en appelant la méthode launch héritée de la classe Application.
     *
     * @param args Les arguments de ligne de commande passés au programme.
     */

    public static void main(String[] args) {
        launch(args);
    }
}