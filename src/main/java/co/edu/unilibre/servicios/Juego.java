package co.edu.unilibre.servicios;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Resultado;

/**
 * Juego
 */
public interface Juego {

	public void iniciarJuego(Casilla casilla);
	public void terminarJuego(Resultado resultado);
	public void descubrirCasilla(Casilla casilla);
	public void hacerChording(Casilla casilla);
	public void alternarBandera(Casilla casilla);

}
