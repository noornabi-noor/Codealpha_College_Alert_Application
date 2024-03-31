package com.example.collegealertapp;

public class Ad {
    private String title;
    private String date;
    private int imageResource;

    public Ad() {
        // Default constructor required for Firebase
    }

    public Ad(String title, String date, int imageResource) {
        this.title = title;
        this.date = date;
        this.imageResource = imageResource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}
