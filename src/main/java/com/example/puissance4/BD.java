package com.example.puissance4;


import java.sql.*;

/**
 * Classe de gestion de la connexion à une base de données Oracle.
 * Fournit des méthodes pour obtenir une connexion à la base de données.
 */

public class BD {

    // URL de connexion, nom d'utilisateur et mot de passe pour votre base de données Oracle
    private static final String URL = "jdbc:oracle:thin:@iutdoua-ora.univ-lyon1.fr:1521:cdb1";

    // Nom d'utilisateur pour la connexion à la base de données
    private static final String USER = "P1914379";

    // Mot de passe pour la connexion à la base de données
    private static final String PASSWORD = "459833";

    /**
     * Obtient une connexion à la base de données Oracle.
     *
     * @return Une instance de Connection pour interagir avec la base de données.
     * @throws SQLException Si une erreur se produit lors de la connexion à la base de données.
     */

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Vérifie si un pseudo existe déjà dans la base de données.
     *
     * @param conn La connexion à la base de données.
     * @param nom Le pseudo à vérifier.
     * @return true si le pseudo existe déjà, false sinon.
     * @throws SQLException Si une erreur se produit lors de la requête SQL.
     */
    public static boolean pseudoExists(Connection conn, String nom) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BDJAVA2 WHERE NOM = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Ajoute un nouveau pseudo dans la base de données.
     *
     * @param conn La connexion à la base de données.
     * @param nom Le pseudo à ajouter.
     * @throws SQLException Si une erreur se produit lors de l'exécution de la requête SQL.
     */
    public static void addPseudo(Connection conn, String nom) throws SQLException {
        String sql = "INSERT INTO BDJAVA2 (NOM) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nom);
            pstmt.executeUpdate();
        }
    }}


