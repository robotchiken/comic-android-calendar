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
import com.tacuba.comicsmanager.utils.Utility;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShoppingCartFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private ArrayList<InfoComic> shoppingList;
    AndroidFacade androidFacade;
    ListView listView;
    AbsListView.MultiChoiceModeListener mMultiChoiceModeListener;
    private ArrayList<InfoComic> listSelected;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingCartFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShoppingCartFragment newInstance() {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shoppingcart_list, container, false);
        listView = view.findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        androidFacade = new AndroidFacade(getActivity());
        MultipleListSelectedAdapter adapter=null;
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
                menu.findItem(R.id.action_cart).setVisible(false);
                menu.findItem(R.id.action_borrar).setVisible(false);
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
                    case R.id.action_remove_cart:
                        borrarComics();
                        break;
                    case R.id.action_editar:
                        editarComic();
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
        ArrayList<InfoComic> listaComics = androidFacade.consultarCarrito();
        if(listaComics != null){
            adapter = new MultipleListSelectedAdapter(getActivity().getApplicationContext(), R.layout.comic_list_adapter, listaComics);
        }
        listView.setAdapter(adapter);
        return view;
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
            androidFacade.borrarCarritoCompras(item.getComicDto().getId());
        }
        setCustomAdapter();
        reiniciar();
    }

    public void setCustomAdapter() {
        MultipleListSelectedAdapter adapter=null;
        ArrayList<InfoComic> listaComics = androidFacade.consultarCarrito();
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
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(InfoComic item);

        void onShoppingCartSelected(ArrayList<InfoComic> listSelected);
    }
}
