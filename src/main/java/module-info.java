module com.example.musicdatabaseui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;

    opens com.example.musicdatabaseui to javafx.fxml;
    exports com.example.musicdatabaseui;
}