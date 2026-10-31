package co.edu.unilibre.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Casilla
 */
@Getter
@Setter
public class Casilla {

	private int x;
	private int y;
	private int minasAdyacentes;
	private boolean descubierta;
	private boolean bandera;
	private boolean mina;

	public Casilla(int x, int y) {
	    this.x = x;
		this.y = y;
	}

}
