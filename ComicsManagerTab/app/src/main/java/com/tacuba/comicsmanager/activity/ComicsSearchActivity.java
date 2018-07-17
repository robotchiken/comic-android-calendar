package com.tacuba.comicsmanager.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tacuba.comicsmanager.Constantes;
import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.adapters.MultipleListSelectedAdapter;
import com.tacuba.comicsmanager.dao.AndroidFacade;
import com.tacuba.comicsmanager.dialogs.EditorComicsDialog;
import com.tacuba.comicsmanager.dto.ComicDto;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.fragments.CalendarFragment;
import com.tacuba.comicsmanager.singletons.ShoppingCart;
import com.tacuba.comicsmanager.utils.Utility;

import java.util.ArrayList;

public class ComicsSearchActivity extends AppCompatActivity implements EditorComicsDialog.EditorComicsDialogListener {

    private ListView listViewResultSearch;
    private AndroidFacade androidFacade;
    AbsListView.MultiChoiceModeListener mMultiChoiceModeListener;
    private ArrayList<InfoComic> listSelected;
    private float total;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comics_search_layout);
        listViewResultSearch = (ListView) findViewById(R.id.listViewResultSearch);
        listViewResultSearch.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listSelected = new ArrayList<InfoComic>();
        mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                InfoComic infoComic = (InfoComic) listViewResultSearch.getAdapter().getItem(position);
                if(checked){
                    if(!listSelected.contains(infoComic)){
                        listSelected.add(infoComic);
                    }
                    total+=infoComic.getComicDto().getPrecio();
                }else{
                    listSelected.remove(infoComic);
                    total-=infoComic.getComicDto().getPrecio();
                }
                String strTotal = getResources().getString(R.string.strTotal)+": "+total;
                mode.setTitle(strTotal);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.context_menu, menu);
                menu.findItem(R.id.action_borrar_todo).setVisible(false);
                menu.findItem(R.id.action_remove_cart).setVisible(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_comprar:
                        mode.finish();
                        comprarComics();
                        break;
                    case R.id.action_borrar:
                        mode.finish();
                        borrarComics();
                        break;
                    case R.id.action_editar:
                        mode.finish();
                        editarComic();
                        break;
                    case R.id.action_cart:
                        mode.finish();
                        agregarCarrito();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        };
        androidFacade = new AndroidFacade(this);
        // get action bar
        ActionBar actionBar = getSupportActionBar();
        listViewResultSearch.setMultiChoiceModeListener(mMultiChoiceModeListener);
        // Enabling Back navigation on Action Bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
    }
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);

            /**
             * Use this query to display search results like
             * 1. Getting the data from SQLite and showing in listview
             * 2. Making webrequest and displaying the data
             * For now we just display the query only
             */
            searchComics(query);
        }
    }

    private void searchComics(String strQuery){
        MultipleListSelectedAdapter listAdapter;

        ArrayList<InfoComic> listaComics = androidFacade.searchWidget(strQuery);

        if(listaComics != null && listaComics.size() > 0 ){
            listAdapter = new MultipleListSelectedAdapter(this, R.layout.comic_list_adapter, listaComics,true);
            listViewResultSearch.setAdapter(listAdapter);
        }else{
            Utility.mostrarMensaje(this,"No se encontraron Comics");
            listViewResultSearch.setAdapter(null);
        }
    }

    private void agregarCarrito() {
        ShoppingCart shoppingCart = (ShoppingCart) getApplicationContext();
        shoppingCart.setShoppingCart(listSelected);
        for(InfoComic infoComic : listSelected) {
            androidFacade.agregarAlCarrito(infoComic.getComicDto());
        }
    }
    private void editarComic() {
        ArrayList<String> listEditorial = androidFacade.consultarListaEditoriales();

        ArrayList<String>listPeridiocidad =androidFacade.consultarListaPeridiocidad();
        ShoppingCart shoppingCart = (ShoppingCart) getApplicationContext();
        shoppingCart.setEditComicList(listSelected);
        EditorComicsDialog editorComics = new EditorComicsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("listEditorial",listEditorial);
        args.putStringArrayList("listPeridiocidad",listPeridiocidad);
        args.putBoolean(Constantes.ACTUALIZAR, true);
        editorComics.setArguments(args);
        editorComics.show(getSupportFragmentManager(), getResources().getString(R.string.strEditarComic));
    }

    private void borrarComics() {
        for(InfoComic item : listSelected){
            androidFacade.borrarComic(item.getComicDto());
        }
        refreshCalendar();
        searchComics(query);
        reiniciar();
    }
    private void refreshCalendar() {
        Object tmp = getSupportFragmentManager().findFragmentById(R.id.container);
        if(tmp instanceof CalendarFragment){
            CalendarFragment calendarFragment = (CalendarFragment) tmp;
            calendarFragment.refreshCalendar();
            calendarFragment.reiniciar();
        }
    }

    private void comprarComics() {
        for(InfoComic infoComic : listSelected){
            androidFacade.comprarComic(infoComic);
        }
        refreshCalendar();
        searchComics(query);
        reiniciar();
    }
    public void reiniciar(){
        listSelected = new ArrayList<InfoComic>();
    }

    @Override
    public void onComicsEditorDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onComicsEditorDialogNeutralClick(String titulo, String fecha, int numero, float precio, String strPeridiocidad, String strEditorial, int numeroFinal, int comicID, int enPublicacion) {
        int peridiocidad=androidFacade.consultarPeridiocidad(strPeridiocidad).getId();
        int editorial= androidFacade.consultarEditorial(strEditorial).getId();
        ComicDto comicDto = new ComicDto(comicID, titulo, fecha, numero,precio, peridiocidad, editorial, numeroFinal, enPublicacion);
        androidFacade.actualizarComic(comicDto);
        refreshCalendar();
        reiniciar();
        searchComics(query);
    }

    @Override
    public void onComicsEditorDialogPositiveClick(String titulo, String fecha, int numero, float precio, String strPeridiocidad, String strEditorial, int numeroFinal, int comicID, boolean actualizar,int enPublicacion) {
        int peridiocidad=androidFacade.consultarPeridiocidad(strPeridiocidad).getId();
        int editorial= androidFacade.consultarEditorial(strEditorial).getId();
        ComicDto comicDto = new ComicDto(comicID, titulo, fecha, numero,precio, peridiocidad, editorial, numeroFinal, enPublicacion);
        if(actualizar) {
            androidFacade.actualizarComic(comicDto);
        }else{
            androidFacade.guardarComic(comicDto);
        }
        refreshCalendar();
        reiniciar();
        searchComics(query);
    }
}
