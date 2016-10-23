package com.example.asteroides;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public class Misil extends Grafico {
	private boolean activo;
	public static final int MAX_VELOCIDAD = 20;
	
	public Misil(View view, Drawable drawable){
		super(view,drawable);
		activo = true;
	}
	
	public void dibujaGrafico(Canvas canvas){
		canvas.save();
        int x=(int) (posX+ancho/2);
        int y=(int) (posY+alto/2);
        //canvas.rotate((float) angulo,(float) x,(float) y);
        //canvas.translate((float)x, (float)y);
        drawable.setBounds((int)posX, (int)posY,(int)posX+ancho, (int)posY+alto);
        drawable.draw(canvas);
        canvas.restore();
        //int rInval = (int) Math.hypot(ancho,alto)/2 + MAX_VELOCIDAD;
        //view.invalidate(x-rInval, y-rInval, x+rInval, y+rInval);
        //view.invalidate();
        Rect r = this.drawable.getBounds();
        view.invalidate(r);
	}
    
    public void mueveGrafico(double factor){
    	posY = (posY-8)*factor;
    }

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
