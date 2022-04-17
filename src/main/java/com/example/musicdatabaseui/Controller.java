package com.example.musicdatabaseui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

// NOTE - Run queries in background thread (task) if needed to display on the UI run on UI thread

public class Controller {
    @FXML
    private TableView artistTable;

    @FXML
    public void listArtists(){
        Task<ObservableList<Artist>> task = new GetAllArtist();
        artistTable.itemsProperty().bind(task.valueProperty()); // binding the list to the table view
        new Thread(task).start();
    }

    @FXML
    public void listAlbumsForArtist() {
        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if(artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }
        Task<ObservableList<Albums>> task = new Task<ObservableList<Albums>>() {
            @Override
            protected ObservableList<Albums> call(){
                return FXCollections.observableArrayList(
                        DataSource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };
        artistTable.itemsProperty().bind(task.valueProperty());
        new Thread(task).start();
    }

    @FXML
    public void updateArtist(){
        final Artist artist = (Artist) artistTable.getItems().get(2); // getting actual record
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return DataSource.getInstance().updateArtistName(artist.getId(), "AC/DC");
            }
        };
        task.setOnSucceeded(e -> {
            if(task.valueProperty().get()){
                artist.setName("AC/DC");
                artistTable.refresh();
            }
        });
        new Thread(task).start();
    }
}

class GetAllArtist extends Task{
    @Override
    public ObservableList<Artist> call(){ // A list that allows listeners to track changes when they occur
        return FXCollections.observableArrayList(DataSource.getInstance().queryArtist(DataSource.ORDER_BY_ASC));
    }
}