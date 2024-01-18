package ensisa.puissance4.view;

import ensisa.puissance4.controller.Controller;
import ensisa.puissance4.model.Model;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class View {
    // Déclaration des variables et des constantes
    private Controller controller;
    private Model model;
    private final Pane rootPane;
    private Label turnLabel;
    public Button startButton;

    // Constructeur
    public View(Pane rootPane) {
        this.rootPane = rootPane;
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public void initialize() {
        createGrid();
        createButtons();
        createLabel();
    }
    public void setModel(Model model) {
        this.model = model;
    }

    // Méthode pour créer la grille
    public void createGrid(){
        for (int i = 0; i <= 6; i++) {
            Shape rectangle = new Rectangle(100, 600, Color.DODGERBLUE);
            rectangle.setLayoutX(i * 100);
            rectangle.setLayoutY(150);
            for (int j = 2; j <= 7; j++) {
                Circle hole = new Circle(40, Color.TRANSPARENT);
                hole.setCenterX(i * 100 + 50);
                hole.setCenterY(j * 100);
                rectangle = Shape.subtract(rectangle, hole);
            }
            rectangle.setFill(Color.DODGERBLUE);
            rootPane.getChildren().add(rectangle);
        }
    }

    // Méthode pour créer les boutons de jeu
    public void createButtons() {
        Shape rectangle = new Rectangle(700, 150, Color.DODGERBLUE);
        rootPane.getChildren().add(rectangle);
        for (int i = 0; i <= 6; i++) {
            Button button = new Button("Lâcher ici");
            int column = i;
            button.setOnAction(event -> controller.handleButtonAction(column));
            button.setLayoutX(i * 100 + 15);
            button.setLayoutY(100);
            button.setDisable(true);
            rootPane.getChildren().add(button);
            button.setUserData(i);
        }
    }

    // Méthode pour créer le label et le bouton de démarrage
    public void createLabel(){
        turnLabel = new Label("Appuyez sur Commencer");
        turnLabel.setFont(new Font("Arial", 30));

        Button start = new Button();
        start.setText("Commencer");
        start.setOnAction(event -> controller.handleStartButtonAction());
        startButton = start;

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(turnLabel, startButton);
        stackPane.prefWidthProperty().bind(rootPane.widthProperty());

        StackPane.setAlignment(turnLabel, Pos.CENTER);
        StackPane.setAlignment(startButton, Pos.CENTER_LEFT);
        StackPane.setMargin(startButton, new Insets(5, 0, 0, 10));

        rootPane.getChildren().add(stackPane);

    }

    // Méthode pour ajouter un jeton
    public void addToken(int column, int row, boolean playerTurn) {
        // Création du jeton
        Circle token = new Circle(40, playerTurn ? Color.RED : Color.YELLOW);
        token.setCenterX(column * 100 + 50);
        token.setCenterY(0);
        rootPane.getChildren().add(0, token);

        // Animation du jeton
        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), token);
        transition.setFromY(-100);
        transition.setToY((row + 2) * 100);
        transition.play();
    }

    // Méthode pour afficher le gagnant
    public void displayWinner(int player) {
        String winnerMessage;
        if (player == 1) {
            winnerMessage = "Vous avez gagné !";
        } else {
            winnerMessage = "Vous avez perdu !";
        }
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Partie terminée");
        alert.setHeaderText(null);
        alert.setContentText(winnerMessage);
        alert.showAndWait();

        turnLabel.setText("Appuyez sur Recommencer");
    }

    // Méthode pour afficher un match nul
    public void displayDraw() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Partie terminée");
        alert.setHeaderText(null);
        alert.setContentText("Match nul");
        alert.showAndWait();

        turnLabel.setText("Appuyez sur Recommencer");
    }

    // Méthode pour désactiver les boutons durant le tour de l'ordinateur
    public void disableButtons() {
        for (javafx.scene.Node node : rootPane.getChildren()) {
            if (node instanceof Button) {
                if (node != startButton){
                    node.setDisable(true);
                }
            }
        }
    }

    // Méthode pour réactiver les boutons durant le tour du joueur
    public void enableButtons() {
        for (javafx.scene.Node node : rootPane.getChildren()) {
            if (node instanceof Button) {
                Integer columnInteger = (Integer) node.getUserData();
                if (columnInteger != null) {
                    int column = columnInteger;
                    if (!model.column[column]) {
                        node.setDisable(false);
                    }
                }
            }
        }
    }

    // Méthode pour mettre à jour le label de tour
    public void updateTurnLabel() {
        turnLabel.setText(model.getPlayerTurn() ? "Votre tour" : "Tour de l'ordinateur");
    }

    // Méthode pour effacer le label de tour
    public void clearTurnLabel() {
        turnLabel.setText("");
    }

    // Méthode pour effacer la grille
    public void clearGrid() {
        rootPane.getChildren().clear();
    }
}
