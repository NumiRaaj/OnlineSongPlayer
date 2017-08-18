package com.example.myapplication.data;

public class PropertiesModel {
    private String duration;
    private String fileName;
    private String format;
    private String location;
    private String resolution;
    private String size;

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResolution() {
        return this.resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
