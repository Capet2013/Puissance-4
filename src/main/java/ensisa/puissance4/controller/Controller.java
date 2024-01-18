package ensisa.puissance4.controller;

import ensisa.puissance4.model.Model;
import ensisa.puissance4.view.View;

public class Controller {
    // Déclaration des variables et des constantes
    private final Model model;
    private final View view;

    // Constructeur et initialisation
    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
    }

    public void initialize() {
        view.setModel(model);
        view.initialize();
    }

    // Méthode de gestion des événements afin de dérouler le jeu
    public void game(){
        if (!model.checkWinner()) {
            if (!model.boardFull()) {
                // Si c'est au tour du joueur, on ajoute un jeton et on passe au tour de l'ordinateur
                if (model.getPlayerTurn()) {
                    model.addJeton();
                    view.disableButtons();
                    model.switchPlayerTurn();
                    view.updateTurnLabel();
                    makeComputerMove();
                // Si c'est au tour de l'ordinateur, on ajoute un jeton et on passe au tour du joueur
                } else {
                    model.addJeton();
                    model.switchPlayerTurn();
                    view.enableButtons();
                    view.updateTurnLabel();
                }
            // Si le plateau est plein, on affiche un message de match nul
            } else {
                view.clearTurnLabel();
                view.disableButtons();
                view.displayDraw();
            }
        // Si un joueur a gagné, on affiche un message de victoire
        } else {
                view.clearTurnLabel();
                view.disableButtons();
                view.displayWinner(model.getPlayerTurn() ? 1 : 2);
        }
    }

    // Méthode pour gérer les événements des boutons
    public void handleButtonAction(int column) {
        int row = model.findLowestEmptyRow(column);

        if (row < model.getRows()) {
            view.addToken(column, row, model.getPlayerTurn());
            model.addToken(column, row);
            game();
        }
    }

    // Méthode pour gérer les événements du bouton de démarrage
    public void handleStartButtonAction() {
        view.enableButtons();
        view.clearTurnLabel();
        view.clearGrid();
        view.initialize();
        model.clearGrid();
        model.initialize();
        view.startButton.setText("Recommencer");
        view.startButton.getParent().requestLayout();
        game();
    }

    // Méthode pour la gestion du tour de l'ordinateur
    public void makeComputerMove() {
        int result = model.BestMove(model);
        int row = model.findLowestEmptyRow(result);
        view.addToken(result, row, model.getPlayerTurn());
        model.addToken(result, row);
        game();
    }

}
