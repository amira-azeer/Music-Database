package com.example.musicdatabaseui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        if(!DataSource.getInstance().open()){
            System.out.println("FATAL ERROR : Could not connect to the database");
            Platform.exit(); // Closing the UI before it opens to the user
        }
    }

    @Override
    public void start(Stage stage) throws Exception { // Overridden from the application class
        // stage => the window all content is displayed
        // scene => the content inside the window
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load(); //UI is constructed
        Controller controller = loader.getController();
        controller.listArtists();
        stage.setTitle("Music Database");
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // this goes into the Application method and sets up the JavaFX
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DataSource.getInstance().close();
    }
}