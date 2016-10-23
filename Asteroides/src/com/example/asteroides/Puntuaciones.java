package com.example.asteroides;

import android.app.ListActivity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;
//import android.widget.ArrayAdapter;

public class Puntuaciones extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.puntuaciones);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/heavy_data.ttf");
        TextView titulo = (TextView)findViewById(R.id.tituloPuntuaciones);
        titulo.setTypeface(font);
        setListAdapter(new MiAdaptador(this, Asteroides.almacen.listaPuntuaciones(20)));
        //setListAdapter(new ArrayAdapter<String>(this,R.layout.elemento_lista,R.id.titulo,Asteroides.almacen.listaPuntuaciones(10)));
    }
}
