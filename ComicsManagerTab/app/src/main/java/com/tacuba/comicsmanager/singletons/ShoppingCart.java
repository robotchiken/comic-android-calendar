package com.tacuba.comicsmanager.singletons;

import android.app.Application;

import com.tacuba.comicsmanager.dto.InfoComic;

import java.util.ArrayList;

/**
 * Created by mendoedg on 05/03/2015.
 */
public class ShoppingCart extends Application {

   private ArrayList<InfoComic> shoppingCart;
   private ArrayList<InfoComic> editComicList;
    public ArrayList<InfoComic> getShoppingCart() {
        return shoppingCart;
    }

    public ArrayList<InfoComic> getEditComicList() {
        return editComicList;
    }

    public void setEditComicList(ArrayList<InfoComic> editComicList) {
        this.editComicList = editComicList;
    }

    public void setShoppingCart(ArrayList<InfoComic> shoppingCart) {
        if(shoppingCart == null){
            this.shoppingCart = shoppingCart;
        }else{
            if(this.shoppingCart == null){
                this.shoppingCart = new ArrayList<InfoComic>();
                this.shoppingCart.addAll(shoppingCart);
            }else{
                for(InfoComic infoComic : shoppingCart){
                    if(!this.shoppingCart.contains(infoComic)){
                        this.shoppingCart.add(infoComic);
                    }
                }
            }

        }
    }
}
