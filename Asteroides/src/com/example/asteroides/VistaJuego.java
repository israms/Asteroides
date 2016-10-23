package com.example.asteroides;

import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class VistaJuego extends View {
    // //// ASTEROIDES //////
    private Vector<Asteroide> Asteroides; // Vector con los Asteroides
    private int numAsteroides= 4; // Número inicial de asteroides
    private int numFragmentos= 3; // Fragmentos en que se divide
    
 // //// NAVE //////
    private Nave transbordador;// Gráfico de la nave
    boolean disparo;
    boolean mueve;
    
 // //// MISIL //////

    
    private Vector<Misil> misiles;
    private static int NUM_MAX_MISILES = 5;
    private int misiles_activos = 0;
    
	 // //// THREAD Y TIEMPO //////
	 // Thread encargado de procesar el juego
	 private ThreadJuego thread = new ThreadJuego();
	 // Cada cuanto queremos procesar cambios (ms)
	 private static int PERIODO_PROCESO = 50;
	 // Cuando se realizó el último proceso
	 private long ultimoProceso = 0;
	 private Drawable drawableNave, drawableMisil;
	 
	////// MULTIMEDIA //////
	SoundPool soundPool;
	int idDisparo, idExplosion;
	
	private Drawable drawableAsteroide[]= new Drawable[3];
	
	// Puntuaciones
	private int puntuacion = 0;
	private Activity padre;
	
	boolean finPartida = false;
	boolean victoria = false;
	Typeface font;
	boolean iniciado = false;
	
	Paint pincel = new Paint();
	
    
    public VistaJuego(Context context, AttributeSet attrs) {
    	super(context, attrs);
       
        //drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        
        SharedPreferences pref = context.getSharedPreferences("com.example.asteroides_preferences", Context.MODE_PRIVATE);
        if (pref.getString("graficos", "1").equals("0")){
        	//drawableAsteroide = dibujaAsteroideRetro();
        	dibujaAsteroideRetro();
        	drawableNave = dibujaNaveRetro();
        	drawableMisil = dibujaMisilRetro();
        }else{
        	drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
        	drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
        	drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
        	//drawableAsteroide = context.getResources().getDrawable(R.drawable.asteroide1);
        	drawableNave = context.getResources().getDrawable(R.drawable.nave);
        	drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
        }
        
        Asteroides = new Vector<Asteroide>();
        transbordador = new Nave(this, drawableNave);
        //misil = new Misil(this, drawableMisil);
        misiles = new Vector<Misil>();
        
        for (int i = 0; i < numAsteroides; i++) {
        	Asteroide asteroide = new Asteroide(this, drawableAsteroide[0]);
            asteroide.setIncY(Math.random() * 4 - 2);
            asteroide.setIncX(Math.random() * 4 - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            Asteroides.add(asteroide);
        }
        
        // Carga de efectos de sonido
        soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);
        
        // Cargar fuente para texto fin de partida
        
        
        font = Typeface.createFromAsset(context.getAssets(),"fonts/digital_dreams.ttf"); 
        /*
        pincel.setColor(Color.rgb(231,73,73));
    	pincel.setTypeface(font);
    	pincel.setTextSize(35.0f);
    	pincel.setShadowLayer(1.5f, 1f, 1f, Color.rgb(230, 175, 175));
    	*/
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
    	super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
    	
        // Una vez que conocemos nuestro ancho y alto.
    	transbordador.setPosX((ancho/2)-(transbordador.getAncho()/2));
    	transbordador.setPosY((alto/2)-(transbordador.getAlto()/2));
    	
        for (Asteroide asteroide: Asteroides) {
        	do {
        		asteroide.setPosX(Math.random()*(ancho-asteroide.getAncho()));
        	    asteroide.setPosY(Math.random()*(alto-asteroide.getAlto()));
        	} while(asteroide.distancia(transbordador) < (ancho+alto)/5);
        }
        
        /*if(this.isEnabled()){
        	ultimoProceso = System.currentTimeMillis();  	
        	thread.start(); 
        }*/
    	ultimoProceso = System.currentTimeMillis();  	
    	thread.start(); 	
    }

    @Override
    synchronized protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	
        for (Grafico asteroide: Asteroides) {
        	asteroide.dibujaGrafico(canvas);
        }
        
        if(!finPartida){
        	transbordador.dibujaGrafico(canvas);
        } else {
        	muestraFinPartida(victoria,canvas);
        }
        
        for(Misil misil: misiles){
        	if(misil.isActivo()){
        		misil.dibujaGrafico(canvas);
        	}
        }
        
        /*
        if(!iniciado){
    		canvas.drawText("TOCA PARA INICIAR", 10,(this.getHeight()/2)-30, pincel);
    	}
    	*/
        /*if(misilActivo){
        	misil.dibujaGrafico(canvas);
        }*/
    }
    
    @Override
    public boolean onTouchEvent (MotionEvent event) {
    	super.onTouchEvent(event);
    	if(!finPartida){
	    	float x = event.getX();
	    	float y = event.getY();
	    	
	    	Rect r = transbordador.getDrawable().getBounds();
	    	
	        switch (event.getAction()) {
	       		case MotionEvent.ACTION_DOWN:
	       			/*if(!iniciado){
	       	        	ultimoProceso = System.currentTimeMillis();  	
	       	        	thread.start();
	       	        	iniciado = true;
	       	        }else{*/
		       			if(r.contains((int)x,(int)y)){
		       				//disparo = true;
		       				mueve = true;
		       				Log.d("AsteroidesLog", "Está contenido el toque");
		       			} else {
		       				disparo = true;
		       			}
	       	        //}
	       			Log.d("AsteroidesLog", "ACTION_DOWN");
	       			break;
	       		case MotionEvent.ACTION_MOVE:
	       			if(mueve){
	       				transbordador.mueveGrafico(x, y);
	           			Log.d("AsteroidesLog", "ACTION_MOVE");
	           			disparo = false;
	       			}
	       			
	       			break;
	       		case MotionEvent.ACTION_UP:
	       			mueve = false;
	       			Log.d("AsteroidesLog", "ACTION_UP");
	       			String msg = "false";
	       			if(disparo){
	       				msg = "true";
	       			}
	       			Log.d("AsteroidesLog", msg);
	       			//disparo = true;
		            if(disparo && (misiles_activos < NUM_MAX_MISILES)){
		            	Log.d("AsteroidesLog", "Disparando misil");
		            	activaMisil();
		            }
		            break;
	       }
    	}
       
       return true;
    }
    
    
    synchronized protected void actualizaFisica() {
    	Log.d("AsteroidesLog","ActualizaFisica");
        long ahora = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
              return;
        }
        // Para una ejecución en tiempo real calculamos retardo           
        double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
       
        for (Asteroide asteroide : Asteroides) {
              asteroide.mueveGrafico(retardo);
        }
        
        for(int i=0; i<misiles.size(); i++){
        	Misil aux = misiles.get(i);
        	
        	if(aux.isActivo()){
        		aux.mueveGrafico(retardo);
        		if(aux.getPosY() <= 0){
        			aux.setActivo(false);
        			misiles.remove(i);
        			misiles_activos--;
        		}else{
        			for (int j = 0; j < Asteroides.size(); j++){
        				if (aux.verificaColision(Asteroides.elementAt(j))){
        					aux.setActivo(false);
                			destruyeAsteroide(j);
                			destruyeMisil(i);
                            break;
                        }
        			}
        		}
        	}
        }
        
        for (Asteroide asteroide : Asteroides) {
            if (asteroide.verificaColision(transbordador)) {
            	soundPool.play(idExplosion, 1, 1, 0, 0, 2.0f);
            	transbordador.destruirNave();
            	salir();
            	finPartida = true;
            	victoria = false;
            }
        }
    }
    
    public void setPadre(Activity padre) {
        this.padre = padre;
    }
    
    private void muestraFinPartida(boolean victoria, Canvas canvas){
    	
    	String msg;
    	if(!victoria){
    		msg = "GAME OVER";
    	}else{
    		msg = "VICTORIA!";
    	}
    	
    	pincel.setColor(Color.rgb(231,73,73));
    	pincel.setTypeface(font);
    	int fontSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);
    	//pincel.setTextSize(40.0f);
    	pincel.setTextSize(fontSize);
    	Rect rect = new Rect();
    	pincel.getTextBounds(msg, 0, msg.length(), rect);
    	pincel.setShadowLayer(1.5f, 1f, 1f, Color.rgb(230, 175, 175));
    	canvas.drawText(msg, (this.getWidth()/2)-(rect.width()/2),(this.getHeight()/2)-(rect.height()/2), pincel);
    }
    
    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        //padre.finish();
        
    }
    
    private void dibujaAsteroideRetro(){
    	Path pathAsteroide = new Path();
	    pathAsteroide.moveTo((float) 0.3, (float) 0.0);
	    pathAsteroide.lineTo((float) 0.6, (float) 0.0);
	    pathAsteroide.lineTo((float) 0.6, (float) 0.3);
	    pathAsteroide.lineTo((float) 0.8, (float) 0.2);
	    pathAsteroide.lineTo((float) 1.0, (float) 0.4);
	    pathAsteroide.lineTo((float) 0.8, (float) 0.6);
	    pathAsteroide.lineTo((float) 0.9, (float) 0.9);
	    pathAsteroide.lineTo((float) 0.8, (float) 1.0);
	    pathAsteroide.lineTo((float) 0.4, (float) 1.0);
	    pathAsteroide.lineTo((float) 0.0, (float) 0.6);
	    pathAsteroide.lineTo((float) 0.0, (float) 0.2);
	    pathAsteroide.lineTo((float) 0.3, (float) 0.0);

	    for (int i=0; i<3; i++) {
    		ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 1, 1));
            dAsteroide.getPaint().setColor(Color.WHITE);
            dAsteroide.getPaint().setStyle(Style.STROKE);
            dAsteroide.setIntrinsicWidth(50 - i * 14);
            dAsteroide.setIntrinsicHeight(50 - i * 14);
            drawableAsteroide[i] = dAsteroide;
    	}
	    setBackgroundColor(Color.BLACK);
	    
	    //return dAsteroide;
	    
    }
    
    private ShapeDrawable dibujaNaveRetro(){
    	Path pathNave = new Path();
    	pathNave.moveTo((float) 0.5, (float) 0);
    	pathNave.lineTo((float) 0.0, (float) 1.0);
    	pathNave.lineTo((float) 1.0, (float) 1.0);
    	pathNave.lineTo((float) 0.5, (float) 0.0);
    	ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 1, 1));
	    dNave.getPaint().setColor(Color.WHITE);
	    dNave.getPaint().setStyle(Style.STROKE);
	    dNave.setIntrinsicWidth(25);
	    dNave.setIntrinsicHeight(15);
	    setBackgroundColor(Color.BLACK);
	    
	    return dNave;
    	
    }
    
    private ShapeDrawable dibujaMisilRetro(){
    	ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
    	dMisil.getPaint().setColor(Color.WHITE);
    	dMisil.getPaint().setStyle(Style.STROKE);
    	dMisil.setIntrinsicWidth(3);
    	dMisil.setIntrinsicHeight(15);
    	
    	return dMisil;
    }
    
    private void activaMisil(){      
        Misil aux = new Misil(this, drawableMisil);
        aux.setPosX(transbordador.getPosX() + transbordador.getAncho()/2-aux.getAncho()/2);
        aux.setPosY(transbordador.getPosY() + transbordador.getAlto()/2-aux.getAlto()/2);
        aux.setActivo(true);
        misiles.add(aux);
        soundPool.play(idDisparo, 1, 1, 1, 0, 2.0f);
        misiles_activos++;
    }
    
    private void destruyeAsteroide(int i) {
    	int tam;
    	if(Asteroides.get(i).getDrawable()!=drawableAsteroide[2]){
    	   if(Asteroides.get(i).getDrawable()==drawableAsteroide[1]){
    	          tam=2;
    	   } else {
    	          tam=1;
    	   }
    	   for(int n=0;n<numFragmentos;n++){
    	          Asteroide asteroide = new Asteroide(this,drawableAsteroide[tam]);
    	          asteroide.setPosX(Asteroides.get(i).getPosX());
    	          asteroide.setPosY(Asteroides.get(i).getPosY());
    	          asteroide.setIncX(Math.random()*7-2-tam);
    	          asteroide.setIncY(Math.random()*7-2-tam);
    	          asteroide.setAngulo((int)(Math.random()*360));
    	          asteroide.setRotacion((int)(Math.random()*8-4));
    	          Asteroides.add(asteroide);
    	   }     
    	}
        Asteroides.remove(i);
        soundPool.play(idExplosion, 1, 1, 0, 0, 2.0f);
        puntuacion += 1125;
        
        // Comprobar si la partida ha terminado
        if (Asteroides.isEmpty()) {
        	finPartida = true;
        	victoria = true;
            salir();
        }
    }
    
    private void destruyeMisil(int i){
    	misiles.remove(i);
    	misiles_activos--;
    }
    
    public ThreadJuego getThread() {
    	return thread;
    }
    
    class ThreadJuego extends Thread {
    	private boolean pausa, corriendo;
    	
    	public synchronized void pausar() {
    		pausa = true;
    	}
    	 
    	public synchronized void reanudar() {
    		pausa = false;
    	    notify();
    	}
    	 
    	public void detener() {
    		corriendo = false;
    	    if (pausa) reanudar();
    	}
    	  
    	@Override
    	public void run() {
    		corriendo = true;
    	    while (corriendo) {
    	    	actualizaFisica();
    	        synchronized (this) {
    	        	while (pausa) {
    	        		try {
    	                    wait();
    	                } catch (Exception e) {
    	                	//...
    	                }
    	            }
    	       }
    	   }
    	}
    }
}
