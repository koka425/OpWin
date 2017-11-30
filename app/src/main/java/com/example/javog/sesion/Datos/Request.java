package com.example.javog.sesion.Datos;

/**
 * Created by T410 on 30/11/2017.
 */

public class Request {
    private String id;
    private String userID;
    private String date;
    private String description;
    private String Time;
    private String payment;
    private String location;

    public Request(String id, String userID, String date, String description, String time, String payment, String location) {
        this.id = id;
        this.userID = userID;
        this.date = date;
        this.description = description;
        Time = time;
        this.payment = payment;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
