package com.example.asteroides;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Asteroides extends Activity {
	/* ---> Declaración de variables */
	private MediaPlayer mp;
	private boolean audio;
	private int puntuacion;
	private String jugador;
	
	
	// Almacenar puntuaciones array no persistente
    //public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesArray();
    
    // Almacenar las particiones en una BD con SQLite
    public static AlmacenPuntuacionesSQLite almacen;
    /* Fin declaración de variables <--- */
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		TextView titulo = (TextView)findViewById(R.id.titulo);
		// Añadir fuente personalizada al titulo 
	    Typeface font = Typeface.createFromAsset(getAssets(), "fonts/heavy_data.ttf");
	    titulo.setTypeface(font);
	    
	    Button botonJugar = (Button)findViewById(R.id.botonJugar);
	    Button botonConfig = (Button)findViewById(R.id.botonConfigurar);
	    Button botonAcerca = (Button)findViewById(R.id.botonAcercaDe);
	    Button botonPuntos = (Button)findViewById(R.id.botonPuntuaciones);
	    Button botonSalir = (Button)findViewById(R.id.botonSalir);
	    
	    // Animar titulo y botones con un fadeIn
	    Animation animacion = AnimationUtils.loadAnimation(this,R.anim.anim_titulo);
	    Animation animacion2 = AnimationUtils.loadAnimation(this,R.anim.anim_botones);
	    titulo.startAnimation(animacion);
	    botonJugar.startAnimation(animacion2);
	    botonConfig.startAnimation(animacion2);
	    botonAcerca.startAnimation(animacion2);
	    botonPuntos.startAnimation(animacion2);
	    botonSalir.startAnimation(animacion2);
	        
	    // Añadir fuente personalizada a los botones
	    font = Typeface.createFromAsset(getAssets(), "fonts/digital_dreams.ttf");
	    botonJugar.setTypeface(font);
	    botonConfig.setTypeface(font);
	    botonAcerca.setTypeface(font);
	    botonPuntos.setTypeface(font);
	    botonSalir.setTypeface(font);
	    
	    // Escuchador de eventos para el boton Acerca de
	    botonAcerca.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view) {
	    		audio = true;
	    		lanzarAcercaDe(null);
	    	}
	    });
	    
	    // Escuchador de eventos para el boton Configurar
	    botonConfig.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view) {
	    		audio = true;
	    		lanzarPreferencias(null);
	    	}
	    });
	    
	    // Escuchador de eventos para el boton Puntuaciones
	    botonPuntos.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view) {
	    		audio = true;
	    		lanzarPuntuaciones(null);
	    	}
	    });
	    
	    // Escuchador de eventos para el boton Salir
	    botonSalir.setOnClickListener(new OnClickListener(){
	    	public void onClick(View view) {
	    		salirApp(null);
	    	}
	    });
	    
	    // Ejecutar música
	    mp = MediaPlayer.create(this, R.raw.audio);
	    mp.setLooping(true);
	    mp.start();
	    
	    almacen = new AlmacenPuntuacionesSQLite(this);
	    puntuacion = 0;
	    jugador = "Anónimo";
	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    return true; /** true -> el menú ya está visible */
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
			case R.id.acercaDe:
				lanzarAcercaDe(null);
	            break;
			case R.id.config:
                lanzarPreferencias(null);
                break;
	    }
	    
		return true; /** true -> consumimos el item, no se propaga*/
	}
	
	@Override
	protected void onPause(){
		//Toast.makeText(this, "Menú onPause", Toast.LENGTH_SHORT).show();
		super.onPause();
		if(!audio){
			mp.pause();		
		}
		audio = false;
	}
	
	@Override
	protected void onResume(){
		//Toast.makeText(this, "Menu onResume", Toast.LENGTH_SHORT).show();
		super.onResume();
		//mp.pause();
		mp.start();
	}
	
	@Override 
	protected void onActivityResult (int requestCode,int resultCode, Intent data){
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode==1234 && resultCode==RESULT_OK && data!=null) {
	        puntuacion = data.getExtras().getInt("puntuacion");
	        //String nombre;        
	        //String jugadores[] = {"Luke Skywalker","Sheldon Cooper","Han Solo","Darth Vader"};
	        // Mejor leerlo desde un Dialog o una nueva actividad AlertDialog.Builder        
	        //nombre = jugadores[(int)(Math.random() * ((3) + 1))];
	        
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Introduce tu nombre");
	        
	        // Set up the input
	        final EditText input = new EditText(this);
	        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
	        input.setInputType(InputType.TYPE_CLASS_TEXT);
	        builder.setView(input);

	        // Set up the buttons
	        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
	        	@Override
	        	public void onClick(DialogInterface dialog, int which) {
	        		jugador = input.getText().toString();
	        		almacen.guardarPuntuacion(puntuacion,jugador,System.currentTimeMillis());
	        		lanzarPuntuaciones(null);
	        	}
	        });
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	dialog.cancel();
		        }
	        });

	        builder.show();
	       
	      	//almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
	        //lanzarPuntuaciones(null);
	     }
	}
	
	public void lanzarJuego(View view){
		Intent i = new Intent(this, Juego.class);
		startActivityForResult(i, 1234);
	}
	
	public void lanzarAcercaDe(View view){
		Intent i = new Intent(this, AcercaDe.class);
		startActivity(i);
	}
	
	public void lanzarPreferencias(View view){
		Intent i = new Intent(this, Preferencias.class);
		startActivity(i);
	}
	
	public void lanzarPuntuaciones(View view) {
		Intent i = new Intent(this, Puntuaciones.class);
		startActivity(i);
	}
	
	public void salirApp(View view){
		finish();
	}

}
