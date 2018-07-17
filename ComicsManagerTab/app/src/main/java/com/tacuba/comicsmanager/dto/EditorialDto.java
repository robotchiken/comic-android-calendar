package com.tacuba.comicsmanager.dto;

/**
 * Created by mendoedg on 24/02/2015.
 */
public class EditorialDto extends BaseDto{

    private String nombre;

    public EditorialDto(int id, String nombre) {
        super(id);
        this.nombre = nombre;
    }

    public EditorialDto(String nombre){
        super(0);
        this.nombre=nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }



}
