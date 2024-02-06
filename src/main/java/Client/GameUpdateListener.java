package Client;

/**
 * Interface pour écouter les mises à jour du jeu.
 * Cette interface définit une méthode que les classes implémentant cette interface doivent fournir.
 * Elle est utilisée pour notifier les changements ou les mises à jour dans le jeu, par exemple, lorsqu'un coup est joué.
 */
public interface GameUpdateListener {

    /**
     * Méthode appelée lorsqu'une mise à jour du jeu est reçue.
     * Par exemple, cette méthode peut être appelée pour notifier qu'un coup a été joué dans une colonne spécifique.
     *
     * @param colonne L'indice de la colonne où le coup a été joué.
     */
    void onGameUpdate(int colonne);
}
