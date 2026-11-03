package co.edu.unilibre.dto;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;

/**
 * Partida
 */
@Getter
@Setter
public class Partida {

    private int id;
	private Casilla[][] tablero;
	private Dificultad dificultad;
	private Usuario usuario;
	private Duration tiempo;
	private Resultado resultado;
	private Tema tema;

	public Partida(Dificultad dificultad, Usuario usuario, Tema tema) {
        this.tablero = new Casilla[dificultad.getFilas()][dificultad.getColumnas()];
        this.dificultad = dificultad;
        this.usuario = usuario;
        this.tema = tema;
        this.resultado = Resultado.EN_PROGRESO;

        for (int i = 0; i < dificultad.getFilas(); i++) {
            for (int j = 0; j < dificultad.getColumnas(); j++) {
                tablero[i][j] = new Casilla(i, j);
            }
        }
	}

	public Partida(Dificultad dificultad, Usuario usuario, Tema tema, Duration tiempo, Resultado resultado) {
		this.dificultad = dificultad;
		this.usuario = usuario;
		this.tema = tema;
		this.tiempo = tiempo;
		this.resultado = resultado;
	}

}
