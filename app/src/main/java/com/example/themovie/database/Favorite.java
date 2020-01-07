package com.example.themovie.database;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "favorite")
public class Favorite {

    @PrimaryKey
    private int id;
    private String name;
    private String jsonMovieString;

    @Ignore
    public Favorite(String name, String jsonMovieString) {

        this.name = name;
        this.jsonMovieString = jsonMovieString;
    }

    public Favorite(int id, String name, String jsonMovieString) {
        this.id = id;
        this.name = name;
        this.jsonMovieString = jsonMovieString;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getJsonMovieString() {
        return jsonMovieString;
    }

    public void setJsonMovieString(String jsonMovieString) {
        this.jsonMovieString = jsonMovieString;
    }


}
