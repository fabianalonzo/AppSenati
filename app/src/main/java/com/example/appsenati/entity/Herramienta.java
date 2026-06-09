package com.example.appsenati.entity;

public class Herramienta {

    private int idherramienta;
    private String nombre;
    private String marca;
    private String descripcion;
    private String condicion;
    private String tipo;

    public Herramienta() {

    }

    public Herramienta(int idherramienta, String nombre, String marca, String descripcion, String condicion, String tipo) {
        this.idherramienta = idherramienta;
        this.nombre = nombre;
        this.marca = marca;
        this.descripcion = descripcion;
        this.condicion = condicion;
        this.tipo = tipo;
    }

    public int getIdherramienta() {
        return idherramienta;
    }

    public void setIdherramienta(int idherramienta) {
        this.idherramienta = idherramienta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
