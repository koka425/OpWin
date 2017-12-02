package com.example.javog.sesion.Datos;

/**
 * Created by T410 on 30/11/2017.
 */

public class Job {
    private String id;
    private String tittle;
    private String description;
    private String reqTime;
    private int money;
    private String addInfo;
    private String userID;
    private String trabajadorID;
    private double latitud;
    private double longitud;
    private boolean aceptado;
    private boolean terminado;

    public Job(String tittle, String description, String reqTime, int money, String addInfo, String userID, String trabajadorID, double latitud, double longitud, boolean aceptado, boolean terminado) {
        this.id = "";
        this.tittle = tittle;
        this.description = description;
        this.reqTime = reqTime;
        this.money = money;
        this.addInfo = addInfo;
        this.userID = userID;
        this.trabajadorID = trabajadorID;
        this.latitud = latitud;
        this.longitud = longitud;
        this.aceptado = aceptado;
        this.terminado = terminado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
    }

    public boolean isTerminado() {
        return terminado;
    }

    public void setTerminado(boolean terminado) {
        this.terminado = terminado;
    }

    public String getTrabajadorID() {
        return trabajadorID;
    }

    public void setTrabajadorID(String trabajadorID) {
        this.trabajadorID = trabajadorID;
    }
}
