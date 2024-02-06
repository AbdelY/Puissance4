module com.example.puissance4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.puissance4 to javafx.fxml;
    exports com.example.puissance4;
}