package com.example.asteroides;

import java.util.Vector;

public class AlmacenPuntuacionesArray implements AlmacenPuntuaciones {
	private Vector<String> puntuaciones;
	
	// Constrcutor por defecto de la clase
	public AlmacenPuntuacionesArray() {
         puntuaciones= new Vector<String>();
         puntuaciones.add("123000 Luke Skywalker");
         puntuaciones.add("111000 Han Solo");
         puntuaciones.add("011000 Darth Vader");
    }
	
    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
         puntuaciones.add(0, puntos + " " + nombre);
    }
    
    @Override
    public Vector<String> listaPuntuaciones(int cantidad) {
         return  puntuaciones;
    }
}
