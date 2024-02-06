package common;

import java.io.Serializable;

/**
 * Classe représentant un message dans l'application.
 * Cette classe est sérialisable, ce qui permet son envoi et sa réception via des flux d'objets.
 */

public class Message implements Serializable {
    // Propriétés du message
    private String sender;// L'expéditeur du message
    private String content;// Le contenu du message

    /**
     * Constructeur pour créer un nouveau message.
     *
     * @param sender L'expéditeur du message.
     * @param content Le contenu du message.
     */
    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
    // Getters et setters pour les propriétés du message
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Représentation sous forme de chaîne du message.
     *
     * @return Une chaîne de caractères représentant le message.
     */

    public String toString(){
        return this.sender + " - " + this.content;
    }
}
