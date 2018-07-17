package com.tacuba.comicsmanager;

/**
 * Created by mendoedg on 23/02/2015.
 */
public class Constantes {
    public static final String DB_NAME = "DBComicsManager";
    public static final int VERSION_DB = 4;
    public static final String FORMATO_FECHA_MES_ANIO = "MM/yyyy";
    public static final String FORMATO_FECHA_ACTUAL = "dd/MM/yyyy";
    public final static String TITULOS_ARG="titulos";
    public final static String FORMATO_MES="MM";
    public final static String FORMATO_DIA="dd";
    public final static String FORMATO_ANIO="yyyy";
    public final static int AGREGAR_COMIC=0;
    public final static int AGREGAR_EDITORIAL=1;
    public final static String LISTA_COMICS_SCAN="lista_comics_scan";
    public final static String CVECOMIC="cveComic";
    public final static String EDITORIAL = "content";
    public static final String TITULO = "id";
    public static final String PERIDIOCIDAD = "peridiocidad";
    public static final String FECHA = "fecha";
    public static final String NUMERO = "numero";
    public static final String PRECIO = "precio";
    public static final String NUMERABLE = "numerable";
    public static final String ACTUALIZAR = "actualizar";
    public static final String AGREGAR_CAL = "agregar_calendario";
    public static final String DIRECTORIO="DIRECTORIO";
    public static final String GET_PATH="GetPath";
    public static final String GET_FILENAME="GetFileName";
    public static final String sqlCreatComicsTable = "CREATE TABLE comics " +
            "(id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "titulo VARCHAR(100) NOT NULL," +
            "fecha DATE  NOT NULL," +
            "numero INTEGER  NOT NULL," +
            "precio FLOAT  NOT NULL," +
            "peridiocidad INTEGER NOT NULL,"+
            "content INTEGER NOT NULL,"+
            "numeroFinal INTEGER NULL," +
            "enPublicacion INTEGER DEFAULT 1)";
    public static final String sqlCreateEditorialTable = "CREATE TABLE editoriales (" +
            "content INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "nombre VARCHAR(100)  NOT NULL)";
    public static final String sqlCreatePeridiocidadTable = "CREATE TABLE peridiocidad " +
            "(id INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "diasMeses INTEGER NOT NULL,"+//0 dias, 1 meses
            "descripcion VARCHAR(50) NULL," +
            "tiempo INTEGER  NULL)";
    public static final String sqlCreateShoppingCart = "CREATE TABLE carritoCompras " +
            "(id INTEGER  NOT NULL," +
            " fecha DATETIME DEFAULT CURRENT_DATE)";


    public final static int  PENDIENTES =1;
    public final static int  CARRITO_COMPRAS =2;
}
