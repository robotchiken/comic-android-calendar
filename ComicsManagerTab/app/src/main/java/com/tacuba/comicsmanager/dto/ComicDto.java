package com.tacuba.comicsmanager.dto;

/**
 * Created by mendoedg on 24/02/2015.
 */
public class ComicDto extends BaseDto{
    private String titulo;
    private String fecha;
    private int numero;
    private float precio;
    private int peridiocidad;
    private int editorial;
    private int numeroFinal;
    private int enPublicacion;

    /**
     *  @param id
     * @param titulo
     * @param fecha
     * @param numero
     * @param precio
     * @param peridiocidad
     * @param editorial
     * @param numeroFinal
     * @param enPublicacion
     */
    public ComicDto(int id, String titulo, String fecha, int numero, float precio, int peridiocidad, int editorial, int numeroFinal, int enPublicacion) {
        super(id);
        this.titulo = titulo;
        this.fecha = fecha;
        this.numero = numero;
        this.precio = precio;
        this.peridiocidad = peridiocidad;
        this.editorial = editorial;
        this.numeroFinal = numeroFinal;
        this.enPublicacion = enPublicacion;
    }

    /**
     *  @param id
     * @param titulo
     * @param fecha
     * @param numero
     * @param precio
     * @param peridiocidad
     * @param editorial
     * @param enPublicacion
     */
    public ComicDto(int id, String titulo, String fecha, int numero, float precio, int peridiocidad, int editorial, int enPublicacion) {
        super(id);
        this.titulo = titulo;
        this.fecha = fecha;
        this.numero = numero;
        this.precio = precio;
        this.peridiocidad = peridiocidad;
        this.editorial = editorial;
        this.enPublicacion = enPublicacion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getPeridiocidad() {
        return peridiocidad;
    }

    public void setPeridiocidad(int peridiocidad) {
        this.peridiocidad = peridiocidad;
    }

    public int getEditorial() {
        return editorial;
    }

    public void setEditorial(int editorial) {
        this.editorial = editorial;
    }

    public int getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(int numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public int getEnPublicacion() {
        return enPublicacion;
    }

    public void setEnPublicacion(int enPublicacion) {
        this.enPublicacion = enPublicacion;
    }
}
