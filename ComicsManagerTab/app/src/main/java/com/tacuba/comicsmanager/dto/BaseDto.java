package com.tacuba.comicsmanager.dto;

/**
 * Created by mendoedg on 24/02/2015.
 */
public class BaseDto {
    private int id;

    public BaseDto(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
