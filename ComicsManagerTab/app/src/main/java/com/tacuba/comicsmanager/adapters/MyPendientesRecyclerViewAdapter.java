package com.tacuba.comicsmanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.fragments.PendientesFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class MyPendientesRecyclerViewAdapter extends RecyclerView.Adapter<MyPendientesRecyclerViewAdapter.ViewHolder> {

    private final List<InfoComic> mValues;
    private final PendientesFragment.OnListFragmentInteractionListenerPendientes mListener;

    public MyPendientesRecyclerViewAdapter(List<InfoComic> items, PendientesFragment.OnListFragmentInteractionListenerPendientes listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pendientes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.tituloRec.setText(mValues.get(position).getComicDto().getTitulo());
        holder.fechaRec.setText( mValues.get(position).getComicDto().getFecha());
        holder.editorialRec.setText(mValues.get(position).getEditorialDto().getNombre());
        holder.periRec.setText(StringUtils.capitalize(mValues.get(position).getPeridiocidadDto().getDescripcion()));
        String strContent=String.valueOf(mValues.get(position).getComicDto().getPrecio());
        holder.precioRec.setText(strContent);
        int numeroInicial = mValues.get(position).getComicDto().getNumero();
        int numeroFinal = mValues.get(position).getComicDto().getNumeroFinal();
        String mensaje="# ";
        if(numeroFinal > numeroInicial){
            mensaje += numeroInicial+" de "+numeroFinal;
        }else{
            mensaje +=String.valueOf(numeroInicial);
        }
        strContent+=" "+mensaje;
        holder.numeroRec.setText(mensaje);
/*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {


                }
            }
        });*/
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }



    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tituloRec;
        public final TextView fechaRec;
        public final TextView precioRec;
        public final TextView numeroRec;
        public final TextView editorialRec;
        public final TextView periRec;


        public InfoComic mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tituloRec = (TextView) view.findViewById(R.id.tituloRec);
            fechaRec = (TextView) view.findViewById(R.id.fechaRec);
            precioRec=(TextView)view.findViewById(R.id.precioRec);
            numeroRec=(TextView)view.findViewById(R.id.numeroRec);
            editorialRec=(TextView)view.findViewById(R.id.editorialRec);
            periRec = (TextView)view.findViewById(R.id.periRec);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + fechaRec.getText() + "'";
        }
    }
}
