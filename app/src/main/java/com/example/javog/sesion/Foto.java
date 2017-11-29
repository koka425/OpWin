/*
 * Copyright (C) 2017 Marcos Rivas Rojas
 *
 *
 */
package com.example.javog.sesion;


public class Foto {

    private String nombre;
    private int imagen;

    public Foto(String nombre, int imagen) {
        this.nombre = nombre;
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public int getImagen() {
        return imagen;
    }
}
