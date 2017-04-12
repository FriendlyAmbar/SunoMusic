package com.ambar.musicplayerapp;

import android.graphics.Bitmap;

/**
 * Created by ambar on 12-04-2017.
 */

public class Songs {



    String data;
    String title;
    Integer id;
    String album;
    Bitmap songImage;
    String artPath;
    String artist;

    public Songs(String data, String title, Integer id, String album, Bitmap songImage, String artPath, String artist) {
        this.data = data;
        this.title = title;
        this.id = id;
        this.album = album;
        this.songImage = songImage;
        this.artPath = artPath;
        this.artist = artist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Bitmap getSongImage() {
        return songImage;
    }

    public void setSongImage(Bitmap songImage) {
        this.songImage = songImage;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
