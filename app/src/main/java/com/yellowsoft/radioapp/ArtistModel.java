package com.yellowsoft.radioapp;

/**
 * Created by sriven on 1/27/2016.
 */
public class ArtistModel {
    String name,artist_id,image,description;

    ArtistModel(String name,String artist_id,String image,String description){
        this.name = name;
        this.artist_id=artist_id;
        this.image=image;
        this.description=description;
    }

    public String getName(){
        return this.name;
    }
    public String getArtist_id(){
        return this.artist_id;
    }
    public String getImage(){
        return this.image;
    }
    public String getDescription(){
        return this.description;
    }
}
