package com.example.musicdatabaseui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Albums {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleIntegerProperty artist_id;

    public Albums() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.artist_id = new SimpleIntegerProperty();
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getArtist_id() {
        return artist_id.get();
    }

    public void setArtist_id(int artist_id) {
        this.artist_id.set(artist_id);
    }
}
