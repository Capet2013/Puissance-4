module ensisa.puissance4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens ensisa.puissance4 to javafx.fxml;
    opens ensisa.puissance4.controller to javafx.fxml;
    exports ensisa.puissance4;
}