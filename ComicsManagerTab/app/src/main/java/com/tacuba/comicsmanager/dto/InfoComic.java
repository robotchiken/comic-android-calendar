package com.tacuba.comicsmanager.dto;

/**
 * Created by mendoedg on 25/02/2015.
 */
public class InfoComic {
    private ComicDto comicDto;
    private EditorialDto editorialDto;
    private PeridiocidadDto peridiocidadDto;

    public InfoComic(ComicDto comicDto, EditorialDto editorialDto, PeridiocidadDto peridiocidadDto) {
        this.comicDto = comicDto;
        this.editorialDto = editorialDto;
        this.peridiocidadDto = peridiocidadDto;
    }

    public ComicDto getComicDto() {
        return comicDto;
    }

    public void setComicDto(ComicDto comicDto) {
        this.comicDto = comicDto;
    }

    public EditorialDto getEditorialDto() {
        return editorialDto;
    }

    public void setEditorialDto(EditorialDto editorialDto) {
        this.editorialDto = editorialDto;
    }

    public PeridiocidadDto getPeridiocidadDto() {
        return peridiocidadDto;
    }

    public void setPeridiocidadDto(PeridiocidadDto peridiocidadDto) {
        this.peridiocidadDto = peridiocidadDto;
    }

    @Override
    public boolean equals(Object object) {
        boolean result=false;
        if(object != null && object instanceof InfoComic ){
            InfoComic infoComic = (InfoComic)object;
            result =(this.getComicDto().getId() == infoComic.getComicDto().getId());
        }
        return result;
    }
}
