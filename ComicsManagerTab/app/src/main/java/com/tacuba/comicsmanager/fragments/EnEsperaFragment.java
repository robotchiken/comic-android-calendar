package com.tacuba.comicsmanager.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.tacuba.comicsmanager.Constantes;
import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.adapters.MultipleListSelectedAdapter;
import com.tacuba.comicsmanager.dao.AndroidFacade;
import com.tacuba.comicsmanager.dialogs.EditorComicsDialog;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.singletons.ShoppingCart;
import com.tacuba.comicsmanager.utils.MyDateUtils;
import com.tacuba.comicsmanager.utils.Utility;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnEsperaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnEsperaFragment extends Fragment {

    AndroidFacade androidFacade;
    ListView listView;
    AbsListView.MultiChoiceModeListener mMultiChoiceModeListener;
    private ArrayList<InfoComic> listSelected;
    private OnListFragmentInteractionListenerEnEspera mListener;
    public EnEsperaFragment() {

    }


    public static EnEsperaFragment newInstance() {
        EnEsperaFragment fragment = new EnEsperaFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_en_espera, container, false);
        androidFacade = new AndroidFacade(container.getContext());
        listView = view.findViewById(R.id.listViewEspera);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listSelected = new ArrayList<InfoComic>();
        mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                InfoComic infoComic = (InfoComic) listView.getAdapter().getItem(position);
                if(checked){
                    if(!listSelected.contains(infoComic)){
                        listSelected.add(infoComic);
                    }
                }else{
                    listSelected.remove(infoComic);
                }
                String strTotal = getResources().getString(R.string.strTotal)+": "+ Utility.calcularTotal(listSelected);
                mode.setTitle(strTotal);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.context_menu, menu);
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
                        comprarComics();
                        break;
                    case R.id.action_borrar:
                        borrarComics();
                        break;
                    case R.id.action_editar:
                        editarComic();
                        break;
                    case R.id.action_cart:
                        agregarCarrito();
                        break;
                }
                mode.finish();
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listSelected = new ArrayList<InfoComic>();
            }
        };
        listView.setMultiChoiceModeListener(mMultiChoiceModeListener);
        ArrayList<InfoComic> listaComicsEnEspera = androidFacade.consultarComicsEnEpera();
        if(listaComicsEnEspera != null) {
            MultipleListSelectedAdapter adapter = new MultipleListSelectedAdapter(container.getContext(), R.layout.comic_list_adapter, listaComicsEnEspera, false);
            listView.setAdapter(adapter);
        }else{
            listView.setAdapter(null);
        }
        listView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                mListener.OnListFragmentInteractionListenerEnEspera(scrollY);
            }
        });
        return view;
    }

    private void agregarCarrito() {
        for(InfoComic item : listSelected) {
            androidFacade.agregarAlCarrito(item.getComicDto());
        }
    }
    private void editarComic() {
        ArrayList<String> listEditorial = androidFacade.consultarListaEditoriales();

        ArrayList<String>listPeridiocidad =androidFacade.consultarListaPeridiocidad();
        ShoppingCart shoppingCart = (ShoppingCart) getActivity().getApplicationContext();
        shoppingCart.setEditComicList(listSelected);
        EditorComicsDialog editorComics = new EditorComicsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("listEditorial",listEditorial);
        args.putStringArrayList("listPeridiocidad",listPeridiocidad);
        args.putBoolean(Constantes.ACTUALIZAR, true);
        editorComics.setArguments(args);
        editorComics.show(getFragmentManager(), getResources().getString(R.string.strEditarComic));
    }

    private void borrarComics() {
        for(InfoComic item : listSelected){
            androidFacade.borrarComic(item.getComicDto());
        }
        setCustomAdapter();
        reiniciar();
    }

    public void setCustomAdapter() {
        MultipleListSelectedAdapter adapter=null;
        ArrayList<InfoComic> listaComics = androidFacade.consultarComicsEnEpera();
        if(listaComics != null){
            adapter = new MultipleListSelectedAdapter(getActivity().getApplicationContext(), R.layout.comic_list_adapter, listaComics);
        }
        listView.setAdapter(adapter);
    }

    private void comprarComics() {
        for(InfoComic infoComic : listSelected){
            androidFacade.comprarComic(infoComic);
        }

        setCustomAdapter();
        reiniciar();
    }
    public void reiniciar(){
        listSelected = new ArrayList<InfoComic>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListenerEnEspera) {
            mListener = (OnListFragmentInteractionListenerEnEspera) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListenerEnEspera {
        void OnListFragmentInteractionListenerEnEspera(int item);
    }
}
