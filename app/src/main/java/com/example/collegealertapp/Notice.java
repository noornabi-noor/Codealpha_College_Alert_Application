//package com.example.collegealertapp;
//
//import java.util.List;
//public class Notice {
//    private String noticeId;
//    private String title;
//    private String description;
//    private List<String> imageUrls;
//    private String date;
//
//    private String id;
//    private String creatorId;
//
//    public Notice() {
//        // Default constructor required for Firebase
//    }
//
//    public Notice(String noticeId,String date, String title, String description, List<String> imageUrls) {
//        this.noticeId = noticeId;
//        this.date = date;
//        this.title = title;
//        this.description = description;
//        this.imageUrls = imageUrls;
//
//
//    }
//
//
//    // Method to get the ID
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getNoticeId() {
//        return noticeId;
//    }
//
//    public void setNoticeId(String noticeId) {
//        this.noticeId = noticeId;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public List<String> getImageUrls() {
//        return imageUrls;
//    }
//
//    public void setImageUrls(List<String> imageUrls) {
//        this.imageUrls = imageUrls;
//    }
//}



package com.example.collegealertapp;

import java.util.List;

public class Notice {
    private String noticeId;
    private String title;
    private String description;
    private List<String> imageUrls;
    private String date;
    private String creatorId; // Add creator ID field

    public Notice() {
        // Default constructor required for Firebase
    }

    public Notice(String noticeId, String date, String title, String description, List<String> imageUrls, String creatorId) {
        this.noticeId = noticeId;
        this.date = date;
        this.title = title;
        this.description = description;
        this.imageUrls = imageUrls;
        this.creatorId = creatorId;
    }

    // Getter and setter methods for creatorId
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
