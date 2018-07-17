package com.tacuba.comicsmanager.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.tacuba.comicsmanager.Constantes;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public MySQLiteHelper(Context contexto, String nombre,CursorFactory factory, int version) {
		super(contexto, nombre, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		crearTablas(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
		/*
		db.execSQL("DROP TABLE IF EXISTS carritoCompras");
		db.execSQL(Constantes.sqlCreateShoppingCart);
		*/
	}
	
	private void crearTablas(SQLiteDatabase db){
		db.execSQL(Constantes.sqlCreatComicsTable);
		db.execSQL(Constantes.sqlCreateEditorialTable);
		db.execSQL(Constantes.sqlCreatePeridiocidadTable);
        db.execSQL(Constantes.sqlCreateShoppingCart);
	}
}
