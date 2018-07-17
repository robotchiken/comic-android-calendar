package com.tacuba.comicsmanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.fragments.ShoppingCartFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

public class MyShoppingCartRecyclerViewAdapter extends RecyclerView.Adapter<MyShoppingCartRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<InfoComic> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyShoppingCartRecyclerViewAdapter(ArrayList<InfoComic> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comic_list_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        InfoComic tmp =mValues.get(position);
        holder.textNumero.setText(String.valueOf(tmp .getComicDto().getNumero()));
        holder.textEditorial.setText(tmp.getEditorialDto().getNombre());
        holder.textFecha.setText(tmp.getComicDto().getFecha());
        holder.textTitulo.setText(tmp.getComicDto().getTitulo());
        holder.textPrecioL.setText(String.valueOf(tmp.getComicDto().getPrecio()));
        holder.textPeriodicidadL.setText(tmp.getPeridiocidadDto().getDescripcion());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView textNumero;
        public final TextView textEditorial;
        public final TextView textFecha;
        public final TextView textTitulo;
        public final TextView textPrecioL;
        public final TextView textPeriodicidadL;
        public InfoComic mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textNumero = (TextView) view.findViewById(R.id.textNumero);
            textEditorial = (TextView) view.findViewById(R.id.textEditorial);
            textFecha = (TextView) view.findViewById(R.id.textFecha);
            textTitulo = (TextView) view.findViewById(R.id.textTitulo);
            textPrecioL = (TextView) view.findViewById(R.id.textPrecioL);
            textPeriodicidadL = (TextView) view.findViewById(R.id.textPeriodicidadL);
        }
    }
}
