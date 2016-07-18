package com.yellowsoft.radioapp;

import android.content.Context;

/**
 * Created by sriven on 1/27/2016.
 */
public class SongModel implements java.io.Serializable{
    String song_name,song_name_ar,song_id,image,description,description_ar,url,itunes,youtube,count,artist_name,artist_name_ar,artist_id;
    String isradio;
    Context context;
    SongModel(String song_name,String song_name_ar,
              String song_id,
              String image,
              String description,String description_ar,
              String url,
              String count,
              String artist_name,String artist_name_ar,
              String artist_id,
              String youtube,
              String itunes,
              String isradio,Context context
    ){
        this.song_name = song_name;
        this.song_name_ar = song_name_ar;
        this.song_id = song_id;
        this.image=image;
        this.description=description;
        this.description_ar=description_ar;
        this.url = url;
        this.count = count;
        this.artist_name = artist_name;
        this.artist_name_ar = artist_name_ar;
        this.artist_id=artist_id;
        this.itunes = "";
        this.youtube = youtube;
        this.isradio = isradio;
        this.context=context;
    }

    public String getSongName(){
        if(Settings.get_user_language(context).equals("en"))
        return this.song_name;
        else
            return this.song_name_ar;

    }
    public String getSongId(){
        return this.song_id;
    }
    public String getArtist_Name(){
        if(Settings.get_user_language(context).equals("en"))
        return this.artist_name;
        else
            return this.artist_name_ar;

    }
    public String getArtist_id(){
        return this.artist_id;
    }
    public String getImage(){
        return this.image;
    }
    public String getDescription(){
        if(Settings.get_user_language(context).equals("en"))
        return this.description;
        else
            return this.description_ar;

    }
    public String getSong(){
        return this.url;
    }
    public String getItunes(){
        return "";
    }
    public String getYoutube(){
        return this.youtube;
    }
    public String getViewcount(){
        return this.count;
    }
}
