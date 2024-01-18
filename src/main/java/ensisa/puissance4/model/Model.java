package ensisa.puissance4.model;

import javafx.scene.layout.Pane;
import java.util.Random;

public class Model {
    // Déclaration des variables d'instance
    private final Pane rootPane;
    private final int rows = 6;
    private final int columns = 7;
    public boolean[] column = new boolean[columns];//true = colonne pleine
    private int jetons = 0;
    private final int[][] grid = new int[columns][rows];//0 = case vide, 1 = joueur, 2 = ordinateur
    private boolean playerTurn;
    // Initialisation du modèle
    public Model(Pane rootPane) {
        this.rootPane = rootPane;
    }
    public void initialize() {
        createGrid();
        Random rand = new Random();
        playerTurn = rand.nextBoolean();
    }

    // Méthode pour créer la grille
    private void createGrid(){
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows ; j++) {
                grid[i][j] = 0;
            }
            column[i] = false;//true = colonne pleine
        }
    }

    // Méthode pour vérifier si un joueur a gagné
    public boolean checkWinner() {
        // Vérification horizontale
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - 3; col++) {
                if (checkSequence(grid[col][row], grid[col + 1][row], grid[col + 2][row], grid[col + 3][row])) {
                    return true;
                }
            }
        }

        // Vérification verticale
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows - 3; row++) {
                if (checkSequence(grid[col][row], grid[col][row + 1], grid[col][row + 2], grid[col][row + 3])) {
                    return true;
                }
            }
        }

        // Vérification diagonale ascendante (de gauche à droite)
        for (int col = 0; col < columns - 3; col++) {
            for (int row = 0; row < rows - 3; row++) {
                if (checkSequence(grid[col][row], grid[col + 1][row + 1], grid[col + 2][row + 2], grid[col + 3][row + 3])) {
                    return true;
                }
            }
        }

        // Vérification diagonale descendante (de gauche à droite)
        for (int col = 0; col < columns - 3; col++) {
            for (int row = 3; row < rows; row++) {
                if (checkSequence(grid[col][row], grid[col + 1][row - 1], grid[col + 2][row - 2], grid[col + 3][row - 3])) {
                    return true;
                }
            }
        }

        // Vérification s'il y a égalité
        if(jetons == 42){
            return true;
        }

        return false;
    }

    // Méthode pour vérifier si une séquence de 4 jetons est gagnante
    private boolean checkSequence(int... values) {
        for (int i = 0; i < values.length - 1; i++) {
            if (values[i] == 0 || values[i] != values[i + 1]) {
                return false;
            }
        }
        return true;
    }

    // Méthode pour trouver la première case vide d'une colonne
    public int findLowestEmptyRow(int column) {
        for (int j = rows -1; j >= 0; j--) {
            if (grid[column][j] == 0) {
                if(j == 0){
                    this.column[column] = true;
                }
                return j;
            }
        }
        return 10;
    }

    // Méthode getter et setter
    public int getRows() {
        return rows;
    }

    public boolean getPlayerTurn() {
        return playerTurn;
    }

    public void addToken(int column, int row) {
        grid[column][row] = playerTurn ? 1 : 2;
    }

    public void switchPlayerTurn() {
        playerTurn = !playerTurn;
    }

    public void addJeton() {
        jetons++;
    }

    // Méthode pour vérifier si la grille est pleine
    public boolean boardFull(){
        for(int i = 0, j = 0; i < columns; i++){
            if(column[i]){
                j++;
            }
            if(j == columns){
                return true;
            }
        }
        return false;
    }

    // Méthode pour vider la grille afin de recommencer une partie
    public void clearGrid() {
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows ; j++) {
                grid[i][j] = 0;
            }
            column[i] = false;
        }
        jetons = 0;
    }

    // Méthode pour faire jouer l'ordinateur de manière aléatoire
    public int randomMove(){
        Random rand = new Random();
        int col = rand.nextInt(columns);
        if(!column[col]){
            return col;
        } else {
            return randomMove();
        }
    }

    // Méthode pour copier la grille afin de pouvoir faire des simulations pour anticiper les coups
    public Model copy() {
        Model copy = new Model(rootPane);
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows ; j++) {
                copy.grid[i][j] = grid[i][j];
            }
            copy.column[i] = column[i];
        }
        copy.jetons = jetons;
        copy.playerTurn = playerTurn;
        return copy;
    }

    // Méthode pour faire jouer l'ordinateur de manière intelligente
    public int BestMove(Model model) {
        int bestScore = -100000;
        int bestMove = 0;
        for(int i = 0; i < columns; i++){
            if(!column[i]){
                Model copy = model.copy();
                copy.addToken(i, copy.findLowestEmptyRow(i));
                int score = copy.minimax(copy,6, false, -100000, 100000);
                if(score > bestScore){
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    // Algorithm minimax avec élagage alpha-bêta
    public int minimax(Model model, int depth, boolean isMaximizing, int alpha, int beta) {
        if(checkWinner()){
            return evaluate(model)*depth;
        }
        if(depth == 0){
            return evaluate(model);
        }

        if(isMaximizing){
            int bestScore = -100000;
            for(int i = 0; i < columns; i++) {
                int row = model.findLowestEmptyRow(i);
                if (row < rows) {
                    Model copy = model.copy();
                    copy.addToken(i, copy.findLowestEmptyRow(i));
                    int score = copy.minimax(model, depth - 1, false, alpha, beta)*(depth);
                    bestScore = Math.max(score, bestScore);
                    alpha = Math.max(alpha, bestScore);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return bestScore;

        } else {
            int bestScore = 100000;
            for(int i = 0; i < columns; i++){
                int row = model.findLowestEmptyRow(i);
                if(row < rows){
                    Model copy = model.copy();
                    copy.addToken(i, copy.findLowestEmptyRow(i));
                    int score = copy.minimax(model,depth - 1, true, alpha, beta)*(depth);
                    bestScore = Math.min(score, bestScore);
                    if (bestScore < beta) {
                        beta = bestScore;
                    }
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
            return bestScore;
        }
    }

    // Méthode pour évaluer la grille
    private int evaluate(Model model){
        int score = 0;

        //Alignements horizontaux
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns - 3; col++) {
                score += 5 * evaluateSequence(model.grid[col][row], model.grid[col + 1][row], model.grid[col + 2][row], model.grid[col + 3][row]);
            }
        }

        //Alignements verticaux
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows - 3; row++) {
                score += evaluateSequence(model.grid[col][row], model.grid[col][row + 1], model.grid[col][row + 2], model.grid[col][row + 3]);
            }
        }

        //Alignements diagonaux ascendantes (de gauche à droite)
        for (int col = 0; col < columns - 3; col++) {
            for (int row = 0; row < rows - 3; row++) {
                score += evaluateSequence(model.grid[col][row], model.grid[col + 1][row + 1], model.grid[col + 2][row + 2], model.grid[col + 3][row + 3]);
            }
        }

        //Alignements diagonaux descendantes (de gauche à droite)
        for (int col = 0; col < columns - 3; col++) {
            for (int row = 3; row < rows; row++) {
                score += evaluateSequence(model.grid[col][row], model.grid[col + 1][row - 1], model.grid[col + 2][row - 2], model.grid[col + 3][row - 3]);
            }
        }

        return score;
    }

    // Méthode pour évaluer une séquence de 4 jetons
    int evaluateSequence(int jeton1, int jeton2, int jeton3, int jeton4){
        int score = 0;
        int nbJetonJoueur = 0;
        int nbJetonOrdinateur = 0;
        int nbCaseVide = 0;

        // Recherche de la séquence
        if(jeton1 == 1){
            nbJetonJoueur++;
        } else if(jeton1 == 2){
            nbJetonOrdinateur++;
        } else {
            nbCaseVide++;
        }

        if(jeton2 == 1){
            nbJetonJoueur++;
        } else if(jeton2 == 2){
            nbJetonOrdinateur++;
        } else {
            nbCaseVide++;
        }

        if(jeton3 == 1){
            nbJetonJoueur++;
        } else if(jeton3 == 2){
            nbJetonOrdinateur++;
        } else {
            nbCaseVide++;
        }

        if(jeton4 == 1){
            nbJetonJoueur++;
        } else if(jeton4 == 2){
            nbJetonOrdinateur++;
        } else {
            nbCaseVide++;
        }

        // Évaluation de la séquence
        if(nbJetonJoueur == 4){
            score -= 10000;
        } else if(nbJetonJoueur == 3 && nbCaseVide == 1) {
            score -= 500;
        } else if (nbJetonJoueur == 3 && nbJetonOrdinateur == 1){
            score -= 50;
        } else if(nbJetonJoueur == 2 && nbCaseVide == 2) {
            score -= 20;
        } else if (nbJetonJoueur == 2 && nbJetonOrdinateur == 1){
            score -= 5;
        } else if(nbJetonJoueur == 1 && nbCaseVide == 3){
            score -= 1;
        }

        if(nbJetonOrdinateur == 4){
            score += 10000;
        } else if(nbJetonOrdinateur == 3 && nbCaseVide == 1) {
            score += 100;
        } else if (nbJetonOrdinateur == 3 && nbJetonJoueur == 1){
            score += 50;
        } else if(nbJetonOrdinateur == 2 && nbCaseVide == 2) {
            score += 20;
        } else if (nbJetonOrdinateur == 2 && nbJetonJoueur == 1){
            score += 5;
        } else if(nbJetonOrdinateur == 1 && nbCaseVide == 3){
            score += 1;
        }

        return score;
    }
}
