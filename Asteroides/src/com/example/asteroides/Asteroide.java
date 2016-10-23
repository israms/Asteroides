package com.example.asteroides;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Asteroide extends Grafico {
	private double incX, incY;   //Velocidad desplazamiento
    private int angulo, rotacion;	//Ángulo y velocidad rotación
    public static final int MAX_VELOCIDAD = 20;
	
	public Asteroide(View view, Drawable drawable){
		super(view, drawable);
    }
	
	public void dibujaGrafico(Canvas canvas){
    	canvas.save();
        int x=(int) (posX+ancho/2);
        int y=(int) (posY+alto/2);
        canvas.rotate((float) angulo,(float) x,(float) y);
        drawable.setBounds((int)posX, (int)posY,(int)posX+ancho, (int)posY+alto);
        drawable.draw(canvas);
        canvas.restore();
        int rInval = (int) Math.hypot(ancho,alto)/2 + MAX_VELOCIDAD;
        view.invalidate(x-rInval, y-rInval, x+rInval, y+rInval);
        //view.invalidate();
    }
	
	public void mueveGrafico(double factor){
		posX+=incX * factor;
        // Si salimos de la pantalla, corregimos posición
        if(posX<-ancho/2) {posX=view.getWidth()-ancho/2;}
        if(posX>view.getWidth()-ancho/2) {posX=-ancho/2;}
        
        posY+=incY * factor;
        if(posY<-alto/2) {posY=view.getHeight()-alto/2;}
        if(posY>view.getHeight()-alto/2) {posY=-alto/2;}
        
        angulo += rotacion * factor; //Actualizamos ángulo
  }
	
	public double getIncX() {
		return incX;
	}

	public void setIncX(double incX) {
		this.incX = incX;
	}

	public double getIncY() {
		return incY;
	}

	public void setIncY(double incY) {
		this.incY = incY;
	}

	public int getAngulo() {
		return angulo;
	}

	public void setAngulo(int angulo) {
		this.angulo = angulo;
	}

	public int getRotacion() {
		return rotacion;
	}

	public void setRotacion(int rotacion) {
		this.rotacion = rotacion;
	}
	
	public static int getMaxVelocidad() {
		return MAX_VELOCIDAD;
	}

}
