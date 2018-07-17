package com.tacuba.comicsmanager;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;


import com.tacuba.comicsmanager.activity.ConfigurationActivity;
import com.tacuba.comicsmanager.dao.AndroidFacade;
import com.tacuba.comicsmanager.dialogs.EditorComicsDialog;
import com.tacuba.comicsmanager.dto.ComicDto;
import com.tacuba.comicsmanager.dto.EditorialDto;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.dto.PeridiocidadDto;
import com.tacuba.comicsmanager.fragments.CalendarFragment;
import com.tacuba.comicsmanager.fragments.EnEsperaFragment;
import com.tacuba.comicsmanager.fragments.PendientesFragment;
import com.tacuba.comicsmanager.fragments.ShoppingCartFragment;
import com.tacuba.comicsmanager.utils.MyDateUtils;
import com.tacuba.comicsmanager.utils.Utility;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements CalendarFragment.OnFragmentInteractionListener,
        EditorComicsDialog.EditorComicsDialogListener,CalendarFragment.OnShoppingCartSelectedListener,
       PendientesFragment.OnListFragmentInteractionListenerPendientes,ShoppingCartFragment.OnListFragmentInteractionListener,
EnEsperaFragment.OnListFragmentInteractionListenerEnEspera{

    private TextView mTextMessage;
    private AndroidFacade androidFacade;
    private static final int EDIT_REQUEST_CODE = 44;
    private static final int WRITE_REQUEST_CODE = 43;
    private static final int REQUEST_PATH = 1;
    private String tipoAccion;
    BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment =  CalendarFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_dashboard:
                   fragment = ShoppingCartFragment.newInstance();
                   loadFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    fragment = PendientesFragment.newInstance();
                    loadFragment(fragment);
                    return true;
                case R.id.action_espera:
                    fragment = EnEsperaFragment.newInstance();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidFacade = new AndroidFacade(this);
        mTextMessage = (TextView) findViewById(R.id.message);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(CalendarFragment.newInstance());
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void actualizarFragment() {
        Object tmp = getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if(tmp instanceof CalendarFragment){
            CalendarFragment calendarFragment = (CalendarFragment) tmp;
            calendarFragment.refreshCalendar();
            calendarFragment.setCustomAdapter();
            calendarFragment.reiniciar();
        }else if(tmp instanceof  ShoppingCartFragment){
            ShoppingCartFragment shoppingCartFragment = (ShoppingCartFragment)tmp;
            shoppingCartFragment.setCustomAdapter();
            shoppingCartFragment.reiniciar();
        }else if(tmp instanceof PendientesFragment){
            PendientesFragment pendientesFragment =(PendientesFragment)tmp;
            pendientesFragment.setCustomAdapter();
            pendientesFragment.reiniciar();
        }else{
            EnEsperaFragment enEsperaFragment =(EnEsperaFragment)tmp;
            enEsperaFragment.setCustomAdapter();
            enEsperaFragment.reiniciar();
        }
    }
    @Override
    public void onFragmentInteraction(int modo) {
        navigation.setVisibility(modo);
    }
    private void mostrarBarraNavegacion(int dy){
        if (dy > 0 && navigation.isShown()) {
            navigation.setVisibility(View.GONE);
        } else if (dy < 0 ) {
            navigation.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_restaurar:
                performFileSearch();
                break;
            case R.id.action_respaldar:
                respaldar();
                break;
            case R.id.action_configurar:
                configurar();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void configurar() {
        Intent intent1 = new Intent(this, ConfigurationActivity.class);
        startActivity(intent1);
    }

    private void respaldar() {
        String fileName="Respaldo"+MyDateUtils.mesActual()+".xls";
        createFile(fileName);
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        tipoAccion=Intent.ACTION_OPEN_DOCUMENT;
        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
       // intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.setType("application/vnd.ms-excel");
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }

    private void createFile(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        tipoAccion=Intent.ACTION_CREATE_DOCUMENT;
        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent resultData) {

        if(resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                if(tipoAccion.equals(Intent.ACTION_OPEN_DOCUMENT)){
                    leerExcel(uri);
                    actualizarFragment();
                }else{
                    crearExcel(uri);
                }

            }
        }
    }

    private void crearExcel(Uri uri){
        try {
            ParcelFileDescriptor parcelFileDescriptor = null;
            parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "w");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            FileOutputStream fileOut = new FileOutputStream(fileDescriptor);
            HSSFWorkbook workbook = new HSSFWorkbook();

            ArrayList<ComicDto>listaComics = androidFacade.respaldoComics();
            ArrayList<EditorialDto>listaEditorial = androidFacade.respaldoEditoriales();
            ArrayList<PeridiocidadDto>listaPeridiocidad = androidFacade.respaldoPeridiocidad();

            HSSFSheet worksheetComics = workbook.createSheet("Comics");
            int rowPosition =1;
            HSSFCellStyle style = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setFontName(HSSFFont.FONT_ARIAL);
            font.setFontHeightInPoints((short)10);
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            if(listaComics != null){
                HSSFRow row;
                row = worksheetComics.createRow((short) 0);
                HSSFCell cellA1 = row.createCell((short) 0);
                cellA1.setCellValue("Titulo");
                cellA1.setCellStyle(style);
                HSSFCell cellA2 = row.createCell((short) 1);
                cellA2.setCellValue("Fecha");
                cellA2.setCellStyle(style);
                HSSFCell cellA3 = row.createCell((short) 2);
                cellA3.setCellValue("Numero");
                cellA3.setCellStyle(style);
                HSSFCell cellA4 = row.createCell((short) 3);
                cellA4.setCellValue("Precio");
                cellA4.setCellStyle(style);
                HSSFCell cellA5 = row.createCell((short) 4);
                cellA5.setCellValue("Peridiocidad");
                cellA5.setCellStyle(style);
                HSSFCell cellA6 = row.createCell((short) 5);
                cellA6.setCellValue("Editorial");
                cellA6.setCellStyle(style);
                HSSFCell cellA7 = row.createCell((short) 6);
                cellA7.setCellValue("Numero Final");
                cellA7.setCellStyle(style);
                HSSFCell cellA8 = row.createCell((short) 7);
                cellA8.setCellValue("En publicacion");
                cellA8.setCellStyle(style);
                  for(ComicDto tmp : listaComics){
                      row = worksheetComics.createRow((short) rowPosition);
                      HSSFCell cell1 = row.createCell((short) 0);
                      cell1.setCellValue(tmp.getTitulo());
                      HSSFCell cell2 = row.createCell((short) 1);
                      cell2.setCellValue(tmp.getFecha());
                      HSSFCell cell3 = row.createCell((short) 2);
                      cell3.setCellValue(tmp.getNumero());
                      HSSFCell cell4 = row.createCell((short) 3);
                      cell4.setCellValue(tmp.getPrecio());
                      HSSFCell cell5 = row.createCell((short) 4);
                      cell5.setCellValue(tmp.getPeridiocidad());
                      HSSFCell cell6 = row.createCell((short) 5);
                      cell6.setCellValue(tmp.getEditorial());
                      HSSFCell cell7 = row.createCell((short) 6);
                      cell7.setCellValue(tmp.getNumeroFinal());
                      HSSFCell cell8 = row.createCell((short) 7);
                      cell8.setCellValue(tmp.getEnPublicacion());
                      rowPosition++;
                  }
            }
            rowPosition=1;
            HSSFSheet worksheetPeriodicidad = workbook.createSheet("Peridiocidad");
            if(listaPeridiocidad!= null){
                HSSFRow row;
                row = worksheetPeriodicidad.createRow((short) 0);
                HSSFCell cellA1 = row.createCell((short) 0);
                cellA1.setCellValue("Dias Meses");
                cellA1.setCellStyle(style);
                HSSFCell cellA2 = row.createCell((short) 1);
                cellA2.setCellValue("Descripcion");
                cellA2.setCellStyle(style);
                HSSFCell cellA3 = row.createCell((short) 2);
                cellA3.setCellValue("Tiempo");
                cellA3.setCellStyle(style);
                for(PeridiocidadDto tmp : listaPeridiocidad){
                    row = worksheetPeriodicidad.createRow((short) rowPosition);
                    HSSFCell cell1 = row.createCell((short) 0);
                    cell1.setCellValue(tmp.getDiasMeses());
                    HSSFCell cell2 = row.createCell((short) 1);
                    cell2.setCellValue(tmp.getDescripcion());
                    HSSFCell cell3 = row.createCell((short) 2);
                    cell3.setCellValue(tmp.getTiempo());
                    rowPosition++;
                }
            }

            rowPosition=1;
            HSSFSheet worksheetEditoriales = workbook.createSheet("Editoriales");
            if(listaEditorial!= null){
                HSSFRow row;
                row = worksheetEditoriales.createRow((short) 0);
                HSSFCell cellA1 = row.createCell((short) 0);
                cellA1.setCellValue("Nombre");
                cellA1.setCellStyle(style);
                for(EditorialDto tmp : listaEditorial){
                    row = worksheetEditoriales.createRow((short) rowPosition);
                    HSSFCell cell1 = row.createCell((short) 0);
                    cell1.setCellValue(tmp.getNombre());
                    rowPosition++;
                }
            }
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void leerExcel(Uri uri){
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            FileInputStream fileInputStream = new FileInputStream(fileDescriptor);
            POIFSFileSystem myFileSystem = new POIFSFileSystem(fileInputStream);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            //Se obtiene la periodicidad
            HSSFSheet periodicidadSheet = myWorkBook.getSheet("Peridiocidad");
            Iterator rowIterPeriodicidad = periodicidadSheet.rowIterator();
            rowIterPeriodicidad.next();// se quita el primer renglon
            ArrayList<PeridiocidadDto>listaPeriodicidad  = new ArrayList<PeridiocidadDto>();
            while(rowIterPeriodicidad.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIterPeriodicidad.next();
                int id= (int)myRow.getCell(0).getNumericCellValue();
                String descripcion= myRow.getCell(1).getStringCellValue();
                int tiempo= (int)myRow.getCell(2).getNumericCellValue();
                listaPeriodicidad.add(new PeridiocidadDto(id,descripcion,tiempo));
            }
            //Se obtiene lista de editoriales
            HSSFSheet editorialesSheet = myWorkBook.getSheet("Editoriales");
            Iterator rowIterEditoriales = editorialesSheet.rowIterator();
            rowIterEditoriales.next();// se quita el primer renglon
            ArrayList<EditorialDto>listaEditoriales  = new ArrayList<EditorialDto>();
            while(rowIterEditoriales.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIterEditoriales.next();
                String nombreEditorial= myRow.getCell(0).getStringCellValue();
                listaEditoriales.add(new EditorialDto(nombreEditorial));
            }

           //Se obtiene los comics
            HSSFSheet comicsSheet = myWorkBook.getSheet("Comics");
            Iterator rowItercomics = comicsSheet.rowIterator();
            rowItercomics.next();// se quita el primer renglon
            ArrayList<ComicDto>listaComics  = new ArrayList<ComicDto>();
            while(rowItercomics.hasNext()){
                HSSFRow myRow = (HSSFRow) rowItercomics.next();
                String titulo =myRow.getCell(0).getStringCellValue();
                String fecha = myRow.getCell(1).getStringCellValue();
                int numero = (int)myRow.getCell(2).getNumericCellValue();
                float precio = (float) myRow.getCell(3).getNumericCellValue();
                int peridiocidad = (int) myRow.getCell(4).getNumericCellValue();
                int editorial = (int) myRow.getCell(5).getNumericCellValue();
                int numeroFinal = (int) myRow.getCell(6).getNumericCellValue();
                int enPublicacion  = (int) myRow.getCell(7).getNumericCellValue();

                if(!titulo.trim().equals("") && !fecha.trim().equals("") ){
                    listaComics.add(new ComicDto(0,titulo,fecha,numero,precio,peridiocidad,editorial,numeroFinal, enPublicacion));
                }
            }
            androidFacade.restaurarInformacion(listaComics,listaEditoriales,listaPeriodicidad);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComicsEditorDialogNegativeClick(DialogFragment dialog) {
        actualizarFragment();
    }

    @Override
    public void onComicsEditorDialogNeutralClick(String titulo, String fecha, int numero, float precio, String strPeridiocidad, String strEditorial, int numeroFinal, int comicID,int enPublicacion) {
        int peridiocidad=androidFacade.consultarPeridiocidad(strPeridiocidad).getId();
        int editorial= androidFacade.consultarEditorial(strEditorial).getId();
        ComicDto comicDto = new ComicDto(comicID, titulo, fecha, numero,precio, peridiocidad, editorial, numeroFinal, enPublicacion);
        androidFacade.actualizarComic(comicDto);
        Utility.mostrarMensaje(this,"Se actualizo el comic: "+titulo);
        actualizarFragment();
    }
    @Override
    public void onComicsEditorDialogPositiveClick(String titulo, String fecha, int numero, float precio, String strPeridiocidad, String strEditorial, int numeroFinal, int comicID, boolean actualizar,int enPublicacion) {
        int peridiocidad=androidFacade.consultarPeridiocidad(strPeridiocidad).getId();
        int editorial= androidFacade.consultarEditorial(strEditorial).getId();
        ComicDto comicDto = new ComicDto(comicID, titulo, fecha, numero,precio, peridiocidad, editorial, numeroFinal, enPublicacion);
        if(actualizar) {
            androidFacade.actualizarComic(comicDto);
            Utility.mostrarMensaje(this,"Se actualizo el comic: "+titulo);
        }else{
            androidFacade.guardarComic(comicDto);
            Utility.mostrarMensaje(this,"Se agrego el comic: "+titulo);
        }
        actualizarFragment();
    }

    @Override
    public void onListFragmentInteraction(InfoComic item) {

    }

    @Override
    public void onShoppingCartSelected(ArrayList<InfoComic> selected) {
        for(InfoComic tmp : selected) {
            androidFacade.agregarAlCarrito(tmp.getComicDto());
        }
    }

    @Override
    public void onListFragmentInteractionPendientes(int dy) {
        mostrarBarraNavegacion(dy);
    }

    @Override
    public void onShoppingCartSelectedPendientes(ArrayList<InfoComic> selected) {
        for(InfoComic tmp : selected) {
            androidFacade.agregarAlCarrito(tmp.getComicDto());
        }
    }

    @Override
    public void OnListFragmentInteractionListenerEnEspera(int dy) {
        mostrarBarraNavegacion(dy);
    }

}
