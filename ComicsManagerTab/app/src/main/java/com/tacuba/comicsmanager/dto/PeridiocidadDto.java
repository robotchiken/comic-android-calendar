package com.tacuba.comicsmanager.dto;

/**
 * Created by mendoedg on 24/02/2015.
 */
public class PeridiocidadDto extends BaseDto{
    private int diasMeses;
    private String descripcion;
    private int tiempo;

    public PeridiocidadDto(int id, int diasMeses, String descripcion, int tiempo) {
        super(id);
        this.diasMeses = diasMeses;
        this.descripcion = descripcion;
        this.tiempo = tiempo;
    }

    public PeridiocidadDto(int id, String descripcion, int tiempo) {
        super(id);
        this.descripcion = descripcion;
        this.tiempo = tiempo;
    }
    public PeridiocidadDto(String descripcion, int tiempo) {
        super(0);
        this.diasMeses=0;
        this.descripcion = descripcion;
        this.tiempo = tiempo;
    }

    public int getDiasMeses() {
        return diasMeses;
    }

    public void setDiasMeses(int diasMeses) {
        this.diasMeses = diasMeses;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
}
