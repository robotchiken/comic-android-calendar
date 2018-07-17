package com.tacuba.comicsmanager.dao;

import java.util.ArrayList;

import com.tacuba.comicsmanager.Constantes;
import com.tacuba.comicsmanager.dto.ComicDto;
import com.tacuba.comicsmanager.dto.EditorialDto;
import com.tacuba.comicsmanager.dto.InfoComic;
import com.tacuba.comicsmanager.dto.PeridiocidadDto;
import com.tacuba.comicsmanager.utils.MyDateUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AndroidFacade {
	MySQLiteHelper usdbh;
	private SQLiteDatabase db;
    private final String CAMPOS_COMICDTO = "c.id,c.fecha, p.tiempo,c.titulo,e.nombre,c.numero,c.precio,c.content,p.descripcion,p.id,c.numeroFinal,p.diasMeses,c.enPublicacion";
	private Context context;
	public AndroidFacade(Context context){
		usdbh =new MySQLiteHelper(context, Constantes.DB_NAME, null, Constantes.VERSION_DB);
		db = usdbh.getWritableDatabase();
		this.context = context;
	}
    public void guardarComic(ComicDto comicDto){
        if(comicDto.getNumeroFinal() > 0){
            db.execSQL("INSERT INTO comics (titulo,fecha,numero,precio,peridiocidad,content,numeroFinal,enPublicacion) " +
                    "VALUES ('"+comicDto.getTitulo()+
                    "','"+comicDto.getFecha()+
                    "',"+comicDto.getNumero()+
                    ","+comicDto.getPrecio()+
                    ","+comicDto.getPeridiocidad()+
                    ","+comicDto.getEditorial()+
                    ","+comicDto.getNumeroFinal()
                    +","+comicDto.getEnPublicacion()+")");
        }else{
            db.execSQL("INSERT INTO comics (titulo,fecha,numero,precio,peridiocidad,content,enPublicacion) " +
                    "VALUES ('"+comicDto.getTitulo()+
                    "','"+comicDto.getFecha()+
                    "',"+comicDto.getNumero()+
                    ","+comicDto.getPrecio()+
                    ","+comicDto.getPeridiocidad()+
                    ","+comicDto.getEditorial()+
                    ","+comicDto.getEnPublicacion()+")");
        }
    }

    public void guardarPeridiocidad(PeridiocidadDto item){
        db.execSQL("INSERT INTO peridiocidad (descripcion,tiempo,diasMeses) VALUES ('"+item.getDescripcion()+"',"+item.getTiempo()+","+item.getDiasMeses()+")");
    }

    public void actualizarPeridiocidad(PeridiocidadDto item){
        db.execSQL("UPDATE peridiocidad SET descripcion ='"+item.getDescripcion()+"', tiempo ="+item.getTiempo()+", diasMeses="+item.getDiasMeses()+" WHERE id ="+item.getId());
    }

    public void guardarEditorial(EditorialDto editorial){
        db.execSQL("INSERT INTO editoriales (nombre) VALUES ('"+editorial.getNombre()+"')");
    }

    public void actualizarEditorial(EditorialDto item){
        db.execSQL("UPDATE editoriales SET nombre ='"+item.getNombre()+"' WHERE content = "+item.getId());
    }

    public String[] buscarComicsAutoComplete(){
        String listaComics[]=null;
        Cursor c = db.rawQuery("SELECT titulo FROM comics ORDER BY titulo ASC",null);
        int i = 0;
        if (c.moveToFirst()){
            listaComics = new String[c.getCount()] ;
            do {
                String titulo= c.getString(0);
                listaComics[i++]=titulo;
            } while(c.moveToNext());
        }
        return listaComics;
    }

    public void restaurarInformacion(ArrayList<ComicDto> listaComics, ArrayList<EditorialDto> listaEditoriales, ArrayList<PeridiocidadDto> listaPeridiocidad){
        borrarTablas();
        crearTablas();
        for(ComicDto comicDto : listaComics){
            guardarComic(comicDto);
        }

        for(EditorialDto editorialDto : listaEditoriales){
            guardarEditorial(editorialDto);
        }

        for(PeridiocidadDto peridiocidadDto : listaPeridiocidad){
            guardarPeridiocidad(peridiocidadDto);
        }
    }

    private void borrarTablas(){
        db.execSQL("DROP TABLE IF EXISTS comics");
        db.execSQL("DROP TABLE IF EXISTS editoriales");
        db.execSQL("DROP TABLE IF EXISTS peridiocidad");
        db.execSQL("DROP TABLE IF EXISTS carritoCompras");
    }

    private void crearTablas(){
        db.execSQL(Constantes.sqlCreatComicsTable);
        db.execSQL(Constantes.sqlCreateEditorialTable);
        db.execSQL(Constantes.sqlCreatePeridiocidadTable);
        db.execSQL(Constantes.sqlCreateShoppingCart);
    }

    public  ArrayList<InfoComic> consultarComicsDiaMesDto(String fechaActual){
        ArrayList<InfoComic> comicArray = null;
        String condition= (fechaActual == null ? "" : "c.fecha = '"+fechaActual+"' AND ");
        String sql = "SELECT "+CAMPOS_COMICDTO+" FROM comics c, peridiocidad p, editoriales e WHERE "+condition+"p.id = c.peridiocidad AND e.content = c.content AND c.enPublicacion=1 ORDER BY fecha ASC";
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            comicArray = new ArrayList<InfoComic>();
            do{
                comicArray.add(crearComicDto(c));
            }while(c.moveToNext());
            c.close();
        }
        return comicArray;
    }

    public InfoComic consultarComicsId(int id){
        Cursor c = db.rawQuery("SELECT "+CAMPOS_COMICDTO+" FROM comics c, peridiocidad p, editoriales e WHERE c.id="+id+" AND p.id = c.peridiocidad AND e.content = c.content ORDER BY fecha ASC", null);
        InfoComic infoComic=null;
        if(c.moveToFirst()){
            infoComic=crearComicDto(c);
            c.close();
        }
        return infoComic;
    }

    public InfoComic consultarComicsTitulo(String titulo){
        Cursor c = db.rawQuery("SELECT "+CAMPOS_COMICDTO+" FROM comics c, peridiocidad p, editoriales e WHERE c.id='"+titulo+"' AND p.id = c.peridiocidad AND e.content = c.content ORDER BY fecha ASC", null);
        InfoComic infoComic=null;
        if(c.moveToFirst()){
            infoComic=crearComicDto(c);
            c.close();
        }
        return infoComic;
    }
    public ArrayList<InfoComic> consultarComicsEnEpera(){
        ArrayList<InfoComic> comicArray = null;
        Cursor c = db.rawQuery("SELECT "+CAMPOS_COMICDTO+" FROM comics c, peridiocidad p, editoriales e WHERE c.enPublicacion=0 AND p.id = c.peridiocidad AND e.content = c.content ORDER BY fecha ASC", null);
        if(c.moveToFirst()){
            comicArray = new ArrayList<InfoComic>();
            do{
                comicArray.add(crearComicDto(c));
            }while(c.moveToNext());
            c.close();
        }
        return comicArray;
    }

    private InfoComic crearComicDto(Cursor c){
        InfoComic infoComic;

        int  comicId= c.getInt(0);
        String comicfecha = c.getString(1);
        int perTiempo= c.getInt(2);
        String comicTitulo= c.getString(3);
        String editorialNombre = c.getString(4);
        int comicNumero = c.getInt(5);
        float comicPrecio = c.getFloat(6);
        int editorialId=c.getInt(7);
        String perDescripcion = c.getString(8);
        int perId = c.getInt(9);
        int comicNumeroFinal = c.getInt(10);
        int perDiasMeses = c.getInt(11);
        int enPublicacion = c.getInt(12);
        ComicDto comicDto = new ComicDto(comicId,comicTitulo,comicfecha,comicNumero,comicPrecio,perId,editorialId,comicNumeroFinal, enPublicacion);
        EditorialDto editorialDto = new EditorialDto(editorialId,editorialNombre);
        PeridiocidadDto peridiocidadDto = new PeridiocidadDto(perId,perDiasMeses,perDescripcion,perTiempo);
        infoComic = new InfoComic(comicDto,editorialDto,peridiocidadDto);

        return infoComic;
    }

    public EditorialDto consultarEditorial(String item){
        EditorialDto res = null;
        Cursor c = db.rawQuery("SELECT content,nombre FROM editoriales WHERE nombre='"+item+"'", null);
        if(c.moveToFirst()){
            res = new EditorialDto(c.getInt(0),c.getString(1));
        }
        return res;
    }

    public PeridiocidadDto consultarPeridiocidad(String item){
        PeridiocidadDto res = null;
        Cursor c = db.rawQuery("SELECT id,diasMeses, descripcion,tiempo FROM peridiocidad WHERE descripcion='"+item+"'", null);
        if(c.moveToFirst()){
            res = new PeridiocidadDto(c.getInt(0),c.getInt(1),c.getString(2),c.getInt(3));
        }
        return res;
    }

    public String [] consultarComicPeridiocidad(int id){
        String listaComics[]=null;
        Cursor c = db.rawQuery("SELECT titulo FROM comics where peridiocidad="+id,null);
        int i = 0;
        if (c.moveToFirst()){
            listaComics = new String[c.getCount()] ;
            do {
                String titulo= c.getString(0);
                listaComics[i++]=titulo;
            } while(c.moveToNext());
        }
        return listaComics;
    }

    public ArrayList<String> consultarListaPeridiocidad(){
        ArrayList<String> list = null;
        Cursor c = db.rawQuery("SELECT descripcion FROM peridiocidad ORDER BY descripcion ASC", null);
        if(c.moveToFirst()){
            list = new ArrayList<String>();
            do {
                list.add(c.getString(0));
            } while(c.moveToNext());
        }
        return list;
    }

    public ArrayList<String> consultarListaEditoriales(){
        ArrayList<String> list = null;
        Cursor c = db.rawQuery("SELECT nombre FROM editoriales ORDER BY nombre ASC", null);
        if(c.moveToFirst()){
            list = new ArrayList<String>();
            do {
                list.add(c.getString(0));
            } while(c.moveToNext());
        }
        return list;
    }

    public ArrayList<InfoComic> searchWidget(String query) {
        ArrayList<InfoComic> searchResults = new ArrayList<InfoComic>();
        ArrayList<InfoComic> comicsResult = this.searchWidgetComics(query);
        ArrayList<InfoComic> editorialesResult = this.searchWidgetEditorial(query);
        if(comicsResult!= null){
            searchResults.addAll(comicsResult);
        }
        if(editorialesResult != null){
            searchResults.addAll(editorialesResult);
        }
        return searchResults;
    }

    private ArrayList<InfoComic> searchWidgetComics(String query){
        ArrayList<InfoComic> comicArray = null;
        Cursor c = db.rawQuery("SELECT " + CAMPOS_COMICDTO + " FROM comics c, peridiocidad p, editoriales e WHERE c.titulo LIKE '%" + query + "%' AND p.id = c.peridiocidad AND e.content = c.content ORDER BY fecha ASC", null);
        if(c.moveToFirst()){
            comicArray = new ArrayList<InfoComic>();
            do{
                comicArray.add(crearComicDto(c));
            }while(c.moveToNext());
            c.close();
        }
        return comicArray;
    }
    private ArrayList<InfoComic> searchWidgetEditorial(String query){
        ArrayList<InfoComic> comicArray = null;
        Cursor c = db.rawQuery("SELECT "+CAMPOS_COMICDTO+" FROM comics c, peridiocidad p, editoriales e WHERE e.nombre LIKE '%"+query+"%' AND p.id = c.peridiocidad AND e.content = c.content ORDER BY fecha ASC", null);
        if(c.moveToFirst()){
            comicArray = new ArrayList<InfoComic>();
            do{
                comicArray.add(crearComicDto(c));
            }while(c.moveToNext());
            c.close();
        }
        return comicArray;
    }


    public void comprarComic(InfoComic infoComicDto){
        ComicDto comicDto = infoComicDto.getComicDto();
        PeridiocidadDto peridiocidadDto = infoComicDto.getPeridiocidadDto();
        int numero = comicDto.getNumero();

        numero++;
        if(consultarComicEnCarrito(comicDto.getId())){
            borrarCarritoCompras(comicDto.getId());
        }
        if(numero > comicDto.getNumeroFinal() && comicDto.getNumeroFinal() > 0){
            borrarComic(comicDto);
        }else{
            String fecha_add = MyDateUtils.agregarMes(peridiocidadDto.getTiempo(),comicDto.getFecha());
            actualizarFechaComic(fecha_add,numero,comicDto.getId());
        }
    }

    public void actualizarFechaComic(String fecha,int numero,int id){
        db.execSQL("UPDATE comics SET fecha='"+fecha+"', numero="+numero+" WHERE id='"+id+"'");
    }

    public void borrarComic(ComicDto comicDto){
        db.execSQL("DELETE FROM comics WHERE id = '"+comicDto.getId()+"'");
    }

    public void actualizarComic(ComicDto comicDto) {
            String numeroFinal = (comicDto.getNumeroFinal() == 0 ? "" : ",numeroFinal="+comicDto.getNumeroFinal());
            String strSql="UPDATE comics SET content="+comicDto.getEditorial()+", fecha='"+comicDto.getFecha()+"',numero="+comicDto.getNumero()+", precio="+comicDto.getPrecio()+",peridiocidad="+comicDto.getPeridiocidad()+",titulo = '"+comicDto.getTitulo()+"'"+numeroFinal+",enPublicacion="+comicDto.getEnPublicacion()+" WHERE id="+comicDto.getId();
            db.execSQL(strSql);
    }

    public ArrayList<InfoComic> consultarComics(ArrayList<Integer> selected) {
        String query="";
        for(Integer tmp : selected){
            query+=tmp.intValue()+",";
        }

        ArrayList<InfoComic> comicArray = null;
        Cursor c = db.rawQuery("SELECT "+CAMPOS_COMICDTO+" FROM comics c, peridiocidad p, editoriales e WHERE c.id IN ("+query.substring(0,query.length()-1)+") AND p.id = c.peridiocidad AND e.content = c.content ORDER BY fecha ASC", null);
        if(c.moveToFirst()){
            comicArray = new ArrayList<InfoComic>();
            do{
                comicArray.add(crearComicDto(c));
            }while(c.moveToNext());
            c.close();
        }
        return comicArray;
    }

    public ArrayList<ComicDto>respaldoComics(){
        String sql ="SELECT id,titulo,fecha,numero,precio,peridiocidad,content,numeroFinal,enPublicacion FROM comics";
        Cursor c = db.rawQuery(sql, null);
        ArrayList<ComicDto>listaComics=null;
        if(c.moveToFirst()){
            listaComics = new ArrayList<ComicDto>();
            do{
                listaComics.add(new ComicDto(c.getInt(0), c.getString(1), c.getString(2), c.getInt(3), c.getFloat(4), c.getInt(5), c.getInt(6),c.getInt(7), c.getInt(8)));
            }while(c.moveToNext());
        }
        c.close();
        return listaComics;
    }

    public ArrayList<EditorialDto> respaldoEditoriales(){
        ArrayList<EditorialDto>listaEditoriales = null;
        String sql ="SELECT nombre FROM editoriales";
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            listaEditoriales = new ArrayList<EditorialDto>();
            do{
                listaEditoriales.add(new EditorialDto(0, c.getString(0)));
            }while(c.moveToNext());
        }
        c.close();
        return listaEditoriales;
    }

    public ArrayList<PeridiocidadDto> respaldoPeridiocidad(){
        ArrayList<PeridiocidadDto> lista= null;
        String sql = "SELECT diasMeses,descripcion,tiempo FROM peridiocidad";
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            lista = new ArrayList<PeridiocidadDto>();
            do {
                lista.add(new PeridiocidadDto(0, c.getInt(0), c.getString(1), c.getInt(2)));
            }while(c.moveToNext());
        }
        c.close();
        return lista;
    }

    public ArrayList<PeridiocidadDto> obtenerListaPeridiocidad(){
        ArrayList<PeridiocidadDto> lista= null;
        String sql = "SELECT id,diasMeses,descripcion,tiempo FROM peridiocidad";
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            lista = new ArrayList<PeridiocidadDto>();
            do {
                lista.add(new PeridiocidadDto(c.getInt(0), c.getInt(1), c.getString(2), c.getInt(3)));
            }while(c.moveToNext());
        }
        c.close();
        return lista;
    }

    public ArrayList<InfoComic> consultarComicsFaltantes(String fechaActual,String anioActual){
        ArrayList<InfoComic> comicArray = null;
        String condition="fechaf <= date('now') AND ";
        String sql = "SELECT "+CAMPOS_COMICDTO+",substr(fecha,7,4) ||'-'|| substr(fecha,4,2) ||'-'||substr(fecha,1,2) as fechaf  FROM comics c, peridiocidad p, editoriales e WHERE "+condition+"p.id = c.peridiocidad AND e.content = c.content AND c.enPublicacion=1 ORDER BY fechaf DESC";
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            comicArray = new ArrayList<InfoComic>();
            do{
                comicArray.add(crearComicDto(c));
            }while(c.moveToNext());
        }
        c.close();
        return comicArray;
    }

    public void agregarAlCarrito(ComicDto comicDto){
        Cursor c = db.rawQuery("SELECT id FROM carritoCompras WHERE id = "+comicDto.getId(),null);
        if(!c.moveToNext()){
            String fecha = comicDto.getFecha().substring(6)+"-"+comicDto.getFecha().substring(3, 5)+"-"+comicDto.getFecha().substring(0,2);
            db.execSQL("INSERT INTO carritoCompras VALUES ("+comicDto.getId()+",'"+fecha+"')");
        }
        c.close();
    }

    public void borrarCarritoCompras(){
        db.execSQL("DELETE FROM carritoCompras ");
    }

    public void borrarCarritoCompras(int id){
        db.execSQL("DELETE FROM carritoCompras WHERE id ="+id);
    }

    public ArrayList<InfoComic> consultarCarrito(){
        ArrayList<InfoComic> carrito  = null;
        Cursor c = db.rawQuery("SELECT id,fecha FROM carritoCompras ORDER BY fecha",null);
        if(c.moveToFirst()){
            carrito  = new ArrayList<InfoComic>();
            do {
                carrito.add(consultarComicsId(c.getInt(0)));
            }while (c.moveToNext());
        }
        c.close();
        return carrito;
    }

    public boolean consultarComicEnCarrito(int id){
        boolean resultado;
        Cursor c = db.rawQuery("SELECT id,fecha FROM carritoCompras WHERE id ="+id,null);
        resultado = c.moveToFirst();
        c.close();
        return  resultado;
    }

    public void borrarPeriodicidad(int id){
        db.execSQL("DELETE FROM peridiocidad WHERE id =" + id);
    }

}

