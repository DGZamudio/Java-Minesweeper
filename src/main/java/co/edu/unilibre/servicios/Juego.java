package co.edu.unilibre.servicios;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Partida;

/**
 * Juego
 */
public interface Juego {

	public void iniciarJuego(Partida partida);
	public void descubrirCasilla(Casilla casilla);
	public void hacerChording(Casilla casilla);
	public void alternarBandera(Casilla casilla);

}
