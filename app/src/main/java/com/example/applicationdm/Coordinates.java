package com.example.applicationdm;

/*----------------------------------------
*
*Classe Java : enregistre les coordonnées sous forme de latitude et longitude
*
---------------------------------------- */
public class Coordinates {
    Double Latitude;
    Double Longitude;

    public Coordinates(Double latitude, Double longitude) {
        this.Latitude = latitude;
        this.Longitude = longitude;
    }

    public Coordinates() {

    }


    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
