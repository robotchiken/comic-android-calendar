package com.tacuba.comicsmanager.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.dao.AndroidFacade;
import com.tacuba.comicsmanager.dto.EditorialDto;
import com.tacuba.comicsmanager.dto.PeridiocidadDto;

import java.util.ArrayList;

public class ConfigurationActivity extends AppCompatActivity {

    AndroidFacade androidFacade;
    Spinner spinnerPer;
    Spinner spinnerEditorial;
    EditText txtDescPer;
    EditText txtDescTiempo;
    EditText txtDescEditorial;
    Button btnAceptarPer;
    Button btnAceptarEditorial;
    Button btnLimpiarPer;
    Button btnLimpiarEd;
    ArrayList<String>lstEditoriales;
    ArrayList<String>lstPeriodicidad;
    boolean actualizarEditorial=false;
    boolean actualizarPeriodicidad;
    PeridiocidadDto peridiocidadDto;
    EditorialDto editorialDto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        androidFacade = new AndroidFacade(this);
        spinnerPer = findViewById(R.id.spinnerPer);
        spinnerEditorial = findViewById(R.id.spinnerEditorial);
        txtDescPer = findViewById(R.id.txtDescPer);
        txtDescTiempo = findViewById(R.id.txtDescTiempo);
        btnAceptarPer = findViewById(R.id.btnAceptarPer);
        btnAceptarEditorial = findViewById(R.id.btnAceptarEditorial);
        txtDescEditorial = findViewById(R.id.txtDescEditorial);


        spinnerEditorial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editorialDto= androidFacade.consultarEditorial(lstEditoriales.get(position));
                txtDescEditorial.setText(editorialDto.getNombre());
                actualizarEditorial = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                actualizarPeriodicidad=false;
            }
        });
        btnAceptarEditorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actualizarEditorial){
                    editorialDto.setNombre(txtDescEditorial.getText().toString());
                    androidFacade.actualizarEditorial(editorialDto);
                }else{
                    editorialDto = new EditorialDto(txtDescEditorial.getText().toString());
                    androidFacade.guardarEditorial(editorialDto);
                }
                obtenerListaEditoriales();
            }
        });

        obtenerListaPerodicidad();
        obtenerListaEditoriales();

        spinnerPer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                peridiocidadDto = androidFacade.consultarPeridiocidad(lstPeriodicidad.get(position));
                txtDescPer.setText(peridiocidadDto.getDescripcion());
                txtDescTiempo.setText(String.valueOf(peridiocidadDto.getTiempo()));
                actualizarPeriodicidad=true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                actualizarEditorial = false;
            }
        });
        btnAceptarPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actualizarPeriodicidad){
                    peridiocidadDto.setDescripcion(txtDescPer.getText().toString());
                    peridiocidadDto.setTiempo(Integer.parseInt(txtDescTiempo.getText().toString()));
                    peridiocidadDto.setDiasMeses(0);
                    androidFacade.actualizarPeridiocidad(peridiocidadDto);
                }else{
                    peridiocidadDto = new PeridiocidadDto(txtDescPer.getText().toString(),Integer.parseInt(txtDescTiempo.getText().toString()));
                    androidFacade.guardarPeridiocidad(peridiocidadDto);
                }
                obtenerListaPerodicidad();
            }
        });
        btnLimpiarPer = findViewById(R.id.btnLimpiarPer);
        btnLimpiarEd=findViewById(R.id.btnLimpiarEd);
        btnLimpiarPer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPeriodicidad=false;
                txtDescTiempo.setText(null);
                txtDescPer.setText(null);
            }
        });
        btnLimpiarEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarEditorial=false;
                txtDescEditorial.setText(null);
            }
        });
    }
    private void obtenerListaEditoriales(){
        lstEditoriales=androidFacade.consultarListaEditoriales();
        if(lstEditoriales != null){
            ArrayAdapter<String> editorialesAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,lstEditoriales);
            editorialesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEditorial.setAdapter(editorialesAdapter);
        }else{
            spinnerEditorial.setAdapter(null);
        }
    }
    private void obtenerListaPerodicidad(){
        lstPeriodicidad = androidFacade.consultarListaPeridiocidad();
        if(lstPeriodicidad != null){
            ArrayAdapter<String> periodicidadAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,lstPeriodicidad );
            periodicidadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerPer.setAdapter(periodicidadAdapter);
        }else {
            spinnerPer.setAdapter(null);
        }
    }
}
