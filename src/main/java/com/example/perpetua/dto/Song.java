package com.example.perpetua.dto;


public class Song {
    private String title;
    private  String artist;

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    private String lyrics;




    public Song(String title, String artist, String lyrics) {
        this.title = title;
        this.artist = artist;
        this.lyrics = lyrics;

    }
    public Song clone()
    {
        return new Song(this.title, this.artist, this.lyrics);
    }

    public Song() {
    }


    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", lyrics='" + lyrics + '\'' +
                '}';
    }
}
