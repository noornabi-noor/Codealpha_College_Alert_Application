//
//package com.example.collegealertapp;
//
//import android.net.Uri;
//
//public class ModelImagePicked {
//
//    private Uri imageUri;
//
//    public ModelImagePicked(Uri imageUri) {
//        this.imageUri = imageUri;
//    }
//
//    public Uri getImageUri() {
//        return imageUri;
//    }
//
//    public void setImageUri(Uri imageUri) {
//        this.imageUri = imageUri;
//    }
//}



package com.example.collegealertapp;

import android.net.Uri;

public class ModelImagePicked {
    /*---Variables---*/
    private String id = "";
    private Uri imageUri = null;
    private String imageUrl = null;
    private Boolean fromInternet = false;

    /*---Empty constructor required for Firebase db---*/
    public ModelImagePicked() {

    }

    /*---Constructor with all parameters---*/
    public ModelImagePicked(String id, Uri imageUri, String imageUrl, Boolean fromInternet) {
        this.id = id;
        this.imageUri = imageUri;
        this.imageUrl = imageUrl;
        this.fromInternet = fromInternet;
    }

    /*---Getter & Setters---*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getFromInternet() {
        return fromInternet;
    }

    public void setFromInternet(Boolean fromInternet) {
        this.fromInternet = fromInternet;
    }
}
