package com.example.myapplication.data;

public class MediaFolder {
    private String displayName;
    private int numberOfMediaFiles;
    private String path;

    public MediaFolder() {
        setDisplayName("");
        setNumberOfMediaFiles(1);
        setPath("");
    }


    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumberOfMediaFiles() {
        return this.numberOfMediaFiles;
    }

    public void setNumberOfMediaFiles(int numberOfMediaFiles) {
        this.numberOfMediaFiles = numberOfMediaFiles;
    }
}
