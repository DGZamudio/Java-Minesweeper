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

	public Partida(Casilla[][] tablero, Dificultad dificultad, Usuario usuario, Tema tema) {
        this.tablero = tablero;
        this.dificultad = dificultad;
        this.usuario = usuario;
        this.tema = tema;
        this.resultado = Resultado.EN_PROGRESO;
	}

	public Partida(Casilla[][] tablero, Dificultad dificultad, Usuario usuario, Tema tema, Duration tiempo, Resultado resultado) {
	    this.tablero = tablero;
		this.dificultad = dificultad;
		this.usuario = usuario;
		this.tema = tema;
		this.tiempo = tiempo;
		this.resultado = resultado;
	}

}
