package com.myproject.ad340;

public class Camera {

    private String street;
    private String imageURL;

    public Camera(String street, String imageURL) {
        this.street = street;
        this.imageURL = imageURL;
    }

    public String getStreet() {
        return street;
    }

    public String getImageURL() {
        return imageURL;
    }
}