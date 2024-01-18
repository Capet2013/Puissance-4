package ensisa.puissance4;

import ensisa.puissance4.controller.Controller;
import ensisa.puissance4.model.Model;
import ensisa.puissance4.view.View;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Puissance4Application extends Application {
    @Override
    public void start(Stage stage) {
        Pane rootpane = new Pane();
        // Création du modèle, de la vue et du contrôleur
        Model model = new Model(rootpane);
        View view = new View(rootpane);
        Controller controller = new Controller(model, view);
        // Initialisation et liaisons des différents composants
        controller.initialize();
        view.setController(controller);
        // Création de la scène
        Scene scene = new Scene(rootpane, 700, 750);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Puissance 4");
        stage.show();
    }
}