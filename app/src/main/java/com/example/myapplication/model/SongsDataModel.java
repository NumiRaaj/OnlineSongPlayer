package com.example.myapplication.model;


public class SongsDataModel {

    private long id;
    private String title;
    private String artist;
    private String album;
    private String path;
    private long duration;


    public SongsDataModel(long songID, String songTitle, String songArtist, String songAlbum, String path, long duration) {
        this.artist = songArtist;
        this.id = songID;
        this.title = songTitle;
        this.album = songAlbum;

        this.path = path;
        this.duration = duration;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}
    public String getAlbum(){return album;}
    public String getPath(){return path;}
    public long getDuration(){return duration;}

}
