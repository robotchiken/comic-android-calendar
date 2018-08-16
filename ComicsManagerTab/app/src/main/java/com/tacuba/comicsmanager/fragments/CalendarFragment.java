package com.tacuba.comicsmanager.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tacuba.comicsmanager.Constantes;
import com.tacuba.comicsmanager.R;
import com.tacuba.comicsmanager.adapters.CalendarAdapter;
import com.tacuba.comicsmanager.adapters.MultipleListSelectedAdapter;
import com.tacuba.comicsmanager.dao.AndroidFacade;
import com.tacuba.comicsmanager.dialogs.EditorComicsDialog;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.singletons.ShoppingCart;
import com.tacuba.comicsmanager.utils.MyDateUtils;
import com.tacuba.comicsmanager.utils.Utility;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import android.widget.AbsListView.MultiChoiceModeListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalendarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private ActionBar actionBar;
    private ListView listCalendarView;
    public CalendarAdapter adapter;
    public GregorianCalendar month;
    ArrayList<String> date;
    ArrayList<String> desc;
    protected String selectedGridDate;
    TextView title;
    MultiChoiceModeListener mMultiChoiceModeListener;
    private OnFragmentInteractionListener mListener;
    LinearLayout rLayout;
    private SimpleDateFormat mesFormat;
    private AndroidFacade androidFacade;
    private ArrayList<InfoComic> listSelected;
    OnShoppingCartSelectedListener mCallback;
    public CalendarFragment() {

    }

    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_calendar, container, false);
        Locale.setDefault(Locale.US);
        mesFormat = new SimpleDateFormat("MMMM yyyy",new Locale("es"));
        actionBar = getActivity().getActionBar();
        //actionBar.setTitle(MyDateUtils.mesActual());
        listCalendarView = (ListView) rootView.findViewById(R.id.listCalendarView);
        month = (GregorianCalendar) GregorianCalendar.getInstance();
        adapter = new CalendarAdapter(getActivity().getApplicationContext(), month);
        rLayout = (LinearLayout) rootView.findViewById(R.id.text);
        androidFacade = new AndroidFacade(getActivity());
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(adapter);
        listSelected = new ArrayList<InfoComic>();
        RelativeLayout previous = (RelativeLayout) rootView.findViewById(R.id.previous);

        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setPreviousMonth();
                refreshCalendar();
            }
        });

        RelativeLayout next = (RelativeLayout) rootView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setNextMonth();
                refreshCalendar();

            }
        });
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //private MultipleListSelectedAdapter listAdapter;

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                MultipleListSelectedAdapter listAdapter;
                // removing the previous view if added
                if (((LinearLayout) rLayout).getChildCount() > 0) {
                    ((LinearLayout) rLayout).removeAllViews();
                }
                desc = new ArrayList<String>();
                date = new ArrayList<String>();
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);
                selectedGridDate = CalendarAdapter.dayString.get(position);
                String[] separatedTime = selectedGridDate.split("-");
                String gridvalueString = separatedTime[2].replaceFirst("^0*",
                        "");// taking last part of date. ie; 2 from 2012-12-02.
                int gridvalue = Integer.parseInt(gridvalueString);
                // navigate to next or previous month on clicking offdays.
                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v);

                for (int i = 0; i < Utility.startDates.size(); i++) {
                    if (Utility.startDates.get(i).equals(selectedGridDate)) {
                        desc.add(Utility.nameOfEvent.get(i));
                    }
                }

                if (desc.size() > 0) {
                    for (int i = 0; i < desc.size(); i++) {
                        TextView rowTextView = new TextView(getActivity().getApplicationContext());

                        // set some properties of rowTextView or something
                        rowTextView.setText("Event:" + desc.get(i));
                        rowTextView.setTextColor(Color.BLACK);

                        // add the textview to the linearlayout
                        rLayout.addView(rowTextView);

                    }

                }

                ArrayList<InfoComic> listaComics = androidFacade.consultarComicsDiaMesDto(MyDateUtils.convertirFecha(selectedGridDate));

                if(listaComics != null){
                    if(listaComics.size() >= 2){
                        mListener.onFragmentInteraction(View.GONE);
                    }else{
                        mListener.onFragmentInteraction(View.VISIBLE);
                    }
                    listAdapter = new MultipleListSelectedAdapter(getActivity().getApplicationContext(), R.layout.comic_list_adapter, listaComics,true);
                    listCalendarView.setAdapter(listAdapter);
                }else{
                    //Toast.makeText(getActivity().getApplicationContext(),"No existen Comics en esa Fecha ",Toast.LENGTH_LONG).show();
                    listCalendarView.setAdapter(null);
                    mListener.onFragmentInteraction(View.VISIBLE);
                }


                desc = null;

            }

        });
        title = (TextView) rootView.findViewById(R.id.title);
        title.setText(StringUtils.capitalize(mesFormat.format(month.getTime())));

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               mostrarEditor();
                return true;
            }
        });

        listCalendarView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mMultiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                InfoComic infoComic = (InfoComic) listCalendarView.getAdapter().getItem(position);
                if(checked){
                    if(!listSelected.contains(infoComic)){
                        listSelected.add(infoComic);
                    }
                }else{
                    listSelected.remove(infoComic);
                }
                String strTotal = getResources().getString(R.string.strTotal)+": "+Utility.calcularTotal(listSelected);
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
            private void agregarCarrito() {
                mCallback.onShoppingCartSelected(listSelected);
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                listSelected = new ArrayList<InfoComic>();
            }


        };
        listCalendarView.setMultiChoiceModeListener(mMultiChoiceModeListener);

        return rootView;

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
        refreshCalendar();
        reiniciar();
    }

    private void comprarComics() {
        for(InfoComic infoComic : listSelected){
            androidFacade.comprarComic(infoComic);
        }

        setCustomAdapter();
        refreshCalendar();
        reiniciar();
    }

    private void mostrarEditor(){
        ArrayList<String> listEditorial = androidFacade.consultarListaEditoriales();

        ArrayList<String>listPeridiocidad =androidFacade.consultarListaPeridiocidad();

        EditorComicsDialog editorComicsDialog = new EditorComicsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("listEditorial",listEditorial);
        args.putStringArrayList("listPeridiocidad",listPeridiocidad);
        args.putBoolean(Constantes.ACTUALIZAR, false);
        args.putString("fechaSeleccionada",selectedGridDate);
        editorComicsDialog.setArguments(args);
        editorComicsDialog.show(getFragmentManager(), getResources().getString(R.string.strAgregarComics));
    }
    protected void setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1),
                    month.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,month.get(GregorianCalendar.MONTH) + 1);
        }
        setActionBarTitle(month);
        listCalendarView.setAdapter(null);
    }
    private void setActionBarTitle(GregorianCalendar mes){

    }
    protected void setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month
                .getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1),
                    month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            month.set(GregorianCalendar.MONTH,
                    month.get(GregorianCalendar.MONTH) - 1);
        }
        setActionBarTitle(month);
        //listCalendarView.setAdapter(null);
    }

    public void refreshCalendar() {
        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        //handler.post(calendarUpdater); // generate some calendar items
        title.setText(StringUtils.capitalize(mesFormat.format(month.getTime())));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setCustomAdapter(){
        if(selectedGridDate != null){
            MultipleListSelectedAdapter adapter=null;
            ArrayList<InfoComic> listaComics = androidFacade.consultarComicsDiaMesDto(MyDateUtils.convertirFecha(selectedGridDate));
            if(listaComics != null){
                adapter = new MultipleListSelectedAdapter(getActivity().getApplicationContext(), R.layout.comic_list_adapter, listaComics,true);;
            }
            listCalendarView.setAdapter(adapter);
        }
    }

    public void reiniciar(){
        listSelected = new ArrayList<InfoComic>();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int modo);
    }

    public interface OnShoppingCartSelectedListener {
        public void onShoppingCartSelected(ArrayList<InfoComic> selected);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnShoppingCartSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnShoppingCartSelectedListener");
        }
    }
}
