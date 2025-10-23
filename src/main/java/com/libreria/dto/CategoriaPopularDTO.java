package com.libreria.dto;

public class CategoriaPopularDTO {
    private String nombre;
    private Long cantidad;

    public CategoriaPopularDTO() {
    }

    public CategoriaPopularDTO(String nombre, Long cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCantidad() {
        return cantidad;
    }

    public void setCantidad(Long cantidad) {
        this.cantidad = cantidad;
    }
}
