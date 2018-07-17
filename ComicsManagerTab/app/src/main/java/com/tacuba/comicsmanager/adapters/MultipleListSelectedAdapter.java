package com.tacuba.comicsmanager.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.dao.AndroidFacade;
import com.tacuba.comicsmanager.dto.ComicDto;
import com.tacuba.comicsmanager.dto.EditorialDto;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.dto.PeridiocidadDto;
import com.tacuba.comicsmanager.utils.MyDateUtils;

import org.apache.commons.lang3.StringUtils;


public class MultipleListSelectedAdapter extends ArrayAdapter<InfoComic> {

	private ArrayList<InfoComic> comicList;
	private Context context;
	private int resource;
	private AndroidFacade androidFacade;
	private boolean validarComicEnCarrito;
	private String titulo;

	public MultipleListSelectedAdapter(Context context, int resource,List<InfoComic> listComics) {
		super(context, resource, listComics);
		this.comicList = new ArrayList<InfoComic>();
		if(listComics != null){
			this.comicList.addAll(listComics);	
		}
		this.context = context;
		this.resource=resource;
		this.androidFacade = new AndroidFacade(this.context);
	}

	public MultipleListSelectedAdapter(Context context, int resource,List<InfoComic> listComics,boolean validarComicEnCarrito) {
		super(context, resource, listComics);
		this.comicList = new ArrayList<InfoComic>();
		if(listComics != null){
			this.comicList.addAll(listComics);
		}
		this.context = context;
		this.resource=resource;
		this.androidFacade = new AndroidFacade(this.context);
		this.validarComicEnCarrito=validarComicEnCarrito;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderItem viewHolder= null;
        String strNumero="";
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(this.resource, parent, false);
			viewHolder = new ViewHolderItem();
			viewHolder.textNumero = (TextView) convertView.findViewById(R.id.textNumero);
			viewHolder.textEditorial = (TextView)convertView.findViewById(R.id.textEditorial);
			viewHolder.textFecha= (TextView)convertView.findViewById(R.id.textFecha);
			viewHolder.textTitulo = (TextView)convertView.findViewById(R.id.textTitulo);
			viewHolder.textPrecioL = (TextView)convertView.findViewById(R.id.textPrecioL);
			viewHolder.textPeriodicidadL = (TextView)convertView.findViewById(R.id.textPeriodicidadL);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolderItem)convertView.getTag();
		}

		InfoComic tmp = this.comicList.get(position);

        ComicDto comicDto = tmp.getComicDto();
        EditorialDto editorialDto = tmp.getEditorialDto();
        PeridiocidadDto peridiocidadDto = tmp.getPeridiocidadDto();

		Resources res = context.getResources();
		int colorTexto=obtenerColorTexto(comicDto,res);

		strNumero = "#"+comicDto.getNumero()+(comicDto.getNumeroFinal() == 0 ? "" : " de "+comicDto.getNumeroFinal());


		viewHolder.textNumero.setText(strNumero);
		viewHolder.textNumero.setTextColor(colorTexto);
		viewHolder.textTitulo.setText(this.titulo);
		viewHolder.textFecha.setText(comicDto.getFecha());
		viewHolder.textFecha.setTextColor(colorTexto);
		viewHolder.textTitulo.setTextColor(colorTexto);
		viewHolder.textPeriodicidadL.setText(StringUtils.capitalize(peridiocidadDto.getDescripcion()));
		viewHolder.textPeriodicidadL.setTextColor(colorTexto);
		viewHolder.textPrecioL.setText("$" + comicDto.getPrecio());
		viewHolder.textPrecioL.setTextColor(colorTexto);
		viewHolder.textEditorial.setText(editorialDto.getNombre());
		viewHolder.textEditorial.setTextColor(colorTexto);
		return convertView;
	}
	private int obtenerColorTexto(ComicDto comicDto,Resources res){
		int colorTexto=0;
		int resultado = MyDateUtils.compararFecha(comicDto.getFecha(), MyDateUtils.fechaActual());
		if(this.validarComicEnCarrito){
			boolean comicEnCarrito=androidFacade.consultarComicEnCarrito(comicDto.getId());
			if(!comicEnCarrito) {
				if (resultado == 0) {
					colorTexto = res.getColor(R.color.text_ready);
				} else if (resultado > 0) {
					colorTexto = Color.BLACK;
				} else {
					colorTexto = res.getColor(R.color.text_normal);
				}
			}else{
				colorTexto = res.getColor(R.color.item_on_shopping_cart);
			}
			this.titulo=comicDto.getTitulo()+(comicEnCarrito ? "*":"");
			this.titulo+=(comicDto.getEnPublicacion()== 1 ? " Publicando":" En Espera");
		}else{
			if (resultado == 0) {
				colorTexto = res.getColor(R.color.text_ready);
			} else if (resultado > 0) {
				colorTexto = Color.BLACK;
			} else {
				colorTexto = res.getColor(R.color.text_warning);
			}
			this.titulo=comicDto.getTitulo();
		}
		return colorTexto;
	}

	/*
	 * El patron ViewHolder evita que se tenga que llamar a cada momento el findViewById 
	 * para cada elemento y ahorra ciclos de procesador en llamar a los elementos, 
	 * provocando que sea mas rapido la ejecucion del listview 
	 */
	static class ViewHolderItem {
		TextView textNumero;
		TextView textEditorial;
		TextView textFecha;
		TextView textTitulo;
		TextView textPrecioL;
		TextView textPeriodicidadL;
	}
	

}
