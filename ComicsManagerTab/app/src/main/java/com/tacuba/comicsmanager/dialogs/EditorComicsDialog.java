package com.tacuba.comicsmanager.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.tacuba.comicsmanager.Constantes;
import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.singletons.ShoppingCart;
import com.tacuba.comicsmanager.utils.MyDateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mendoedg on 24/02/2015.
 */
public class EditorComicsDialog extends DialogFragment {
    private Spinner spnEditorial;
    private Spinner spnPeridiocidad;
    private EditText editFecha;
    private EditText editTitulo;
    private EditText editNumero;
    private EditText editTotalNumeros;
    private EditText editPrecio;
    private int year;
    private int month;
    private int day;
    private int comicID;
    private String editorial;
    private String titulo;
    private String peridiocidad;
    private String fecha;
    private int numero;
    private double precio;
    private int numeroFinal;
    private boolean bolEnpublicacion;
    private int indice;
    private RelativeLayout relativeLayoutButtons;
    private ImageView imageViewPrev;
    private ImageView imageViewNext;
    ArrayList<InfoComic>comicsListEdit;
    boolean actualizar;
    private String fechaSeleccionada;
    private CheckBox checkBoxEnPublicacion;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the border_shadow inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.editor_layout, null);
        editFecha = (EditText)rootView.findViewById(R.id.editFecha);
        spnEditorial = (Spinner) rootView.findViewById(R.id.spnEditorial);
        spnPeridiocidad =(Spinner)rootView.findViewById(R.id.spnPeridiocidad);
        editTitulo =(EditText)rootView.findViewById(R.id.editTitulo);
        editNumero = (EditText)rootView.findViewById(R.id.editNumero);
        editTotalNumeros =(EditText)rootView.findViewById(R.id.editTotalNumeros);
        editPrecio=(EditText)rootView.findViewById(R.id.editPrecio);
        relativeLayoutButtons = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutButtons);
        imageViewPrev = (ImageView)rootView.findViewById(R.id.imageViewPrev);
        imageViewNext = (ImageView)rootView.findViewById(R.id.imageViewNext);
        checkBoxEnPublicacion = (CheckBox)rootView.findViewById(R.id.checkBoxEnPublicacion);
        Bundle bundle = getArguments();
        List<String> listEditorial = null;
        List<String> listPeridiocidad = null;
        ArrayAdapter<String> dataAdapterEditorial = null;
        ArrayAdapter<String> dataAdapterPeridiocidad = null;
        actualizar=false;

        if(bundle != null){
            listEditorial = bundle.getStringArrayList("listEditorial");
            listPeridiocidad = bundle.getStringArrayList("listPeridiocidad");

            if(listEditorial != null){
                dataAdapterEditorial = new ArrayAdapter<String>(builder.getContext(),android.R.layout.simple_spinner_item, listEditorial);
                dataAdapterEditorial.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }

            if(listPeridiocidad != null){
                dataAdapterPeridiocidad = new ArrayAdapter<String>(builder.getContext(),android.R.layout.simple_spinner_item, listPeridiocidad);
                dataAdapterPeridiocidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }

            actualizar = bundle.getBoolean(Constantes.ACTUALIZAR);

            fechaSeleccionada = (bundle.getString("fechaSeleccionada") != null ? MyDateUtils.convertirFecha(bundle.getString("fechaSeleccionada")):null);
            if(actualizar){
                ShoppingCart shoppingCart = (ShoppingCart) getActivity().getApplicationContext();
                comicsListEdit = shoppingCart.getEditComicList();
                if(comicsListEdit.size() == 1){
                    setEditValues(comicsListEdit.get(0));
                }else{
                    setEditValues(comicsListEdit.get(indice));
                    relativeLayoutButtons.setVisibility(View.VISIBLE);
                    imageViewPrev.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setEditValues(comicsListEdit.get(--indice));
                            if(indice <= 0){
                                imageViewPrev.setVisibility(View.GONE);
                            }

                            if (imageViewNext.getVisibility() == View.GONE){
                                imageViewNext.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    imageViewPrev.setVisibility(View.GONE);
                    imageViewNext.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            setEditValues(comicsListEdit.get(++indice));
                            if(indice == comicsListEdit.size()-1){
                                imageViewNext.setVisibility(View.GONE);
                            }

                            if(imageViewPrev.getVisibility() == View.GONE){
                                imageViewPrev.setVisibility(View.VISIBLE);
                            }


                        }
                    });
                }
            }else{
                editFecha.setText(fechaSeleccionada);
                editTitulo.setText("");
                editNumero.setText("");
                editTotalNumeros.setText("");
                editPrecio.setText("");
            }
        }

        spnEditorial.setAdapter(dataAdapterEditorial);
        spnPeridiocidad.setAdapter(dataAdapterPeridiocidad);
        if(listPeridiocidad != null && actualizar){
            int pos = listPeridiocidad.indexOf(peridiocidad);
            if(pos > 0){
                spnPeridiocidad.setSelection(pos);
            }
        }

        if(listEditorial != null && actualizar){
            int pos =listEditorial.indexOf(editorial);
            if(pos > 0){
                spnEditorial.setSelection(pos);
            }
        }
        obtenerFechaActual();
        ImageButton imageBtnCalendar = (ImageButton)rootView.findViewById(R.id.imageBtnCalendar);
        imageBtnCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datePickerListener,year, month,day);
                datePickerDialog.show();
            }
        });

        builder.setView(rootView);
        builder.setTitle((actualizar ? R.string.strEditarComic : R.string.strAgregarComics));
        if(actualizar && comicsListEdit .size() > 1){
            builder.setNeutralButton(R.string.strGuardar, null);
        }else if(actualizar && comicsListEdit.size() == 1) {
            builder.setPositiveButton(R.string.strGuardar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String titulo;
                    String fecha;
                    String strNumero;
                    titulo = editTitulo.getText().toString();
                    fecha =editFecha.getText().toString();
                    strNumero = editNumero.getText().toString();
                    int numero = (strNumero.trim().equals("")? 0: Integer.parseInt(strNumero));
                    String strPrecio = editPrecio.getText().toString();
                    float precio = (strPrecio.trim().equals("")? 0: Float.parseFloat(strPrecio));
                    String strPeridiocidad =spnPeridiocidad.getSelectedItem().toString();
                    String strEditorial=spnEditorial.getSelectedItem().toString();
                    String strNumeroFinal =editTotalNumeros.getText().toString();
                    int numeroFinal = (strNumeroFinal.trim().equals("") ? 0 : Integer.parseInt(strNumeroFinal));
                    int enPublicacion = (checkBoxEnPublicacion.isChecked() ? 1:0);
                    mListener.onComicsEditorDialogPositiveClick(titulo, fecha, numero, precio, strPeridiocidad, strEditorial, numeroFinal, comicID, actualizar,enPublicacion);
                }
            });
        }else{
            builder.setPositiveButton(R.string.strGuardar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    String titulo;
                    String fecha;
                    String strNumero;
                    titulo = editTitulo.getText().toString();
                    fecha =editFecha.getText().toString();
                    strNumero = editNumero.getText().toString();
                    int numero = (strNumero.trim().equals("")? 0: Integer.parseInt(strNumero));
                    String strPrecio = editPrecio.getText().toString();
                    float precio = (strPrecio.trim().equals("")? 0: Float.parseFloat(strPrecio));
                    String strPeridiocidad =spnPeridiocidad.getSelectedItem().toString();
                    String strEditorial=spnEditorial.getSelectedItem().toString();
                    String strNumeroFinal =editTotalNumeros.getText().toString();
                    int numeroFinal = (strNumeroFinal.trim().equals("") ? 0 : Integer.parseInt(strNumeroFinal));
                    int enPublicacion = (checkBoxEnPublicacion.isChecked() ? 1:0);
                    mListener.onComicsEditorDialogPositiveClick(titulo, fecha, numero, precio, strPeridiocidad, strEditorial, numeroFinal, 0, actualizar,enPublicacion);
                }
            });
        }

        builder.setNegativeButton(R.string.strCancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onComicsEditorDialogNegativeClick(EditorComicsDialog.this);
            }
        });
        return builder.create();
    }



    private void setEditValues(InfoComic infoComic) {
        comicID= infoComic.getComicDto().getId();
        editorial= infoComic.getEditorialDto().getNombre();
        titulo= infoComic.getComicDto().getTitulo();
        peridiocidad = infoComic.getPeridiocidadDto().getDescripcion();
        fecha= infoComic.getComicDto().getFecha();
        numero = infoComic.getComicDto().getNumero();
        precio = infoComic.getComicDto().getPrecio();
        numeroFinal= infoComic.getComicDto().getNumeroFinal();
        bolEnpublicacion = (infoComic.getComicDto().getEnPublicacion() ==1);
        editFecha.setText(fecha);
        editTitulo.setText(titulo);
        editNumero.setText(String.valueOf(numero));
        editTotalNumeros.setText(String.valueOf(numeroFinal));
        editPrecio.setText(String.valueOf(precio));
        checkBoxEnPublicacion.setChecked(bolEnpublicacion);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            String fecha = MyDateUtils.dateFormat(selectedDay, selectedMonth, selectedYear);
            editFecha.setText(new StringBuilder(fecha));
        }
    };

    public interface EditorComicsDialogListener {
        public void onComicsEditorDialogNegativeClick(DialogFragment dialog);
        public void onComicsEditorDialogNeutralClick(String titulo, String fecha, int numero, float precio, String strPeridiocidad, String strEditorial, int numeroFinal, int comicID, int enPublicacion);
        public void onComicsEditorDialogPositiveClick(String titulo, String fecha, int numero, float precio, String strPeridiocidad, String strEditorial, int numeroFinal, int comicID, boolean actualizar,int enPublicacion);
    }

    // Use this instance of the interface to deliver action events
    EditorComicsDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the EditorComicsDialogListener so we can send events to the host
            mListener = (EditorComicsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EditorComicsDialogListener");
        }
    }

    private void obtenerFechaActual(){
        Calendar calendar = null;
        if(fechaSeleccionada!=null){
            calendar = MyDateUtils.convertirStrFecha(fechaSeleccionada);
        }else{
            calendar  = Calendar.getInstance();
        }
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onResume() {
        super.onResume();

        AlertDialog alertDialog = (AlertDialog) getDialog();
        /*
        Esta tecnica evita que al dar click en el botón se cierre el dialogo
         */
        Button neutralButton = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        if(neutralButton != null){
            neutralButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String titulo;
                    String fecha;
                    String strNumero;
                    titulo = editTitulo.getText().toString();
                    fecha =editFecha.getText().toString();
                    strNumero = editNumero.getText().toString();
                    int numero = (strNumero.trim().equals("")? 0: Integer.parseInt(strNumero));
                    String strPrecio = editPrecio.getText().toString();
                    float precio = (strPrecio.trim().equals("")? 0: Float.parseFloat(strPrecio));
                    String strPeridiocidad =spnPeridiocidad.getSelectedItem().toString();
                    String strEditorial=spnEditorial.getSelectedItem().toString();
                    String strNumeroFinal =editTotalNumeros.getText().toString();
                    int numeroFinal = (strNumeroFinal.trim().equals("") ? 0 : Integer.parseInt(strNumeroFinal));
                    int enPublicacion = (checkBoxEnPublicacion.isChecked() ? 1:0);
                    mListener.onComicsEditorDialogNeutralClick(titulo,fecha,numero,precio,strPeridiocidad,strEditorial,numeroFinal,comicID,enPublicacion);
                }
            });
        }
    }
}
