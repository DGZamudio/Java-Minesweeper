package co.edu.unilibre.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Dificultad
 */
@Getter
@Setter
public class Dificultad {

    private int id;
    private String nombre;
	private int minas;
	private int columnas;
	private int filas;

	public Dificultad(String nombre, int minas, int columnas, int filas) {
	    this.nombre = nombre;
		this.minas = minas;
		this.columnas = columnas;
		this.filas = filas;
	}

}
