package com.example.asteroides;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

abstract class Grafico {
    protected Drawable drawable;   //Imagen que dibujaremos
    protected double posX, posY;   //Posición
    protected int ancho, alto;     //Dimensiones de la imagen
    protected int radioColision;   //Para determinar colisión
     //Donde dibujamos el gráfico (usada en view.ivalidate)
    protected View view;
     // Para determinar el espacio a borrar (view.ivalidate)
   
    public Grafico(View view, Drawable drawable){
          this.view = view;
          this.drawable = drawable;
          ancho = drawable.getIntrinsicWidth();  
          alto = drawable.getIntrinsicHeight();
          radioColision = (alto+ancho)/4;
    }
    
    abstract public void dibujaGrafico(Canvas canvas);
    
    public void mueveGrafico(double factor){
    	
    }
    
    public void mueveGrafico(float x, float y){
    	
    }

    public double distancia(Grafico g) {
          return Math.hypot(posX-g.posX, posY-g.posY);
    }

    public boolean verificaColision(Grafico g) {
          return(distancia(g) < (radioColision+g.radioColision));
    }

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public int getRadioColision() {
		return radioColision;
	}

	public void setRadioColision(int radioColision) {
		this.radioColision = radioColision;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}  
}