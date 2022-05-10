package com.myproject.ad340;

public class Camera {

    private String description;
    private String imageURL;
    private Double[] coordinates;

    public Camera(){

    }

    public Camera(String description, String imageURL, Double[] coordinates ) {
        this.description = description;
        this.imageURL = imageURL;
       // this.coordinates = coordinates;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public Double[] getCoordinates(){
        return coordinates;
    };

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setCoordinates(Double[] coordinates){
        this.coordinates = coordinates;
    };
}