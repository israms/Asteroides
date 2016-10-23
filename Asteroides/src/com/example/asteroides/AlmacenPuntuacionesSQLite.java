package com.example.asteroides;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlmacenPuntuacionesSQLite extends SQLiteOpenHelper implements AlmacenPuntuaciones {
	
	//Métodos de SQLiteOpenHelper
    public AlmacenPuntuacionesSQLite(Context context) {
          super(context, "puntuaciones", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL("CREATE TABLE puntuaciones ("+"_id INTEGER PRIMARY KEY AUTOINCREMENT, "+"puntos INTEGER, nombre TEXT, fecha LONG)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// En caso de una nueva versión habría que actualizar las tablas
    }

    //Métodos de AlmacenPuntuaciones
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
    	SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO puntuaciones VALUES ( null, "+puntos+", '"+nombre+"', "+fecha+")");
    }
   
    public Vector<String> listaPuntuaciones(int cantidad) {
    	Vector<String> result = new Vector<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT puntos, nombre, fecha FROM " + "puntuaciones ORDER BY puntos DESC LIMIT " +cantidad, null);
        while (cursor.moveToNext()){
        	result.add(cursor.getString(1)+" " +cursor.getInt(0)+" "+longToDate(cursor.getLong(2)));
        }
        cursor.close();
        return result;
    }
    
    private String longToDate(long valor){
        Date date = new Date(valor);
        SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
        String dateText = df2.format(date);
        
        return dateText;
    }
}
